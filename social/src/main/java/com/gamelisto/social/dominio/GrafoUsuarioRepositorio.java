package com.gamelisto.social.dominio;

import java.util.UUID;

public interface GrafoUsuarioRepositorio {

  void upsertUser(UserRef user);

  void deleteUser(UUID userId);
}
