package com.gamelisto.busquedas.infrastructure.in.messaging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/** DTO que mapea el evento GameCreado publicado por el servicio catalogo. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GameCreadoEventDto(Long id, String name, List<String> alternativeNames) {}
