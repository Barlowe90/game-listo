package com.gamelist.catalogo.infrastructure.out.dto;

import com.gamelist.catalogo.application.dto.out.GameDetailDTO;

import java.util.List;

/** Response DTO para detalles multimedia de un juego */
public record GameDetailResponse(
    Long gameId,
    List<String> alternativeNames,
    String coverUrl,
    List<String> screenshots,
    List<String> videos) {

  public static GameDetailResponse from(GameDetailDTO dto) {
    if (dto == null) return null;
    return new GameDetailResponse(
        dto.gameId(), dto.alternativeNames(), dto.coverUrl(), dto.screenshots(), dto.videos());
  }
}
