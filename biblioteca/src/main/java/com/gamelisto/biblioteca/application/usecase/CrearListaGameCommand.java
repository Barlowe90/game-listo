package com.gamelisto.biblioteca.application.usecase;

import java.util.UUID;

public record CrearListaGameCommand(UUID userId, String nombre, String tipo) {}
