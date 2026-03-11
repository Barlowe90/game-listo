package com.gamelisto.biblioteca.infrastructure.in.api.dto;

import com.gamelisto.biblioteca.application.usecase.RateGameEstadoCommand;

import java.util.UUID;

public record RateGameEstadoRequest(Double rating) {
  public RateGameEstadoCommand toCommand(UUID userId, String gameRefId) {
    return new RateGameEstadoCommand(userId, gameRefId, rating);
  }
}
