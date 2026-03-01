package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarPublicacionesPorJuegoUseCase implements BuscarPublicacionesPorJuegoHandler {

  private final PublicacionRepositorio publicacionRepositorio;

  @Override
  public List<PublicacionResult> execute(Long gameId) {
    return publicacionRepositorio.findByGameId(gameId).stream()
        .map(PublicacionResult::from)
        .toList();
  }
}
