package com.gamelisto.biblioteca.infrastructure.out.persistence;

import com.gamelisto.biblioteca.domain.GameEstado;
import com.gamelisto.biblioteca.domain.GameEstadoRepositorio;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    entity.setUsuarioRef(usuarioJpa.getReferenceById(gameEstado.getUsuarioRefId().value()));
    entity.setGameRef(gameRefJpa.getReferenceById(gameEstado.getGameRefId().value()));

    GameEstadoEntity saved = estadoJpa.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<GameEstado> findByUsuarioYGame(
      com.gamelisto.biblioteca.domain.UsuarioId userId,
      com.gamelisto.biblioteca.domain.GameId gameId) {
    return estadoJpa
        .findByUsuarioRef_IdAndGameRef_Id(userId.value(), gameId.value())
        .map(mapper::toDomain);
  }

  @Override
  public void deleteById(com.gamelisto.biblioteca.domain.GameEstadoId id) {
    estadoJpa.deleteById(id.value());
  }

  @Override
  public List<GameEstado> findByGameRefId(com.gamelisto.biblioteca.domain.GameId gameId) {
    return estadoJpa.findByGameRef_Id(gameId.value()).stream().map(mapper::toDomain).toList();
  }
}
