package com.gamelisto.catalogo.infrastructure.in.igdb;

import com.gamelisto.catalogo.application.dto.in.IgdbGameDTO;
import com.gamelisto.catalogo.application.dto.in.IgdbPlatformDTO;
import com.gamelisto.catalogo.domain.IgdbClientPortRepositorio;
import com.gamelisto.catalogo.infrastructure.in.igdb.dto.PlatformFromIGDBResponse;
import com.gamelisto.catalogo.shared.config.IgdbProperties;
import com.gamelisto.catalogo.infrastructure.in.igdb.dto.GameFromIGDBResponse;
import com.gamelisto.catalogo.infrastructure.in.igdb.mapper.IgdbGameMapper;
import com.gamelisto.catalogo.infrastructure.in.igdb.mapper.IgdbPlatformMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Adapter HTTP para comunicación con la API de IGDB. */
@Component
@RequiredArgsConstructor
public class IgdbHttpAdapterRepositorio implements IgdbClientPortRepositorio {

  private static final Logger logger = LoggerFactory.getLogger(IgdbHttpAdapterRepositorio.class);

  private final WebClient igdbWebClient;
  private final IgdbQueryBuilder queryBuilder;
  private final IgdbRateLimitHandler rateLimitHandler;
  private final IgdbProperties igdbProperties;
  private final IgdbGameMapper igdbGameMapper;
  private final IgdbPlatformMapper igdbPlatformMapper;

  @Override
  public List<IgdbGameDTO> fetchGamesBatch(Long afterId, int limit) {
    String query = queryBuilder.buildGamesQuery(afterId, limit);

    return rateLimitHandler.executeWithRetry(
        () -> {
          List<GameFromIGDBResponse> response =
              igdbWebClient
                  .post()
                  .uri("/games")
                  .header("Authorization", "Bearer " + igdbProperties.getAccessToken())
                  .bodyValue(query)
                  .retrieve()
                  .bodyToMono(new ParameterizedTypeReference<List<GameFromIGDBResponse>>() {})
                  .block();

          if (response == null) {
            logger.warn("Respuesta nula de IGDB para juegos");
            return List.of();
          }
          return response.stream().map(igdbGameMapper::toApplicationDto).toList();
        });
  }

  @Override
  public List<IgdbPlatformDTO> fetchPlatforms() {
    String query = queryBuilder.buildPlatformsQuery();

    return rateLimitHandler.executeWithRetry(
        () -> {
          List<PlatformFromIGDBResponse> response =
              igdbWebClient
                  .post()
                  .uri("/platforms")
                  .header("Authorization", "Bearer " + igdbProperties.getAccessToken())
                  .bodyValue(query)
                  .retrieve()
                  .bodyToMono(new ParameterizedTypeReference<List<PlatformFromIGDBResponse>>() {})
                  .block();

          if (response == null) {
            logger.warn("Respuesta nula de IGDB para plataformas");
            return List.of();
          }
          return response.stream().map(igdbPlatformMapper::toApplicationDto).toList();
        });
  }
}
