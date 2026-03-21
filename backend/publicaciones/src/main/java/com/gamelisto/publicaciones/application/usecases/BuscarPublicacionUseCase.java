package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.*;
import com.gamelisto.publicaciones.domain.vo.GrupoId;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
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
            .findById(PublicacionId.of(publicacionUuid))
            .orElseThrow(() -> new ApplicationException("No se encuentra la publicacion"));

    GrupoJuego grupoJuego =
        grupoJuegoRepositorio
            .findByPublicacionId(PublicacionId.of(publicacionUuid))
            .orElseThrow(() -> new ApplicationException("No se encuentra el grupo de juego"));

    GrupoId grupoId = grupoJuego.getId();

    List<java.util.UUID> userIds =
        grupoJuegoUsuarioRepositorio.findByGrupoId(grupoId).stream()
            .map(g -> g.getUsuarioId().value())
            .toList();

    List<UsuarioRef> usuarios =
        userIds.stream()
            .map(id -> usuariosRefRepositorio.findById(id).orElse(null))
            .filter(Objects::nonNull)
            .toList();

    return PublicacionDetalleResult.from(publicacion, grupoJuego, usuarios);
  }
}
