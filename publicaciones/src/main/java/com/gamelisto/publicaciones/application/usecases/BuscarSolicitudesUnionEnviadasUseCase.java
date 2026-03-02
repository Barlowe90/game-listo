package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.SolicitudUnionRepositorio;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarSolicitudesUnionEnviadasUseCase
    implements BuscarSolicitudesUnionEnviadasHandler {

  private final SolicitudUnionRepositorio solicitudUnionRepositorio;

  @Override
  public List<SolicitudUnionResult> execute(UUID userId) {
    return solicitudUnionRepositorio.findByUsuarioId(UsuarioId.of(userId)).stream()
        .map(SolicitudUnionResult::from)
        .toList();
  }
}
