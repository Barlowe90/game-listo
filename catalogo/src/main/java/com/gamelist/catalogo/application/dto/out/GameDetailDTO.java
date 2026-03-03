package com.gamelist.catalogo.application.dto.out;

import com.gamelist.catalogo.domain.GameDetail;

import java.util.List;

public record GameDetailDTO(Long gameId, List<String> screenshots, List<String> videos) {

  public static GameDetailDTO from(GameDetail gameDetail) {
    return new GameDetailDTO(
        gameDetail.getGameId().value(),
        gameDetail.getScreenshots() != null ? gameDetail.getScreenshots() : List.of(),
        gameDetail.getVideos() != null ? gameDetail.getVideos() : List.of());
  }
}
