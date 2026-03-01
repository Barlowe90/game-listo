package com.gamelisto.publicaciones.application.usecases;

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

  @Override
  public List<PeticionUnionResult> execute(UUID userId, UUID publicacionId) {

    return peticionUnionRepositorio.findByPublicacionIdAndUsuarioId(publicacionId, userId).stream()
        .map(PeticionUnionResult::from)
        .toList();
  }
}
