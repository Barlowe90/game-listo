package com.gamelisto.busquedas.application.usecases;

import java.util.List;

import com.gamelisto.busquedas.domain.BuscarJuegoDoc;
import com.gamelisto.busquedas.domain.BuscarJuegoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EntradaEventosUseCase implements EntradaEventosHandle {

  private final BuscarJuegoRepositorio buscarJuegoRepositorio;

  @Override
  public void procesarGameCreado(Long gameId, String title, List<String> alternativeNames) {
    BuscarJuegoDoc juegoDoc = new BuscarJuegoDoc(gameId, title, alternativeNames);
    buscarJuegoRepositorio.upsert(juegoDoc);
  }
}
