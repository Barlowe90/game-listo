package com.gamelisto.social.application.usecases;

import java.util.UUID;

public interface ConsultarResumenSocialJuegoHandle {
  ResumenSocialJuegoResult execute(UUID usuarioId, Long gameId);
}
