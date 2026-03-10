package com.gamelisto.biblioteca.application.usecase;

import java.util.UUID;

public record RateGameEstadoCommand(UUID userId, String gameId, Double rating) {}
