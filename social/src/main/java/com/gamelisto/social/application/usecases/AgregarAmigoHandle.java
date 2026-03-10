package com.gamelisto.social.application.usecases;

import java.util.UUID;

public interface AgregarAmigoHandle {
  void execute(UUID userId, UUID friendId);
}
