package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.command.BuscarGamePorIdCommand;
import com.gamelist.catalogo.application.dto.out.GameDTO;
import com.gamelist.catalogo.domain.exceptions.DomainException;
import com.gamelist.catalogo.domain.GameId;
import com.gamelist.catalogo.domain.GameRepositorio;
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
