package com.gamelisto.social.application.usecases;

import java.util.List;
import java.util.UUID;

public interface ListarAmigosHandle {
  List<UserRefResult> execute(UUID userId);
}
