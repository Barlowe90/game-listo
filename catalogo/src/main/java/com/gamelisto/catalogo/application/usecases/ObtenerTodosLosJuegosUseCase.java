package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.application.dto.out.GameDTO;
import com.gamelisto.catalogo.domain.GameRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ObtenerTodosLosJuegosUseCase implements ObtenerTodosLosJuegosHandle {

  private final GameRepositorio gameRepositorio;

  @Override
  public List<GameDTO> execute() {
    return gameRepositorio.findAll().stream().map(GameDTO::from).toList();
  }
}
