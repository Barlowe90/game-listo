package com.gamelisto.usuarios.domain.usuario;

import java.util.regex.Pattern;

public final class Username {

  private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{3,30}$");

  private final String value;

  private Username(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("El username no puede ser nulo o vacío");
    }

    String usernameNormalized = value.trim();

    if (!cumplePatronUsername(usernameNormalized)) {
      throw new IllegalArgumentException(
          "El username debe tener entre 3 y 30 caracteres y solo puede contener letras, números, guiones y guiones bajos");
    }

    this.value = usernameNormalized;
  }

  private boolean cumplePatronUsername(String usernameNormalized) {
    return USERNAME_PATTERN.matcher(usernameNormalized).matches();
  }

  public static Username of(String value) {
    return new Username(value);
  }

  public String value() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
