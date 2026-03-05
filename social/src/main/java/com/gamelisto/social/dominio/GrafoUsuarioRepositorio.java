package com.gamelisto.social.dominio;

public interface GrafoUsuarioRepositorio {

  void upsertUser(UserRef user);

  void deleteUser(String userId);
}
