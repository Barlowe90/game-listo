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
  private final String discordUsername;

  private UsuarioRef(
      UUID id, String username, String avatar, String discordUserId, String discordUsername) {
    this.id = id;
    this.username = username;
    this.avatar = avatar;
    this.discordUserId = discordUserId;
    this.discordUsername = discordUsername;
  }

  public static UsuarioRef create(UUID id, String username, String avatar) {
    return create(id, username, avatar, null, null);
  }

  public static UsuarioRef reconstitute(UUID id, String username, String avatar) {
    return reconstitute(id, username, avatar, null, null);
  }

  public static UsuarioRef create(
      UUID id, String username, String avatar, String discordUserId, String discordUsername) {
    return new UsuarioRef(id, username, avatar, discordUserId, discordUsername);
  }

  public static UsuarioRef reconstitute(
      UUID id, String username, String avatar, String discordUserId, String discordUsername) {
    return new UsuarioRef(id, username, avatar, discordUserId, discordUsername);
  }
}
