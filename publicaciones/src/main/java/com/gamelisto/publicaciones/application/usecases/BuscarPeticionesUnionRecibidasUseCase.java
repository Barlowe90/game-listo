package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.PeticionUnionRepositorio;
import java.util.List;
import java.util.UUID;

import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarPeticionesUnionRecibidasUseCase
    implements BuscarPeticionesUnionRecibidasHandler {

  private final PeticionUnionRepositorio peticionUnionRepositorio;
  private final PublicacionRepositorio publicacionRepositorio;

  @Override
  public List<PeticionUnionResult> execute(UUID userId) {
    // recuperar las publicaciones donde soy autor
    List<UUID> publicacionesIds =
        publicacionRepositorio.findByAutorId(userId).stream().map(Publicacion::getId).toList();

    if (publicacionesIds.isEmpty()) {
      return List.of();
    }

    return peticionUnionRepositorio.findByPublicacionIdIn(publicacionesIds).stream()
        .map(PeticionUnionResult::from)
        .toList();
  }
}
