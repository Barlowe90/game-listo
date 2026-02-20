package com.gamelist.catalogo.infrastructure.in.igdb;

import com.gamelist.catalogo.application.dto.results.IgdbGameDTO;
import com.gamelist.catalogo.application.dto.results.IgdbPlatformDTO;
import com.gamelist.catalogo.domain.repositories.IIgdbClientPort;
import com.gamelist.catalogo.shared.config.IgdbProperties;
import com.gamelist.catalogo.infrastructure.in.igdb.dto.IgdbGameResponseDto;
import com.gamelist.catalogo.infrastructure.in.igdb.dto.IgdbPlatformResponseDto;
import com.gamelist.catalogo.infrastructure.in.igdb.dto.IgdbNameDto;
import com.gamelist.catalogo.infrastructure.in.igdb.dto.IgdbIdDto;
import com.gamelist.catalogo.infrastructure.in.igdb.dto.IgdbExternalGameDto;
import com.gamelist.catalogo.infrastructure.in.igdb.dto.IgdbInvolvedCompanyDto;
import com.gamelist.catalogo.infrastructure.in.igdb.dto.IgdbScreenshotResponseDto;
import com.gamelist.catalogo.infrastructure.in.igdb.dto.IgdbVideoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/** Adapter HTTP para comunicación con la API de IGDB. */
@Component
@RequiredArgsConstructor
@Slf4j
public class IgdbHttpAdapter implements IIgdbClientPort {

  private final WebClient igdbWebClient;
  private final IgdbQueryBuilder queryBuilder;
  private final IgdbRateLimitHandler rateLimitHandler;
  private final IgdbProperties igdbProperties;

  @Override
  public List<IgdbGameDTO> fetchGamesBatch(Long afterId, int limit) {
    log.info("Obteniendo batch de juegos desde IGDB (afterId: {}, limit: {})", afterId, limit);
    String query = queryBuilder.buildGamesQuery(afterId, limit);
    log.debug("Query IGDB: {}", query);

    return rateLimitHandler.executeWithRetry(
        () -> {
          List<IgdbGameResponseDto> response =
              igdbWebClient
                  .post()
                  .uri("/games")
                  .header("Authorization", "Bearer " + igdbProperties.getAccessToken())
                  .bodyValue(query)
                  .retrieve()
                  .bodyToMono(new ParameterizedTypeReference<List<IgdbGameResponseDto>>() {})
                  .block();

          if (response == null) {
            log.warn("Respuesta nula de IGDB para juegos");
            return List.of();
          }
          log.info("Obtenidos {} juegos desde IGDB", response.size());
          return response.stream().map(this::convertBatchGameToDTO).toList();
        });
  }

  @Override
  public List<IgdbPlatformDTO> fetchPlatforms() {
    log.info("Obteniendo plataformas desde IGDB");
    String query = queryBuilder.buildPlatformsQuery();

    return rateLimitHandler.executeWithRetry(
        () -> {
          List<IgdbPlatformResponseDto> response =
              igdbWebClient
                  .post()
                  .uri("/platforms")
                  .header("Authorization", "Bearer " + igdbProperties.getAccessToken())
                  .bodyValue(query)
                  .retrieve()
                  .bodyToMono(new ParameterizedTypeReference<List<IgdbPlatformResponseDto>>() {})
                  .block();

          if (response == null) {
            log.warn("Respuesta nula de IGDB para plataformas");
            return List.of();
          }
          log.info("Obtenidas {} plataformas desde IGDB", response.size());
          return response.stream()
              .map(r -> new IgdbPlatformDTO(r.id(), r.name(), r.abbreviation()))
              .toList();
        });
  }

  // ── Conversión batch ──────────────────────────────────────────────────────────

  private IgdbGameDTO convertBatchGameToDTO(IgdbGameResponseDto r) {
    log.debug("Convirtiendo juego IGDB batch: id={}, name={}", r.id(), r.name());
    return new IgdbGameDTO(
        r.id(),
        r.name(),
        r.summary(),
        r.cover() != null ? r.cover().getFullUrl() : null,
        extractNames(r.platforms()),
        r.gameType() != null ? r.gameType().type() : null,
        r.gameStatus() != null ? r.gameStatus().status() : null,
        extractNames(r.alternativeNames()),
        extractIds(r.dlcs()),
        extractIds(r.expandedGames()),
        extractIds(r.expansions()),
        extractUrls(r.externalGames()),
        extractNames(r.franchises()),
        extractNames(r.gameModes()),
        extractNames(r.genres()),
        extractCompanies(r.involvedCompanies()),
        extractNames(r.keywords()),
        extractIds(r.multiplayerModes()),
        r.parentGame() != null ? r.parentGame().id() : null,
        extractNames(r.playerPerspectives()),
        extractIds(r.remakes()),
        extractIds(r.remasters()),
        extractIds(r.similarGames()),
        extractNames(r.themes()),
        extractScreenshotUrls(r.screenshots()),
        extractVideoUrls(r.videos()));
  }

  // ── Helpers ───────────────────────────────────────────────────────────────────

  private List<String> extractNames(List<IgdbNameDto> dtos) {
    if (dtos == null) return List.of();
    return dtos.stream().map(IgdbNameDto::name).toList();
  }

  private List<Long> extractIds(List<IgdbIdDto> dtos) {
    if (dtos == null) return List.of();
    return dtos.stream().map(IgdbIdDto::id).filter(java.util.Objects::nonNull).toList();
  }

  private List<String> extractUrls(List<IgdbExternalGameDto> dtos) {
    if (dtos == null) return List.of();
    return dtos.stream().map(IgdbExternalGameDto::url).filter(java.util.Objects::nonNull).toList();
  }

  private List<String> extractCompanies(List<IgdbInvolvedCompanyDto> dtos) {
    if (dtos == null) return List.of();
    return dtos.stream()
        .map(ic -> ic.company() != null ? ic.company().name() : null)
        .filter(java.util.Objects::nonNull)
        .toList();
  }

  private List<String> extractScreenshotUrls(List<IgdbScreenshotResponseDto> dtos) {
    if (dtos == null) return List.of();
    return dtos.stream()
        .map(IgdbScreenshotResponseDto::getFullUrl)
        .filter(java.util.Objects::nonNull)
        .toList();
  }

  private List<String> extractVideoUrls(List<IgdbVideoResponseDto> dtos) {
    if (dtos == null) return List.of();
    return dtos.stream()
        .map(IgdbVideoResponseDto::getYouTubeUrl)
        .filter(java.util.Objects::nonNull)
        .toList();
  }
}
