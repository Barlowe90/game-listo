package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.domain.exceptions.DomainException;
import com.gamelisto.catalogo.domain.GameId;
import com.gamelisto.catalogo.domain.GameDetail;
import com.gamelisto.catalogo.domain.GameDetailRepositorio;
import com.gamelisto.catalogo.domain.GameRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarGameDetailPorIdUseCase implements BuscarGameDetailPorIdHandle {

  private final GameRepositorio gameRepository;
  private final GameDetailRepositorio gameDetailRepository;

  @Override
  public GameDetailResult execute(BuscarGameDetailPorIdCommand command) {

    GameId gameId = GameId.of(command.gameId());
    gameRepository
        .findById(gameId)
        .orElseThrow(() -> new DomainException("Juego no encontrado con ID: " + command.gameId()));

    GameDetail gameDetail =
        gameDetailRepository.findByGameId(gameId).orElse(GameDetail.empty(gameId));

    return GameDetailResult.from(gameDetail);
  }
}
