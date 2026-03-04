package com.gamelisto.busquedas.infrastructure.in.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** DTO que mapea el evento GameCreado publicado por el servicio catalogo. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GameCreadoEventDto(String id, String name, String plataforma) {}
