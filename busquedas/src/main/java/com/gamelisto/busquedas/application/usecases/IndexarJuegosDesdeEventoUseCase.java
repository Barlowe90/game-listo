package com.gamelisto.busquedas.application.usecases;

import com.gamelisto.busquedas.domain.BuscarJuegoRepositorio;
import com.gamelisto.busquedas.infrastructure.messaging.dto.VideojuegoCreadoEventDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IndexarJuegosDesdeEventoUseCase implements IndexarJuegosDesdeEventoHandle {

  private static final Logger logger =
      LoggerFactory.getLogger(IndexarJuegosDesdeEventoUseCase.class);

  private final BuscarJuegoRepositorio repositorio;

  @Override
  public void execute(VideojuegoCreadoEventDto dto) {
    logger.info(
        "Indexando videojuego en OpenSearch: gameId={}, title={}", dto.gameId(), dto.title());
    repositorio.upsert(GameSearchMapper.fromEvent(dto));
  }
}
