package com.gamelisto.social.application.usecases;

import java.util.UUID;

public interface EliminarAmigoHandle {
  void execute(UUID userId, UUID friendId);
}
