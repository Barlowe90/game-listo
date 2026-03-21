package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.EstadoSolicitud;
import com.gamelisto.publicaciones.domain.GrupoJuego;
import com.gamelisto.publicaciones.domain.GrupoJuegoRepositorio;
import com.gamelisto.publicaciones.domain.GrupoJuegoUsuarioRepositorio;
import com.gamelisto.publicaciones.domain.SolicitudUnion;
import com.gamelisto.publicaciones.domain.SolicitudUnionRepositorio;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CrearSolicitudUnionUseCase implements CrearSolicitudUnionHandler {

  private final SolicitudUnionRepositorio solicitudUnionRepositorio;
  private final GrupoJuegoRepositorio grupoJuegoRepositorio;
  private final GrupoJuegoUsuarioRepositorio grupoJuegoUsuarioRepositorio;

  @Override
  public SolicitudUnionResult execute(UUID publicacionId, UUID userId) {
    PublicacionId publicacion = PublicacionId.of(publicacionId);
    UsuarioId usuario = UsuarioId.of(userId);

    Optional<SolicitudUnion> existente =
        solicitudUnionRepositorio.findByPublicacionIdAndUsuarioId(publicacion, usuario);

    if (existente.isPresent()) {
      return reenviarSolicitudSiProcede(existente.get(), publicacion, usuario);
    }

    SolicitudUnion solicitudUnion =
        SolicitudUnion.create(publicacion, usuario, EstadoSolicitud.SOLICITADA);
    SolicitudUnion saved = solicitudUnionRepositorio.save(solicitudUnion);
    return SolicitudUnionResult.from(saved);
  }

  private SolicitudUnionResult reenviarSolicitudSiProcede(
      SolicitudUnion solicitudUnion, PublicacionId publicacionId, UsuarioId usuarioId) {
    if (solicitudUnion.getEstadoSolicitud() == EstadoSolicitud.SOLICITADA) {
      throw new ApplicationException(
          "Ya existe una solicitud para esta publicacion por el usuario");
    }

    if (usuarioPerteneceAlGrupo(publicacionId, usuarioId)) {
      throw new ApplicationException("El usuario ya pertenece al grupo");
    }

    solicitudUnion.cambiarEstado(EstadoSolicitud.SOLICITADA);
    SolicitudUnion saved = solicitudUnionRepositorio.save(solicitudUnion);
    return SolicitudUnionResult.from(saved);
  }

  private boolean usuarioPerteneceAlGrupo(PublicacionId publicacionId, UsuarioId usuarioId) {
    return grupoJuegoRepositorio.findByPublicacionId(publicacionId).map(GrupoJuego::getId).map(
        grupoId -> grupoJuegoUsuarioRepositorio.existsByGrupoIdAndUsuarioId(grupoId, usuarioId))
        .orElse(false);
  }
}
