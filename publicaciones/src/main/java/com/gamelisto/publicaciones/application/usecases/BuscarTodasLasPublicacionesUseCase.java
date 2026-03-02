package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscarTodasLasPublicacionesUseCase implements BuscarTodasLasPublicacionesHandler {

  private final PublicacionRepositorio publicacionRepositorio;

  @Override
  public List<PublicacionResult> execute() {
    return publicacionRepositorio.findAll().stream().map(PublicacionResult::from).toList();
  }
}
