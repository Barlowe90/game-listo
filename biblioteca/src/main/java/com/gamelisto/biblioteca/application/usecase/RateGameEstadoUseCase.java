package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.GameEstado;
import com.gamelisto.biblioteca.domain.GameEstadoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RateGameEstadoUseCase implements RateGameEstadoHandler {

  private final GameEstadoRepositorio gameEstadoRepositorio;

  public RateGameEstadoUseCase(GameEstadoRepositorio gameEstadoRepositorio) {
    this.gameEstadoRepositorio = gameEstadoRepositorio;
  }

  @Transactional
  public void execute(RateGameEstadoCommand command) {

    UUID userId = UUID.fromString(command.userId());
    Long gameId = Long.parseLong(command.gameId());

    GameEstado existente =
        gameEstadoRepositorio
            .findByUsuarioYGame(userId, gameId)
            .orElseThrow(() -> new ApplicationException("No existe GameEstado para ese juego"));

    GameEstado actualizado =
        GameEstado.reconstitute(
            existente.getId(), userId, gameId, existente.getEstado(), command.rating());

    gameEstadoRepositorio.save(actualizado);
  }
}
