package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.GrupoJuegoRepositorio;
import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscarTodasLasPublicacionesUseCase implements BuscarTodasLasPublicacionesHandler {

  private final PublicacionRepositorio publicacionRepositorio;
  private final GrupoJuegoRepositorio grupoJuegoRepositorio;

  @Override
  public List<PublicacionResult> execute() {
    return publicacionRepositorio.findAll().stream()
        .map(
            publicacion ->
                PublicacionResult.from(
                    publicacion,
                    grupoJuegoRepositorio.findByPublicacionId(publicacion.getId()).orElse(null)))
        .toList();
  }
}
