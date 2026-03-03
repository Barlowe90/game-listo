package com.gamelisto.busquedas.infrastructure.messaging.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record VideojuegoCreadoEventDto(
    String eventId, long gameId, String title, List<String> alternativeNames) {}
