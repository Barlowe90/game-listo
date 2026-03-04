package com.gamelisto.biblioteca.infrastructure.in.api.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** DTO que mapea el evento UsuarioEliminado publicado por el servicio usuarios. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioEliminadoEventDto(String usuarioId) {}
