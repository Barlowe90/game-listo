package com.gamelist.catalogo.infrastructure.in.igdb.dto;

public record IgdbScreenshotResponseDto(String url, Integer width, Integer height) {

  /**
   * IGDB devuelve URLs sin protocolo (ej: "//images.igdb.com/...") Esta función construye la URL
   * completa
   */
  public String getFullUrl() {
    if (url == null || url.isEmpty()) {
      return null;
    }
    if (url.startsWith("http")) {
      return url;
    }
    return "https:" + url;
  }
}
