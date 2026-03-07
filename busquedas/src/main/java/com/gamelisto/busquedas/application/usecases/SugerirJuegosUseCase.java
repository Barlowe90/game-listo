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

  @Value("${busquedas.suggest.min-chars:2}")
  private int minChars;

  @Override
  public List<BuscarJuegoDoc> execute(String query, int size) {
    if (query == null || query.length() < minChars) {
      return Collections.emptyList();
    }

    return repositorio.suggest(query, size);
  }
}
