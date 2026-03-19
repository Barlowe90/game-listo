package com.gamelisto.catalogo.application.usecases;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public record ObtenerTodosLosJuegosCommand(int page, int size, List<String> platforms) {

  public ObtenerTodosLosJuegosCommand {
    platforms =
        platforms == null
            ? List.of()
            : platforms.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(value -> value.toLowerCase(Locale.ROOT))
                .distinct()
                .toList();
  }
}
