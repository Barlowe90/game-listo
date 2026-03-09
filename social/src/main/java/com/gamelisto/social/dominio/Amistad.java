package com.gamelisto.social.dominio;

import com.gamelisto.social.dominio.exceptions.DomainException;

import java.util.Objects;
import java.util.UUID;

public final class Amistad {
  private final UUID userAId;
  private final UUID userBId;

  private Amistad(UUID userAId, UUID userBId) {
    this.userAId = Objects.requireNonNull(userAId, "userAId no puede ser null");
    this.userBId = Objects.requireNonNull(userBId, "userBId no puede ser null");
    if (this.userAId.equals(this.userBId)) {
      throw new DomainException("No se puede crear amistad con uno mismo");
    }
  }

  public static Amistad of(UUID id1, UUID id2) {
    if (id1 == null || id2 == null) {
      throw new DomainException("Los ids de usuario no pueden ser null");
    }
    if (id1.equals(id2)) {
      throw new DomainException("No se puede crear amistad con uno mismo");
    }

    // Order ids deterministically to avoid duplicate edges (smaller UUID first by compareTo)
    if (id1.toString().compareTo(id2.toString()) <= 0) {
      return new Amistad(id1, id2);
    } else {
      return new Amistad(id2, id1);
    }
  }

  public UUID userAId() {
    return userAId;
  }

  public UUID userBId() {
    return userBId;
  }
}
