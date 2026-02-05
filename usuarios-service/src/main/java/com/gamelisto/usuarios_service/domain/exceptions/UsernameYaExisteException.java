package com.gamelisto.usuarios_service.domain.exceptions;

import lombok.Getter;

@Getter
public class UsernameYaExisteException extends RuntimeException {

  private final String username;

  public UsernameYaExisteException(String username) {
    super(String.format("El username '%s' ya está en uso", username));
    this.username = username;
  }
}
