package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.domain.GameEstadoRepositorio;
import com.gamelisto.biblioteca.domain.GameId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscarGameEstadosPorGameIdUseCase implements BuscarGameEstadosPorGameIdHandler {

  private final GameEstadoRepositorio gameEstadoRepositorio;

  @Override
  @Transactional(readOnly = true)
  public List<GameEstadoResult> execute(Long gameRefId) {
    return gameEstadoRepositorio.findByGameRefId(GameId.of(gameRefId)).stream()
        .map(GameEstadoResult::from)
        .toList();
  }
}
