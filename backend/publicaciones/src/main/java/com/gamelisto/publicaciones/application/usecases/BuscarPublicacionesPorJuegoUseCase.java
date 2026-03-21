package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.domain.GrupoJuegoRepositorio;
import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import com.gamelisto.publicaciones.domain.vo.GameId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarPublicacionesPorJuegoUseCase implements BuscarPublicacionesPorJuegoHandler {

  private final PublicacionRepositorio publicacionRepositorio;
  private final GrupoJuegoRepositorio grupoJuegoRepositorio;

  @Override
  public List<PublicacionResult> execute(Long gameId) {
    return publicacionRepositorio.findByGameId(GameId.of(gameId)).stream()
        .map(
            publicacion ->
                PublicacionResult.from(
                    publicacion,
                    grupoJuegoRepositorio.findByPublicacionId(publicacion.getId()).orElse(null)))
        .toList();
  }
}
