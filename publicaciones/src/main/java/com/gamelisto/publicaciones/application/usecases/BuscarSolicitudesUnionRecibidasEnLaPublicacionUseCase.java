package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.SolicitudUnionRepositorio;
import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarSolicitudesUnionRecibidasEnLaPublicacionUseCase
    implements BuscarSolicitudesUnionRecibidasEnLaPublicacionHandler {

  private final SolicitudUnionRepositorio solicitudUnionRepositorio;
  private final PublicacionRepositorio publicacionRepositorio;

  @Override
  public List<SolicitudUnionResult> execute(UUID userId, UUID publicacionId) {

    validarUsuarioEsAutorPublicacion(userId, publicacionId);

    return solicitudUnionRepositorio.findByPublicacionId(PublicacionId.of(publicacionId)).stream()
        .map(SolicitudUnionResult::from)
        .toList();
  }

  private void validarUsuarioEsAutorPublicacion(UUID userId, UUID publicacionId) {
    Publicacion publicacion =
        publicacionRepositorio
            .findById(PublicacionId.of(publicacionId))
            .orElseThrow(() -> new ApplicationException("Publicacion no encontrada"));

    if (!publicacion.getAutorId().equals(userId)) {
      throw new ApplicationException("Usuario no autorizado");
    }
  }
}
