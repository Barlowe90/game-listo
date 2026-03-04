package com.gamelisto.biblioteca.infrastructure.in.api.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** DTO que mapea el evento UsuarioCreado publicado por el servicio usuarios. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioCreadoEventDto(
    String usuarioId, String username, String email, String avatar, String role) {}
