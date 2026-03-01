package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EliminarPublicacionUseCase implements EliminarPublicacionHandler {
  private final PublicacionRepositorio publicacionRepositorio;

  @Override
  public void execute(UUID publicacionId, UUID userId) {

    Publicacion publicacion =
        publicacionRepositorio
            .findById(publicacionId)
            .orElseThrow(() -> new ApplicationException("Publicacion no encontrada"));

    if (!publicacion.getAutorId().equals(userId)) {
      throw new ApplicationException("Usuario no propietario");
    }

    publicacionRepositorio.deleteById(publicacionId);
  }
}
