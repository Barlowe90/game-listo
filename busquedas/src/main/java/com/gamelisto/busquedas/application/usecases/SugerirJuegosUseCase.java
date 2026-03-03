package com.gamelisto.busquedas.application.usecases;

import com.gamelisto.busquedas.domain.BuscarJuegoDoc;
import com.gamelisto.busquedas.domain.BuscarJuegoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SugerirJuegosUseCase implements SugerirJuegosHandle {

  private final BuscarJuegoRepositorio repositorio;
  private static final int MAX_SIZE = 20;

  @Value("${busquedas.suggest.min-chars:2}")
  private int minChars;

  @Value("${busquedas.suggest.default-size:4}")
  private int defaultSize;

  @Override
  public List<BuscarJuegoDoc> execute(String query, Integer requestedSize) {
    if (query == null || query.length() < minChars) {
      return Collections.emptyList();
    }

    int size = (requestedSize != null) ? Math.min(requestedSize, MAX_SIZE) : defaultSize;
    return repositorio.suggest(query, size);
  }
}
