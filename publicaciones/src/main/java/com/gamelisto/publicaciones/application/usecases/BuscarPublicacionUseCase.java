package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarPublicacionUseCase implements BuscarPublicacionHandler {

  private final PublicacionRepositorio publicacionRepositorio;
  private final GrupoJuegoRepositorio grupoJuegoRepositorio;
  private final GrupoJuegoUsuarioRepositorio grupoJuegoUsuarioRepositorio;
  private final UsuariosRefRepositorio usuariosRefRepositorio;

  @Override
  public PublicacionDetalleResult execute(String publicacionId) {
    UUID publicacionUuid = UUID.fromString(publicacionId);

    Publicacion publicacion =
        publicacionRepositorio
            .findById(publicacionUuid)
            .orElseThrow(() -> new ApplicationException("No se encuentra la publicacion"));

    GrupoJuego grupoJuego =
        grupoJuegoRepositorio
            .findByPublicacionId(publicacionUuid)
            .orElseThrow(() -> new ApplicationException("No se encuentra el grupo de juego"));

    UUID grupoId = grupoJuego.getId();

    List<UUID> userIds =
        grupoJuegoUsuarioRepositorio.findByGrupoId(grupoId).stream()
            .map(GrupoJuegoUsuario::getUsuarioId)
            .toList();

    List<UsuarioRef> usuarios =
        userIds.stream()
            .map(id -> usuariosRefRepositorio.findById(id).orElse(null))
            .filter(Objects::nonNull)
            .toList();

    return PublicacionDetalleResult.from(publicacion, usuarios);
  }
}
