package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.GameEstado;
import com.gamelisto.biblioteca.domain.GameEstadoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class GameEstadoRepositorioPostgres implements GameEstadoRepositorio {

  private final GameEstadoJpaRepository estadoJpa;
  private final UsuarioRefJpaRepository usuarioJpa;
  private final GameRefJpaRepository gameRefJpa;
  private final GameEstadoMapper mapper;

  public GameEstadoRepositorioPostgres(
      GameEstadoJpaRepository estadoJpa,
      UsuarioRefJpaRepository usuarioJpa,
      GameRefJpaRepository gameRefJpa,
      GameEstadoMapper mapper) {
    this.estadoJpa = estadoJpa;
    this.usuarioJpa = usuarioJpa;
    this.gameRefJpa = gameRefJpa;
    this.mapper = mapper;
  }

  @Override
  public GameEstado save(GameEstado gameEstado) {
    GameEstadoEntity entity = mapper.toEntity(gameEstado);

    entity.setUsuarioRef(usuarioJpa.getReferenceById(gameEstado.getUsuarioRefId()));
    entity.setGameRef(gameRefJpa.getReferenceById(gameEstado.getGameRefId()));

    GameEstadoEntity saved = estadoJpa.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<GameEstado> findByUsuarioYGame(UUID userId, Long gameId) {
    return estadoJpa.findByUsuarioRef_IdAndGameRef_Id(userId, gameId).map(mapper::toDomain);
  }

  @Override
  public void deleteById(UUID id) {
    estadoJpa.deleteById(id);
  }
}
