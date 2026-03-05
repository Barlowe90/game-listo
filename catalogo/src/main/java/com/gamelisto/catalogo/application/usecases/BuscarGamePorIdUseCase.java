package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.application.dto.command.BuscarGamePorIdCommand;
import com.gamelisto.catalogo.application.dto.out.GameDTO;
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
  public GameDTO execute(BuscarGamePorIdCommand command) {
    return gameRepositorio
        .findById(GameId.of(command.gameId()))
        .map(GameDTO::from)
        .orElseThrow(() -> new DomainException("Juego no encontrado con ID: " + command.gameId()));
  }
}
