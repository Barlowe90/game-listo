package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.out.GameDTO;
import com.gamelist.catalogo.domain.repositories.RepositorioGame;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ObtenerTodosLosJuegosUseCase {

  private final RepositorioGame repositorioGame;

  public ObtenerTodosLosJuegosUseCase(RepositorioGame repositorioGame) {
    this.repositorioGame = repositorioGame;
  }

  @Transactional(readOnly = true)
  public List<GameDTO> execute() {
    return repositorioGame.findAll().stream().map(GameDTO::from).toList();
  }
}
