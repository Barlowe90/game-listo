package com.gamelist.catalogo_service.domain.gamedetail;

import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;

public record Screenshot(String url, Integer width, Integer height) {

  public Screenshot {
    if (url == null || url.isBlank()) {
      throw new InvalidGameDataException("La URL del screenshot no puede estar vacía");
    }
    if (width != null && width <= 0) {
      throw new InvalidGameDataException("El ancho del screenshot debe ser positivo");
    }
    if (height != null && height <= 0) {
      throw new InvalidGameDataException("La altura del screenshot debe ser positiva");
    }
  }

  public static Screenshot of(String url) {
    return new Screenshot(url, null, null);
  }

  public static Screenshot of(String url, Integer width, Integer height) {
    return new Screenshot(url, width, height);
  }

  public boolean hasDimensions() {
    return width != null && height != null;
  }
}
