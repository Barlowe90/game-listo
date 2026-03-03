package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.command.BuscarGameDetailPorIdCommand;
import com.gamelist.catalogo.application.dto.out.GameDetailDTO;
import com.gamelist.catalogo.domain.exceptions.DomainException;
import com.gamelist.catalogo.domain.game.GameId;
import com.gamelist.catalogo.domain.gamedetail.GameDetail;
import com.gamelist.catalogo.domain.repositories.IGameDetailRepository;
import com.gamelist.catalogo.domain.repositories.RepositorioGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuscarGameDetailPorIdUseCase {

  private final RepositorioGame gameRepository;
  private final IGameDetailRepository gameDetailRepository;

  @Transactional(readOnly = true)
  public GameDetailDTO execute(BuscarGameDetailPorIdCommand command) {

    GameId gameId = GameId.of(command.gameId());
    gameRepository
        .findById(gameId)
        .orElseThrow(() -> new DomainException("Juego no encontrado con ID: " + command.gameId()));

    GameDetail gameDetail =
        gameDetailRepository.findByGameId(gameId).orElse(GameDetail.empty(gameId));

    return GameDetailDTO.from(gameDetail);
  }
}
