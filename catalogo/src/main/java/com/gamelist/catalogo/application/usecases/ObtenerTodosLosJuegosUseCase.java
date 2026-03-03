package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.out.GameDTO;
import com.gamelist.catalogo.domain.GameRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
