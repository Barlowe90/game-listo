package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AbandonarGrupoUseCase implements AbandonarGrupoHandler {

  private final GrupoJuegoUsuarioRepositorio grupoJuegoUsuarioRepositorio;
  private final GrupoJuegoRepositorio grupoJuegoRepositorio;
  private final PublicacionRepositorio publicacionRepositorio;

  @Override
  public void execute(UUID publicacionId, UUID userId) {
    Publicacion pub =
        publicacionRepositorio
            .findById(publicacionId)
            .orElseThrow(() -> new RuntimeException("Publicación no encontrada"));

    if (pub.getAutorId().equals(userId)) {
      throw new RuntimeException("El autor no puede abandonar su propio grupo");
    }

    GrupoJuego grupo =
        grupoJuegoRepositorio
            .findByPublicacionId(publicacionId)
            .orElseThrow(() -> new RuntimeException("Grupo no existe para esa publicación"));

    grupoJuegoUsuarioRepositorio.deleteByGrupoIdAndUsuarioId(grupo.getId(), userId);
  }
}
