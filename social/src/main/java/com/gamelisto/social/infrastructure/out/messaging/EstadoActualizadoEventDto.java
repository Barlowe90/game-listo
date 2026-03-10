package com.gamelisto.social.infrastructure.out.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record EstadoActualizadoEventDto(UUID usuarioId, Long gameRef, String estado) {}
