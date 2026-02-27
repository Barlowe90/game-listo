package com.gamelisto.biblioteca.application.usecase;

public record RateGameEstadoCommand(String userId, String gameId, Double rating) {}
