package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import com.gamelisto.publicaciones.domain.GrupoJuegoRepositorio;
import com.gamelisto.publicaciones.domain.GrupoJuegoUsuario;
import com.gamelisto.publicaciones.domain.GrupoJuegoUsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EliminarPublicacionUseCase implements EliminarPublicacionHandler {
  private final PublicacionRepositorio publicacionRepositorio;
  private final GrupoJuegoRepositorio grupoJuegoRepositorio;
  private final GrupoJuegoUsuarioRepositorio grupoJuegoUsuarioRepositorio;

  @Override
  public void execute(UUID publicacionId, UUID userId) {

    Publicacion publicacion =
        publicacionRepositorio
            .findById(publicacionId)
            .orElseThrow(() -> new ApplicationException("Publicacion no encontrada"));

    if (!publicacion.getAutorId().equals(userId)) {
      throw new ApplicationException("Usuario no propietario");
    }

    // Si existe un grupo asociado, eliminar sus miembros y luego el grupo
    grupoJuegoRepositorio
        .findByPublicacionId(publicacionId)
        .ifPresent(
            grupo -> {
              // eliminar todos los miembros
              for (GrupoJuegoUsuario miembro :
                  grupoJuegoUsuarioRepositorio.findByGrupoId(grupo.getId())) {
                grupoJuegoUsuarioRepositorio.deleteByGrupoIdAndUsuarioId(
                    grupo.getId(), miembro.getUsuarioId());
              }
              // eliminar el grupo
              grupoJuegoRepositorio.delete(grupo);
            });

    // finalmente eliminar la publicación
    publicacionRepositorio.deleteById(publicacionId);
  }
}
