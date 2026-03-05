package com.gamelisto.social.dominio;

import java.util.Objects;

public final class UserRef {

  private final String id;
  private final String username;
  private final String avatar;

  public UserRef(String id, String username, String avatar) {
    this.id = Objects.requireNonNull(id, "id no puede ser null");
    this.username = username;
    this.avatar = avatar;
  }

  public static UserRef of(String id, String username, String avatar) {
    return new UserRef(id, username, avatar);
  }

  public String id() {
    return id;
  }

  public String username() {
    return username;
  }

  public String avatar() {
    return avatar;
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
