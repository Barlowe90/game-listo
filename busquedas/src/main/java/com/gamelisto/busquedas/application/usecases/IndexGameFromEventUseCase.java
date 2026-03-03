package com.gamelisto.busquedas.application.usecases;

import com.gamelisto.busquedas.domain.BuscarJuegoRepositorio;
import com.gamelisto.busquedas.infrastructure.messaging.dto.VideojuegoCreadoEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Caso de uso: indexar (upsert) un videojuego en OpenSearch a partir de un evento de catálogo. */
@Service
public class IndexGameFromEventUseCase {

  private static final Logger logger = LoggerFactory.getLogger(IndexGameFromEventUseCase.class);

  private final BuscarJuegoRepositorio repositorio;

  public IndexGameFromEventUseCase(BuscarJuegoRepositorio repositorio) {
    this.repositorio = repositorio;
  }

  public void execute(VideojuegoCreadoEventDto dto) {
    logger.info(
        "Indexando videojuego en OpenSearch: gameId={}, title={}", dto.gameId(), dto.title());
    repositorio.upsert(GameSearchMapper.fromEvent(dto));
  }
}
