package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.EstadoSolicitud;
import com.gamelisto.publicaciones.domain.PeticionUnion;
import com.gamelisto.publicaciones.domain.PeticionUnionRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CrearSolicitudUnionUseCase implements CrearSolicitudUnionHandler {

  private final PeticionUnionRepositorio peticionUnionRepositorio;

  @Override
  public PeticionUnionResult execute(UUID publicacionId, UUID userId) {
    PeticionUnion peticionUnion =
        PeticionUnion.create(publicacionId, userId, EstadoSolicitud.SOLICITADA);
    PeticionUnion saved = peticionUnionRepositorio.save(peticionUnion);
    return PeticionUnionResult.from(saved);
  }
}
