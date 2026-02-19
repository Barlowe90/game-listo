package com.gamelist.catalogo.domain.gamedetail;

import com.gamelist.catalogo.domain.exceptions.DomainException;

public record Video(String url, String videoId) {

  public Video {
    if (url == null || url.isBlank()) {
      throw new DomainException("La URL del video no puede estar vacía");
    }
    // videoId es opcional (puede extraerse de la URL más tarde)
  }

  public static Video of(String url) {
    return new Video(url, null);
  }

  public static Video of(String url, String videoId) {
    return new Video(url, videoId);
  }

  public boolean hasVideoId() {
    return videoId != null && !videoId.isBlank();
  }
}
