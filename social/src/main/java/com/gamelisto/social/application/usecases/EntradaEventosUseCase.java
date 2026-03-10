package com.gamelisto.social.application.usecases;

import com.gamelisto.social.dominio.GrafoUsuarioRepositorio;
import com.gamelisto.social.dominio.JuegoSocialRepositorio;
import com.gamelisto.social.dominio.UserRef;
import com.gamelisto.social.dominio.exceptions.DomainException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EntradaEventosUseCase implements EntradaEventosHandle {
  private static final Logger log = LoggerFactory.getLogger(EntradaEventosUseCase.class);

  private final GrafoUsuarioRepositorio grafoUsuarioRepositorio;
  private final JuegoSocialRepositorio juegoSocialRepositorio;

  @Override
  public void procesarUsuarioCreado(UUID usuarioId, String username, String avatar) {
    UserRef user = UserRef.of(usuarioId, username, avatar);
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
      log.error(
          "Error al sincronizar estado de juego para usuario {} y juego {}: {}",
          usuarioId,
          gameRef,
          e.getMessage());
      throw e;
    }
  }
}
