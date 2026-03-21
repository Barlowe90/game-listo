package com.gamelisto.biblioteca.infrastructure.out.http;

import com.gamelisto.biblioteca.application.usecase.SteamOwnedGame;
import com.gamelisto.biblioteca.application.usecase.SteamOwnedGamesPort;
import com.gamelisto.biblioteca.infrastructure.exceptions.InfrastructureException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SteamOwnedGamesHttpClient implements SteamOwnedGamesPort {
  private final RestTemplate restTemplate = new RestTemplate();

  @Value("${steam.api.base-url:https://api.steampowered.com}")
  private String steamBaseUrl;

  @Value("${steam.api.key:}")
  private String steamApiKey;

  @Override
  public List<SteamOwnedGame> findOwnedGames(String steamId64) {
    if (steamApiKey == null || steamApiKey.isBlank()) {
      throw new InfrastructureException("La STEAM_API_KEY no esta configurada");
    }

    String url =
        UriComponentsBuilder.fromUriString(steamBaseUrl)
            .path("/IPlayerService/GetOwnedGames/v1/")
            .queryParam("key", steamApiKey)
            .queryParam("steamid", steamId64)
            .queryParam("include_appinfo", 1)
            .queryParam("include_played_free_games", 1)
            .toUriString();

    try {
      ResponseEntity<SteamOwnedGamesEnvelope> response =
          restTemplate.getForEntity(url, SteamOwnedGamesEnvelope.class);
      SteamOwnedGamesEnvelope body = response.getBody();

      if (body == null || body.response() == null || body.response().games() == null) {
        return List.of();
      }

      return body.response().games().stream()
          .filter(game -> game != null && game.appid() != null)
          .map(game -> new SteamOwnedGame(game.appid(), game.name()))
          .toList();
    } catch (RestClientException e) {
      throw new InfrastructureException("No se pudo obtener la biblioteca de Steam", e);
    }
  }

  private record SteamOwnedGamesEnvelope(SteamOwnedGamesResponse response) {}

  private record SteamOwnedGamesResponse(Integer game_count, List<SteamOwnedGameDto> games) {}

  private record SteamOwnedGameDto(Long appid, String name) {}
}
