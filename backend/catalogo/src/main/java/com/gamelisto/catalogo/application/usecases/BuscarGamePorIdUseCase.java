package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.domain.exceptions.DomainException;
import com.gamelisto.catalogo.domain.GameId;
import com.gamelisto.catalogo.domain.GameRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarGamePorIdUseCase implements BuscarGamePorIdHandle {

  private final GameRepositorio gameRepositorio;

  @Override
  public GameResult execute(BuscarGamePorIdCommand command) {
    return gameRepositorio
        .findById(GameId.of(command.gameId()))
        .map(GameResult::from)
        .orElseThrow(() -> new DomainException("Juego no encontrado con ID: " + command.gameId()));
  }
}
