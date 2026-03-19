package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.domain.GameCardSummary;
import java.util.List;

public record GameCardResult(
    Long id, String name, String coverUrl, List<String> platforms, List<String> gameModes) {

  public static GameCardResult from(GameCardSummary summary) {
    if (summary == null) return null;

    return new GameCardResult(
        summary.id(),
        summary.name(),
        summary.coverUrl(),
        summary.platforms() != null ? List.copyOf(summary.platforms()) : List.of(),
        summary.gameModes() != null ? List.copyOf(summary.gameModes()) : List.of());
  }
}
