package com.gamelisto.social.dominio;

import java.util.Objects;
import java.util.UUID;

public final class UserRef {

  private final UUID id;
  private final String username;
  private final String avatar;
  private final String discordUserId;
  private final String discordUsername;

  public UserRef(UUID id, String username, String avatar) {
    this(id, username, avatar, null, null);
  }

  public UserRef(
      UUID id, String username, String avatar, String discordUserId, String discordUsername) {
    this.id = Objects.requireNonNull(id, "id no puede ser null");
    this.username = username;
    this.avatar = avatar;
    this.discordUserId = discordUserId;
    this.discordUsername = discordUsername;
  }

  public static UserRef of(UUID id, String username, String avatar) {
    return new UserRef(id, username, avatar);
  }

  public static UserRef of(
      UUID id, String username, String avatar, String discordUserId, String discordUsername) {
    return new UserRef(id, username, avatar, discordUserId, discordUsername);
  }

  public UUID id() {
    return id;
  }

  public String username() {
    return username;
  }

  public String avatar() {
    return avatar;
  }

  public String discordUserId() {
    return discordUserId;
  }

  public String discordUsername() {
    return discordUsername;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserRef other)) return false;
    return Objects.equals(id, other.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "UserRef{id='" + id + "', username='" + username + "'}";
  }
}
