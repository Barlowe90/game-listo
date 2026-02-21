package com.gamelisto.biblioteca.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Getter
@ToString
public class UsuarioRef {
  private final UUID id;
  private final String username;
  private final String avatar;

  private UsuarioRef(String username, String avatar) {
    this.id = UUID.randomUUID();
    this.username = Objects.requireNonNull(username, "username no puede ser null");
    this.avatar = avatar;
  }

  // Reconstitución con id
  private UsuarioRef(UUID id, String username, String avatar) {
    this.id = Objects.requireNonNull(id, "id no puede ser null");
    this.username = Objects.requireNonNull(username, "username no puede ser null");
    this.avatar = avatar;
  }

  public static UsuarioRef create(String username, String avatar) {
    return new UsuarioRef(username, avatar);
  }

  public static UsuarioRef reconstitute(UUID id, String username, String avatar) {
    return new UsuarioRef(id, username, avatar);
  }
}
