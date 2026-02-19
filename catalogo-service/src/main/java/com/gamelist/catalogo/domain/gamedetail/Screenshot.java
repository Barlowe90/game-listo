package com.gamelist.catalogo.domain.gamedetail;

import com.gamelist.catalogo.domain.exceptions.DomainException;

public record Screenshot(String url, Integer width, Integer height) {

  public Screenshot {
    if (url == null || url.isBlank()) {
      throw new DomainException("La URL del screenshot no puede estar vacía");
    }
    if (width != null && width <= 0) {
      throw new DomainException("El ancho del screenshot debe ser positivo");
    }
    if (height != null && height <= 0) {
      throw new DomainException("La altura del screenshot debe ser positiva");
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
