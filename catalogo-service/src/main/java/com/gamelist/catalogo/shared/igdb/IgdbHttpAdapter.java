package com.gamelist.catalogo.infrastructure.igdb;

import com.gamelist.catalogo.application.dto.results.IgdbGameDTO;
import com.gamelist.catalogo.application.dto.results.IgdbPlatformDTO;
import com.gamelist.catalogo.domain.repositories.IIgdbClientPort;
import com.gamelist.catalogo.shared.config.IgdbProperties;
import com.gamelist.catalogo.infrastructure.igdb.dto.IgdbGameResponseDto;
import com.gamelist.catalogo.infrastructure.igdb.dto.IgdbPlatformResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Adapter HTTP para comunicación con la API de IGDB.
 *
 * <p>Implementa el puerto {@link IIgdbClientPort} usando WebClient. El access token se obtiene
 * desde variables de entorno configuradas en .env
 */
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
          return convertToGameDTOs(response);
        });
  }

  @Override
  public List<IgdbPlatformDTO> fetchPlatforms() {
    log.info("Obteniendo plataformas desde IGDB");

    String query = queryBuilder.buildPlatformsQuery();
    log.debug("Query IGDB: {}", query);

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
          return convertToPlatformDTOs(response);
        });
  }

  private List<IgdbGameDTO> convertToGameDTOs(List<IgdbGameResponseDto> responses) {
    return responses.stream()
        .map(
            r ->
                new IgdbGameDTO(
                    r.id(),
                    r.name(),
                    r.summary(),
                    r.cover() != null ? r.cover().getFullUrl() : null,
                    r.platforms() != null ? r.platforms() : List.of()))
        .toList();
  }

  private List<IgdbPlatformDTO> convertToPlatformDTOs(List<IgdbPlatformResponseDto> responses) {
    return responses.stream()
        .map(r -> new IgdbPlatformDTO(r.id(), r.name(), r.abbreviation()))
        .toList();
  }
}
