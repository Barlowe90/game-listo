package com.gamelisto.biblioteca.application.usecase;

import java.util.UUID;

public record EditarListaGameCommand(UUID userId, String listaId, String nombre) {}
