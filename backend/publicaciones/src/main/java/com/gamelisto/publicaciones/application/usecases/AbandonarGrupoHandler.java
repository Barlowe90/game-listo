package com.gamelisto.publicaciones.application.usecases;

import java.util.UUID;

public interface AbandonarGrupoHandler {
  void execute(UUID publicacionId, UUID usuarioId);
}
