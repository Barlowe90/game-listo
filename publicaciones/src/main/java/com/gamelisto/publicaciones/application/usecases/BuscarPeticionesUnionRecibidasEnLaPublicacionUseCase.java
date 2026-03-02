package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.PeticionUnionRepositorio;
import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarPeticionesUnionRecibidasEnLaPublicacionUseCase
    implements BuscarPeticionesUnionRecibidasEnLaPublicacionHandler {

  private final PeticionUnionRepositorio peticionUnionRepositorio;
  private final PublicacionRepositorio publicacionRepositorio;

  @Override
  public List<PeticionUnionResult> execute(UUID userId, UUID publicacionId) {

    validarUsuarioEsAutorPublicacion(userId, publicacionId);

    return peticionUnionRepositorio.findByPublicacionId(publicacionId).stream()
        .map(PeticionUnionResult::from)
        .toList();
  }

  private void validarUsuarioEsAutorPublicacion(UUID userId, UUID publicacionId) {
    Publicacion publicacion =
        publicacionRepositorio
            .findById(publicacionId)
            .orElseThrow(() -> new ApplicationException("Publicacion no encontrada"));

    if (!publicacion.getAutorId().equals(userId)) {
      throw new ApplicationException("Usuario no autorizado");
    }
  }
}
