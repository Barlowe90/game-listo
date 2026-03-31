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
  private final String discordUserId;

  private UsuarioRef(UUID id, String username, String avatar, String discordUserId) {
    this.id = id;
    this.username = username;
    this.avatar = avatar;
    this.discordUserId = discordUserId;
  }

  public static UsuarioRef create(UUID id, String username, String avatar) {
    return create(id, username, avatar, null);
  }

  public static UsuarioRef reconstitute(UUID id, String username, String avatar) {
    return reconstitute(id, username, avatar, null);
  }

  public static UsuarioRef create(UUID id, String username, String avatar, String discordUserId) {
    return new UsuarioRef(id, username, avatar, discordUserId);
  }

  public static UsuarioRef reconstitute(
      UUID id, String username, String avatar, String discordUserId) {
    return new UsuarioRef(id, username, avatar, discordUserId);
  }
}
