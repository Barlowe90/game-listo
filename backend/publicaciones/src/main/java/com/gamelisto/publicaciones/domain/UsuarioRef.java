package com.gamelisto.publicaciones.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class UsuarioRef {
  private final UUID id;
  private final String username;
  private final String avatar;

  private UsuarioRef(UUID id, String username, String avatar) {
    this.id = id;
    this.username = username;
    this.avatar = avatar;
  }

  public static UsuarioRef create(UUID id, String username, String avatar) {
    return new UsuarioRef(id, username, avatar);
  }

  public static UsuarioRef reconstitute(UUID id, String username, String avatar) {
    return new UsuarioRef(id, username, avatar);
  }
}
