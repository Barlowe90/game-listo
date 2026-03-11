package com.gamelisto.usuarios.domain.usuario;

import com.gamelisto.usuarios.domain.exceptions.DomainException;

public final class DiscordUserId {

  private final String value;
  private static final int MAX_ID_LENGTH = 100;

  private DiscordUserId(String value) {
    if (value != null && !value.trim().isEmpty()) {
      String trimmedValue = value.trim();
      if (trimmedValue.length() > MAX_ID_LENGTH) {
        throw new DomainException(
            "El ID de Discord no puede exceder " + MAX_ID_LENGTH + " caracteres");
      }
      this.value = trimmedValue;
    } else {
      this.value = null;
    }
  }

  public static DiscordUserId of(String value) {
    return new DiscordUserId(value);
  }

  public static DiscordUserId empty() {
    return new DiscordUserId(null);
  }

  public String value() {
    return value;
  }

  public boolean isEmpty() {
    return value == null;
  }

  @Override
  public String toString() {
    return value != null ? value : "[NO VINCULADO]";
  }
}
