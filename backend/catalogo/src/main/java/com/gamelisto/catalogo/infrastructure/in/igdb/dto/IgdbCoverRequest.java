package com.gamelisto.catalogo.infrastructure.in.igdb.dto;

public record IgdbCoverRequest(String url, Integer width, Integer height) {

  public String getFullUrl() {
    if (url == null) {
      return null;
    }
    if (url.startsWith("//")) {
      return "https:" + url;
    }
    if (!url.startsWith("http")) {
      return "https://" + url;
    }
    return url;
  }
}
