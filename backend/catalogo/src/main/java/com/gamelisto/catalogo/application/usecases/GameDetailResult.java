package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.domain.GameDetail;

import java.util.List;

public record GameDetailResult(Long gameId, List<String> screenshots, List<String> videos) {

  public static GameDetailResult from(GameDetail gameDetail) {
    return new GameDetailResult(
        gameDetail.getGameId().value(),
        gameDetail.getScreenshots() != null ? gameDetail.getScreenshots() : List.of(),
        gameDetail.getVideos() != null ? gameDetail.getVideos() : List.of());
  }
}
