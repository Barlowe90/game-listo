package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AceptarORechazarPeticionUseCase implements AceptarORechazarPeticionHandle {

  private final PeticionUnionRepositorio peticionUnionRepositorio;
  private final PublicacionRepositorio publicacionRepositorio;

  @Override
  public PeticionUnionResult execute(PeticionUnionCommand command) {
    UUID peticionUnionId = command.peticionUnionId();
    EstadoSolicitud nuevoEstado = EstadoSolicitud.valueOf(command.estadoSolicitud());

    PeticionUnion peticionUnion =
        peticionUnionRepositorio
            .findById(peticionUnionId)
            .orElseThrow(() -> new ApplicationException("Peticion no encontrada"));

    Publicacion publicacion =
        publicacionRepositorio
            .findById(peticionUnion.getId())
            .orElseThrow(() -> new ApplicationException("Publicacion no encontrada"));

    if (!publicacion.getAutorId().equals(command.userId())) {
      throw new ApplicationException("Usuario no propietario");
    }

    peticionUnion.cambiarEstado(nuevoEstado);

    PeticionUnion saved = peticionUnionRepositorio.save(peticionUnion);
    return PeticionUnionResult.from(saved);
  }
}
