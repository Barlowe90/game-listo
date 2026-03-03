package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.*;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.SolicitudId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AceptarORechazarPeticionUseCase implements AceptarORechazarPeticionHandle {

  private final SolicitudUnionRepositorio solicitudUnionRepositorio;
  private final PublicacionRepositorio publicacionRepositorio;

  @Override
  public SolicitudUnionResult execute(SolicitudUnionCommand command) {
    UUID peticionUnionId = command.peticionUnionId();
    EstadoSolicitud nuevoEstado = EstadoSolicitud.valueOf(command.estadoSolicitud());

    SolicitudUnion solicitudUnion =
        solicitudUnionRepositorio
            .findById(SolicitudId.of(peticionUnionId))
            .orElseThrow(() -> new ApplicationException("Solicitud no encontrada"));

    Publicacion publicacion =
        publicacionRepositorio
            .findById(PublicacionId.of(solicitudUnion.getPublicacionId().value()))
            .orElseThrow(() -> new ApplicationException("Publicacion no encontrada"));

    if (!publicacion.getAutorId().equals(command.userId())) {
      throw new ApplicationException("Usuario no propietario");
    }

    solicitudUnion.cambiarEstado(nuevoEstado);

    SolicitudUnion saved = solicitudUnionRepositorio.save(solicitudUnion);
    return SolicitudUnionResult.from(saved);
  }
}
