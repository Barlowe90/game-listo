package com.gamelisto.social.application.usecases;

import java.util.UUID;

public interface SincronizarGameEstadoSocialHandler {
  void execute(UUID userId, Long gameId, String estado);
}
