package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.command.BuscarGameDetailPorIdCommand;
import com.gamelist.catalogo.application.dto.out.GameDetailDTO;
import com.gamelist.catalogo.domain.exceptions.DomainException;
import com.gamelist.catalogo.domain.GameId;
import com.gamelist.catalogo.domain.GameDetail;
import com.gamelist.catalogo.domain.GameDetailRepositorio;
import com.gamelist.catalogo.domain.GameRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarGameDetailPorIdUseCase implements BuscarGameDetailPorIdHandle {

  private final GameRepositorio gameRepository;
  private final GameDetailRepositorio gameDetailRepository;

  @Override
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
