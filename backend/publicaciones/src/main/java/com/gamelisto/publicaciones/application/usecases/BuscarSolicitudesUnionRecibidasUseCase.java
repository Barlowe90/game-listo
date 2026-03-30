package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.SolicitudUnionRepositorio;
import java.util.List;
import java.util.UUID;

import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarSolicitudesUnionRecibidasUseCase
    implements BuscarSolicitudesUnionRecibidasHandler {

  private final SolicitudUnionRepositorio solicitudUnionRepositorio;
  private final PublicacionRepositorio publicacionRepositorio;

  @Override
  public List<SolicitudUnionResult> execute(UUID userId) {

    List<PublicacionId> publicacionesIds = recuperarPublicacionesDondeSoyAutor(userId);

    if (publicacionesIds.isEmpty()) {
      return List.of();
    }

    return solicitudUnionRepositorio.findByPublicacionIdIn(publicacionesIds).stream()
        .map(SolicitudUnionResult::from)
        .toList();
  }

  private @NonNull List<PublicacionId> recuperarPublicacionesDondeSoyAutor(UUID userId) {
    return publicacionRepositorio.findByAutorId(UsuarioId.of(userId)).stream()
        .map(Publicacion::getId)
        .toList();
  }
}
