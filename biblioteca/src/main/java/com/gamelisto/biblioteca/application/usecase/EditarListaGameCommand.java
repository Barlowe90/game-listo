package com.gamelisto.biblioteca.application.usecase;

public record EditarListaGameCommand(String userId, String listaId, String nombre) {}
