package com.gamelisto.biblioteca.domain;

import com.gamelisto.biblioteca.domain.exceptions.DomainException;

import java.util.Objects;
import java.util.regex.Pattern;

public final class NombreListaGame {

  // Permitir letras, números, guiones, guiones bajos y espacios, longitud entre 3 y 30
  private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9 _-]{3,30}$");

  private final String value;

  private NombreListaGame(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new DomainException("El nombre de la lista no puede ser nulo o vacío");
    }

    String usernameNormalized = value.trim();

    if (!cumplePatronUsername(usernameNormalized)) {
      throw new DomainException(
          "El nombre de la lista debe tener entre 3 y 30 caracteres y solo puede contener letras, números, espacios, guiones y guiones bajos");
    }

    this.value = usernameNormalized;
  }

  private boolean cumplePatronUsername(String usernameNormalized) {
    return USERNAME_PATTERN.matcher(usernameNormalized).matches();
  }

  public static NombreListaGame of(String value) {
    return new NombreListaGame(value);
  }

  public String value() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    NombreListaGame that = (NombreListaGame) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
