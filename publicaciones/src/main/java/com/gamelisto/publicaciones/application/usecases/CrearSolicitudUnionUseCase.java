package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.EstadoSolicitud;
import com.gamelisto.publicaciones.domain.PeticionUnion;
import com.gamelisto.publicaciones.domain.PeticionUnionRepositorio;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CrearSolicitudUnionUseCase implements CrearSolicitudUnionHandler {

  private final PeticionUnionRepositorio peticionUnionRepositorio;

  @Override
  public PeticionUnionResult execute(UUID publicacionId, UUID userId) {

    comprobarExistePeticion(publicacionId, userId);

    PeticionUnion peticionUnion =
        PeticionUnion.create(
            PublicacionId.of(publicacionId), UsuarioId.of(userId), EstadoSolicitud.SOLICITADA);
    PeticionUnion saved = peticionUnionRepositorio.save(peticionUnion);
    return PeticionUnionResult.from(saved);
  }

  private void comprobarExistePeticion(UUID publicacionId, UUID userId) {
    Optional<PeticionUnion> existente =
        peticionUnionRepositorio.findByPublicacionIdAndUsuarioId(
            PublicacionId.of(publicacionId), UsuarioId.of(userId));

    if (existente.isPresent()) {
      throw new ApplicationException(
          "Ya existe una solicitud para esta publicación por el usuario");
    }
  }
}
