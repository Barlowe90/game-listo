package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.EstadoSolicitud;
import com.gamelisto.publicaciones.domain.SolicitudUnion;
import com.gamelisto.publicaciones.domain.SolicitudUnionRepositorio;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CrearSolicitudUnionUseCase implements CrearSolicitudUnionHandler {

  private final SolicitudUnionRepositorio solicitudUnionRepositorio;

  @Override
  public SolicitudUnionResult execute(UUID publicacionId, UUID userId) {

    comprobarExisteSolicitud(publicacionId, userId);

    SolicitudUnion solicitudUnion =
        SolicitudUnion.create(
            PublicacionId.of(publicacionId), UsuarioId.of(userId), EstadoSolicitud.SOLICITADA);
    SolicitudUnion saved = solicitudUnionRepositorio.save(solicitudUnion);
    return SolicitudUnionResult.from(saved);
  }

  private void comprobarExisteSolicitud(UUID publicacionId, UUID userId) {
    Optional<SolicitudUnion> existente =
        solicitudUnionRepositorio.findByPublicacionIdAndUsuarioId(
            PublicacionId.of(publicacionId), UsuarioId.of(userId));

    if (existente.isPresent()) {
      throw new ApplicationException(
          "Ya existe una solicitud para esta publicación por el usuario");
    }
  }
}
