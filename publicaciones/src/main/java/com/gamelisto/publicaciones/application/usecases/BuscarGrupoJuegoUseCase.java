package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.GrupoJuego;
import com.gamelisto.publicaciones.domain.GrupoJuegoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarGrupoJuegoUseCase implements BuscarGrupoJuegoHandle {

  private final GrupoJuegoRepositorio grupoJuegoRepositorio;

  @Override
  public GrupoJuegoResult execute(UUID grupoJuegoId) {
    GrupoJuego grupoJuego =
        grupoJuegoRepositorio
            .findById(grupoJuegoId)
            .orElseThrow(() -> new ApplicationException("Grupo juego no encontrado"));

    return GrupoJuegoResult.from(grupoJuego);
  }
}
