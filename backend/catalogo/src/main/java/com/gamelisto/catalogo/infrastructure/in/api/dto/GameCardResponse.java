package com.gamelisto.catalogo.infrastructure.in.api.dto;

import com.gamelisto.catalogo.application.usecases.GameCardResult;
import java.util.List;

/** Response DTO ligero para tarjetas y listados paginados del catálogo. */
public record GameCardResponse(
    Long id, String name, String coverUrl, List<String> platforms, List<String> gameModes) {

  public static GameCardResponse from(GameCardResult dto) {
    if (dto == null) return null;

    return new GameCardResponse(
        dto.id(), dto.name(), dto.coverUrl(), dto.platforms(), dto.gameModes());
  }
}
