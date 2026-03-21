package com.gamelisto.catalogo.infrastructure.in.api.dto;

import java.util.List;
import java.util.Map;

public record ResolverJuegosSteamResponse(List<ResolverJuegoSteamItemResponse> items) {

  public static ResolverJuegosSteamResponse from(Map<Long, Long> resolvedGames) {
    List<ResolverJuegoSteamItemResponse> items =
        resolvedGames.entrySet().stream()
            .map(entry -> new ResolverJuegoSteamItemResponse(entry.getKey(), entry.getValue()))
            .toList();
    return new ResolverJuegosSteamResponse(items);
  }
}
