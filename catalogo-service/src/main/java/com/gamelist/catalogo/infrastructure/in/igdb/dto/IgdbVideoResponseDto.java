package com.gamelist.catalogo.infrastructure.in.igdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IgdbVideoResponseDto(@JsonProperty("video_id") String videoId, String name) {

  /** Construye URL de YouTube a partir del video ID */
  public String getYouTubeUrl() {
    if (videoId == null || videoId.isEmpty()) {
      return null;
    }
    return "https://www.youtube.com/watch?v=" + videoId;
  }
}
