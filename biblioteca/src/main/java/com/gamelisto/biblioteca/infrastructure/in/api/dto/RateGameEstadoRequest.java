package com.gamelisto.biblioteca.infrastructure.in.api.dto;

import com.gamelisto.biblioteca.application.usecase.RateGameEstadoCommand;

public record RateGameEstadoRequest(Double rating) {
  public RateGameEstadoCommand toCommand(String userId, String gameRefId) {
    return new RateGameEstadoCommand(userId, gameRefId, rating);
  }
}
