package com.gamelisto.publicaciones.application.usecases;

import java.util.UUID;

public interface BuscarGrupoJuegoHandle {
  GrupoJuegoResult execute(UUID grupoJuegoId);
}
