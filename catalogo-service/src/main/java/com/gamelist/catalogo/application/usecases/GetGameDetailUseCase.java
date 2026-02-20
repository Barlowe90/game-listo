package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.queries.GetGameDetailQuery;
import com.gamelist.catalogo.application.dto.results.GameDetailDTO;
import com.gamelist.catalogo.domain.exceptions.EntityNotFoundException;
import com.gamelist.catalogo.domain.game.GameId;
import com.gamelist.catalogo.domain.gamedetail.GameDetail;
import com.gamelist.catalogo.domain.repositories.IGameDetailRepository;
import com.gamelist.catalogo.domain.repositories.IGameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** findByID en mongo */
@Service
@RequiredArgsConstructor
@Slf4j
public class GetGameDetailUseCase {

  private final IGameRepository gameRepository;
  private final IGameDetailRepository gameDetailRepository;

  @Transactional(readOnly = true)
  public GameDetailDTO execute(GetGameDetailQuery query) {
    log.info("Obteniendo detalle del juego ID: {}", query.gameId());

    GameId gameId = GameId.of(query.gameId());
    gameRepository
        .findById(gameId)
        .orElseThrow(
            () -> new EntityNotFoundException("Juego no encontrado con ID: " + query.gameId()));

    GameDetail gameDetail =
        gameDetailRepository.findByGameId(gameId).orElse(GameDetail.empty(gameId));

    return GameDetailDTO.from(gameDetail);
  }
}
