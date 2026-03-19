package com.gamelisto.catalogo.infrastructure.out.persistence.postgres;

import com.gamelisto.catalogo.domain.Game;
import com.gamelisto.catalogo.domain.GameCardSummary;
import com.gamelisto.catalogo.domain.GameId;
import com.gamelisto.catalogo.domain.GameRepositorio;
import com.gamelisto.catalogo.domain.PageResult;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GameRepositorioPostgre implements GameRepositorio {

  private static final Sort DEFAULT_SORT =
      Sort.by(Sort.Order.asc("name").ignoreCase(), Sort.Order.asc("id"));

  private final GameJpaRepository jpaRepository;
  private final GameMapper mapper;
  private final NamedParameterJdbcTemplate jdbcTemplate;

  @Override
  @Transactional
  public Game save(Game game) {
    GameEntity entity = mapper.toEntity(game);
    GameEntity savedEntity = jpaRepository.save(entity);
    return mapper.toDomain(savedEntity);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Game> findById(GameId id) {
    return jpaRepository.findById(id.value()).map(mapper::toDomain);
  }

  @Override
  @Transactional(readOnly = true)
  public PageResult<Game> findAll(int page, int size, List<String> platforms) {
    Pageable pageable = PageRequest.of(page, size, DEFAULT_SORT);
    Page<GameEntity> gamePage =
        platforms == null || platforms.isEmpty()
            ? jpaRepository.findAll(pageable)
            : jpaRepository.findPageByPlatforms(platforms, pageable);

    return new PageResult<>(
        gamePage.getContent().stream().map(mapper::toDomain).toList(),
        gamePage.getNumber(),
        gamePage.getSize(),
        gamePage.getTotalElements(),
        gamePage.getTotalPages());
  }

  @Override
  @Transactional(readOnly = true)
  public PageResult<GameCardSummary> findSummaries(int page, int size, List<String> platforms) {
    // The lightweight summary path uses JDBC directly, so we flush any pending JPA writes
    // first to keep both access strategies consistent within the same transaction.
    jpaRepository.flush();

    int safePage = Math.max(page, 0);
    int safeSize = Math.max(size, 1);
    boolean hasPlatformFilters = platforms != null && !platforms.isEmpty();
    String whereClause =
        hasPlatformFilters
            ? """
              where exists (
                select 1
                from game_platforms filter_platform
                where filter_platform.game_id = g.id
                  and lower(filter_platform.platform_name) in (:platforms)
              )
              """
            : "";

    MapSqlParameterSource pageParams =
        new MapSqlParameterSource()
            .addValue("limit", safeSize)
            .addValue("offset", safePage * (long) safeSize);

    if (hasPlatformFilters) {
      pageParams.addValue("platforms", platforms);
    }

    List<GameCardBaseRow> rows =
        jdbcTemplate.query(
            ("""
            select g.id, g.name, g.cover_url
            from games g
            %s
            order by lower(g.name), g.id
            limit :limit offset :offset
            """)
                .formatted(whereClause),
            pageParams,
            (rs, rowNum) ->
                new GameCardBaseRow(
                    rs.getLong("id"), rs.getString("name"), rs.getString("cover_url")));

    Long totalElements =
        jdbcTemplate.queryForObject(
            ("""
            select count(*)
            from games g
            %s
            """)
                .formatted(whereClause),
            pageParams,
            Long.class);

    if (rows.isEmpty()) {
      long safeTotalElements = totalElements != null ? totalElements : 0L;
      return new PageResult<>(
          List.of(),
          safePage,
          safeSize,
          safeTotalElements,
          calculateTotalPages(safeTotalElements, safeSize));
    }

    List<Long> gameIds = rows.stream().map(GameCardBaseRow::id).toList();
    Map<Long, List<String>> platformsByGameId =
        loadTextCollectionByGameId("game_platforms", "platform_name", gameIds);
    Map<Long, List<String>> gameModesByGameId =
        loadTextCollectionByGameId("game_modes", "mode_name", gameIds);

    List<GameCardSummary> content =
        rows.stream()
            .map(
                row ->
                    new GameCardSummary(
                        row.id(),
                        row.name(),
                        row.coverUrl(),
                        platformsByGameId.getOrDefault(row.id(), List.of()),
                        gameModesByGameId.getOrDefault(row.id(), List.of())))
            .toList();

    long safeTotalElements = totalElements != null ? totalElements : 0L;
    return new PageResult<>(
        content, safePage, safeSize, safeTotalElements, calculateTotalPages(safeTotalElements, safeSize));
  }

  @Override
  @Transactional(readOnly = true)
  public long findMaxId() {
    return jpaRepository.findMaxId();
  }

  private Map<Long, List<String>> loadTextCollectionByGameId(
      String tableName, String valueColumnName, List<Long> gameIds) {
    MapSqlParameterSource collectionParams = new MapSqlParameterSource().addValue("gameIds", gameIds);
    String sql =
        """
        select game_id, %s as value
        from %s
        where game_id in (:gameIds)
        order by game_id, lower(%s), %s
        """
            .formatted(valueColumnName, tableName, valueColumnName, valueColumnName);

    List<GameTextCollectionRow> rows =
        jdbcTemplate.query(
            sql,
            collectionParams,
            (rs, rowNum) -> new GameTextCollectionRow(rs.getLong("game_id"), rs.getString("value")));

    Map<Long, List<String>> valuesByGameId = new LinkedHashMap<>();
    for (GameTextCollectionRow row : rows) {
      if (row.value() == null || row.value().isBlank()) {
        continue;
      }
      valuesByGameId.computeIfAbsent(row.gameId(), ignored -> new ArrayList<>()).add(row.value());
    }
    return valuesByGameId;
  }

  private int calculateTotalPages(long totalElements, int size) {
    if (totalElements <= 0 || size <= 0) {
      return 0;
    }
    return Math.toIntExact((totalElements + size - 1) / size);
  }

  private record GameCardBaseRow(Long id, String name, String coverUrl) {}

  private record GameTextCollectionRow(Long gameId, String value) {}
}
