package com.gamelist.catalogo.infrastructure.in.igdb.dto;

public record IgdbScreenshotRequest(String url, Integer width, Integer height) {

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
