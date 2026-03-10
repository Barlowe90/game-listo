package com.gamelisto.catalogo.infrastructure.in.api.dto;

import com.gamelisto.catalogo.application.usecases.GameDetailResult;

import java.util.List;

/** Response DTO para detalles multimedia de un juego */
public record GameDetailResponse(Long gameId, List<String> screenshots, List<String> videos) {

  public static GameDetailResponse from(GameDetailResult dto) {
    if (dto == null) return null;
    return new GameDetailResponse(dto.gameId(), dto.screenshots(), dto.videos());
  }
}
