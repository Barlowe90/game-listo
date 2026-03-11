package com.gamelisto.biblioteca.application.usecase;

import java.util.UUID;

public record CrearGameEstadoCommand(UUID userId, String gameId, String estado) {}
