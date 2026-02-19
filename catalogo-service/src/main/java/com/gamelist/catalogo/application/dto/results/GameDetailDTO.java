package com.gamelist.catalogo.application.dto.results;

import java.util.List;

public record GameDetailDTO(Long gameId, List<ScreenshotDTO> screenshots, List<VideoDTO> videos) {
  public record ScreenshotDTO(String url, Integer width, Integer height) {}

  public record VideoDTO(String videoId, String name) {}
}
