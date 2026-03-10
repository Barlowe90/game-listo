package com.gamelisto.usuarios.domain.usuario;

import java.util.regex.Pattern;
import com.gamelisto.usuarios.domain.exceptions.DomainException;

public final class Email {

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

  private final String value;
  private static final int MAX_LENGTH = 255;

  private Email(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new DomainException("El email no puede ser nulo o vacío");
    }

    String normalizedEmail = value.trim().toLowerCase();

    if (!cumplePatronEmail(normalizedEmail)) {
      throw new DomainException("El formato del email es inválido: " + value);
    }

    if (normalizedEmail.length() > MAX_LENGTH) {
      throw new DomainException("El email no puede exceder " + MAX_LENGTH + " caracteres");
    }

    this.value = normalizedEmail;
  }

  private boolean cumplePatronEmail(String normalizedEmail) {
    return EMAIL_PATTERN.matcher(normalizedEmail).matches();
  }

  public static Email of(String value) {
    return new Email(value);
  }

  public String value() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Email email = (Email) o;
    return value.equals(email.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return value;
  }
}
