package com.gamelisto.social.application.usecases;

import com.gamelisto.social.application.exceptions.ApplicationException;
import com.gamelisto.social.dominio.GrafoUsuarioRepositorio;
import com.gamelisto.social.dominio.JuegoSocialRepositorio;
import com.gamelisto.social.dominio.UserRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntradaEventosUseCase implements EntradaEventosHandle {

  private final GrafoUsuarioRepositorio grafoUsuarioRepositorio;
  private final JuegoSocialRepositorio juegoSocialRepositorio;

  @Override
  public void procesarUsuarioCreado(
      UUID usuarioId, String username, String avatar, String discordUserId) {
    UserRef user = UserRef.of(usuarioId, username, avatar, discordUserId);
    grafoUsuarioRepositorio.upsertUser(user);
  }

  @Override
  public void procesarUsuarioActualizado(
      UUID usuarioId, String username, String avatar, String discordUserId) {
    UserRef user = UserRef.of(usuarioId, username, avatar, discordUserId);
    grafoUsuarioRepositorio.upsertUser(user);
  }

  @Override
  public void procesarUsuarioEliminado(UUID usuarioId) {
    grafoUsuarioRepositorio.deleteUser(usuarioId);
  }

  @Override
  public void procesarEstadoActualizado(UUID usuarioId, Long gameRef, String estado) {
    // Delegar sincronización del estado del juego al repositorio de juego
    try {
      juegoSocialRepositorio.syncGameState(usuarioId, gameRef, estado);
    } catch (RuntimeException e) {
      throw new ApplicationException("no se pudo actualizar el estado", e);
    }
  }
}
