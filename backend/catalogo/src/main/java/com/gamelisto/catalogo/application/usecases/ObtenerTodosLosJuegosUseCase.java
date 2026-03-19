package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.domain.GameRepositorio;
import com.gamelisto.catalogo.domain.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObtenerTodosLosJuegosUseCase implements ObtenerTodosLosJuegosHandle {

  private final GameRepositorio gameRepositorio;

  @Override
  public PageResult<GameCardResult> execute(ObtenerTodosLosJuegosCommand command) {
    int safePage = Math.max(command.page(), 0);
    int safeSize = Math.max(command.size(), 1);

    return gameRepositorio.findSummaries(safePage, safeSize, command.platforms()).map(GameCardResult::from);
  }
}
