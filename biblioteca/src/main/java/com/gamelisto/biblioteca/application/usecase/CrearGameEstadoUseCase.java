package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.domain.Estado;
import com.gamelisto.biblioteca.domain.GameEstado;
import com.gamelisto.biblioteca.domain.GameEstadoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CrearGameEstadoUseCase implements CrearGameEstadoHandler {

  private final GameEstadoRepositorio repositorioGameEstado;

  public CrearGameEstadoUseCase(GameEstadoRepositorio repositorioGameEstado) {
    this.repositorioGameEstado = repositorioGameEstado;
  }

  @Transactional
  public void execute(CrearGameEstadoCommand command) {
    UUID userId = UUID.fromString(command.userId());
    Long gameId = Long.parseLong(command.gameId());
    Estado estado = Estado.valueOf(command.estado());

    GameEstado actualizado =
        repositorioGameEstado
            .findByUsuarioYGame(userId, gameId)
            .map(
                existente ->
                    GameEstado.reconstitute(
                        existente.getId(), userId, gameId, estado, existente.getRating()))
            .orElse(GameEstado.create(userId, gameId, estado, 0.0));

    repositorioGameEstado.save(actualizado);
  }
}
