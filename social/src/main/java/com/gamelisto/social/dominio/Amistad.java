package com.gamelisto.social.dominio;

import java.util.Objects;

public final class Amistad {
  private final String userAId;
  private final String userBId;

  private Amistad(String userAId, String userBId) {
    this.userAId = Objects.requireNonNull(userAId, "userAId no puede ser null");
    this.userBId = Objects.requireNonNull(userBId, "userBId no puede ser null");
    if (this.userAId.equals(this.userBId)) {
      throw new IllegalArgumentException("No se puede crear amistad con uno mismo");
    }
  }

  public static Amistad of(String id1, String id2) {
    if (id1 == null || id2 == null) {
      throw new IllegalArgumentException("Los ids de usuario no pueden ser null");
    }
    String a = id1.trim();
    String b = id2.trim();
    if (a.isEmpty() || b.isEmpty()) {
      throw new IllegalArgumentException("Los ids de usuario no pueden estar vacíos");
    }
    if (a.equals(b)) {
      throw new IllegalArgumentException("No se puede crear amistad con uno mismo");
    }
    if (a.compareTo(b) <= 0) {
      return new Amistad(a, b);
    } else {
      return new Amistad(b, a);
    }
  }

  public String userAId() {
    return userAId;
  }

  public String userBId() {
    return userBId;
  }
}
