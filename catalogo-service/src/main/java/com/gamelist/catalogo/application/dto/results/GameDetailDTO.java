package com.gamelist.catalogo.application.dto.results;

import com.gamelist.catalogo.domain.gamedetail.GameDetail;

import java.util.List;

public record GameDetailDTO(
    Long gameId,
    List<String> alternativeNames,
    String coverUrl,
    List<String> screenshots,
    List<String> videos) {

  public static GameDetailDTO from(GameDetail gameDetail) {
    return new GameDetailDTO(
        gameDetail.getGameId().value(),
        gameDetail.getAlternativeNames() != null ? gameDetail.getAlternativeNames() : List.of(),
        gameDetail.getCoverUrl(),
        gameDetail.getScreenshots() != null ? gameDetail.getScreenshots() : List.of(),
        gameDetail.getVideos() != null ? gameDetail.getVideos() : List.of());
  }
}
