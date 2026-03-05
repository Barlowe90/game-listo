package com.gamelisto.biblioteca.infrastructure.in.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** DTO que mapea el evento GameCreado publicado por el servicio catalogo. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GameCreadoEventDto(Long id, String name, String portada) {}
