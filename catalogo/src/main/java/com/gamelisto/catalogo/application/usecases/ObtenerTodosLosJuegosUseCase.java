package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.domain.GameRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ObtenerTodosLosJuegosUseCase implements ObtenerTodosLosJuegosHandle {

  private final GameRepositorio gameRepositorio;

  @Override
  public List<GameResult> execute() {
    return gameRepositorio.findAll().stream().map(GameResult::from).toList();
  }
}
