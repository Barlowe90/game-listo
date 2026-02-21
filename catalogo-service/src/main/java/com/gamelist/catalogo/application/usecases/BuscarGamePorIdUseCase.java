package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.command.BuscarGamePorIdCommand;
import com.gamelist.catalogo.application.dto.out.GameDTO;
import com.gamelist.catalogo.domain.exceptions.EntityNotFoundException;
import com.gamelist.catalogo.domain.game.GameId;
import com.gamelist.catalogo.domain.repositories.RepositorioGame;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuscarGamePorIdUseCase {

  private final RepositorioGame repositorioGame;

  public BuscarGamePorIdUseCase(RepositorioGame repositorioGame) {
    this.repositorioGame = repositorioGame;
  }

  @Transactional(readOnly = true)
  public GameDTO execute(BuscarGamePorIdCommand command) {
    return repositorioGame
        .findById(GameId.of(command.gameId()))
        .map(GameDTO::from)
        .orElseThrow(
            () -> new EntityNotFoundException("Juego no encontrado con ID: " + command.gameId()));
  }
}
