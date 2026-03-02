package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.PeticionUnionRepositorio;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarPeticionesUnionEnviadasUseCase implements BuscarPeticionesUnionEnviadasHandler {

  private final PeticionUnionRepositorio peticionUnionRepositorio;

  @Override
  public List<PeticionUnionResult> execute(UUID userId) {
    return peticionUnionRepositorio.findByUsuarioId(UsuarioId.of(userId)).stream()
        .map(PeticionUnionResult::from)
        .toList();
  }
}
