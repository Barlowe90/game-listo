package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RateGameEstadoUseCase implements RateGameEstadoHandler {

  private final GameEstadoRepositorio gameEstadoRepositorio;

  @Transactional
  public void execute(RateGameEstadoCommand command) {

    UsuarioId userId = UsuarioId.of(command.userId());
    GameId gameId = GameId.of(Long.parseLong(command.gameId()));

    GameEstado existente =
        gameEstadoRepositorio
            .findByUsuarioYGame(userId, gameId)
            .orElseThrow(() -> new ApplicationException("No existe GameEstado para ese juego"));

    GameEstado actualizado =
        GameEstado.reconstitute(
            existente.getId(), userId, gameId, existente.getEstado(), Rating.of(command.rating()));

    gameEstadoRepositorio.save(actualizado);
  }
}
