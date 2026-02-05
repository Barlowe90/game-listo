package com.gamelisto.usuarios_service.domain.exceptions;

import lombok.Getter;

@Getter
public class EmailYaRegistradoException extends RuntimeException {

  private final String email;

  public EmailYaRegistradoException(String email) {
    super(String.format("El email '%s' ya está registrado", email));
    this.email = email;
  }
}
