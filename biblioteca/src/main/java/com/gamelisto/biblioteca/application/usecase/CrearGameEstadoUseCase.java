package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.domain.Estado;
import com.gamelisto.biblioteca.domain.GameEstado;
import com.gamelisto.biblioteca.domain.GameEstadoRepositorio;
import com.gamelisto.biblioteca.domain.UsuarioId;
import com.gamelisto.biblioteca.domain.GameId;
import com.gamelisto.biblioteca.domain.Rating;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CrearGameEstadoUseCase implements CrearGameEstadoHandler {

  private final GameEstadoRepositorio repositorioGameEstado;

  @Transactional
  public void execute(CrearGameEstadoCommand command) {
    UsuarioId userId = UsuarioId.of(command.userId());
    GameId gameId = GameId.of(Long.parseLong(command.gameId()));
    Estado estado = Estado.valueOf(command.estado());

    GameEstado actualizado =
        repositorioGameEstado
            .findByUsuarioYGame(userId, gameId)
            .map(
                existente ->
                    GameEstado.reconstitute(
                        existente.getId(), userId, gameId, estado, existente.getRating()))
            .orElse(GameEstado.create(userId, gameId, estado, Rating.of(0.0)));

    repositorioGameEstado.save(actualizado);
  }
}
