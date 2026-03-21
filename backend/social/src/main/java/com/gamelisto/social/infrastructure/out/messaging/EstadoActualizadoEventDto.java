package com.gamelisto.social.infrastructure.out.messaging;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EstadoActualizadoEventDto(
    UUID usuarioId,
    @JsonAlias("gameRef") Long gameId,
    String estado) {}
