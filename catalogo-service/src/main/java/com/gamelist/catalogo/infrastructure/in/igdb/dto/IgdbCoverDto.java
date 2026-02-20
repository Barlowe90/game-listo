package com.gamelist.catalogo.infrastructure.in.igdb.dto;

public record IgdbCoverDto(String url, Integer width, Integer height) {

  /**
   * Obtiene la URL completa de la portada.
   *
   * <p>IGDB a veces retorna URLs sin el protocolo, esta función asegura que tenga https://
   *
   * @return URL completa con https://
   */
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
