package com.gamelisto.biblioteca.infrastructure.out.http;

import com.gamelisto.biblioteca.application.usecase.CatalogoSteamResolver;
import com.gamelisto.biblioteca.infrastructure.exceptions.InfrastructureException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class CatalogoSteamResolverHttpClient implements CatalogoSteamResolver {
  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${services.catalogo.uri:http://catalogo:8082}")
  private String catalogoServiceUrl;

  @Override
  public Map<Long, Long> resolveGamesBySteamAppIds(List<Long> steamAppIds) {
    if (steamAppIds == null || steamAppIds.isEmpty()) {
      return Map.of();
    }

    try {
      ResponseEntity<ResolverJuegosSteamResponse> response =
          restTemplate.postForEntity(
              catalogoServiceUrl + "/v1/catalogo/games/steam/resolve",
              new ResolverJuegosSteamRequest(steamAppIds),
              ResolverJuegosSteamResponse.class);

      ResolverJuegosSteamResponse body = response.getBody();
      if (body == null || body.items() == null || body.items().isEmpty()) {
        return Map.of();
      }

      Map<Long, Long> resolvedGames = new LinkedHashMap<>();
      for (ResolverJuegoSteamItemResponse item : body.items()) {
        if (item == null || item.steamAppId() == null || item.gameId() == null) {
          continue;
        }
        resolvedGames.put(item.steamAppId(), item.gameId());
      }

      return resolvedGames;
    } catch (RestClientException e) {
      throw new InfrastructureException("No se pudieron resolver los juegos de Steam en catalogo", e);
    }
  }

  private record ResolverJuegosSteamRequest(List<Long> steamAppIds) {}

  private record ResolverJuegoSteamItemResponse(Long steamAppId, Long gameId) {}

  private record ResolverJuegosSteamResponse(List<ResolverJuegoSteamItemResponse> items) {}
}
