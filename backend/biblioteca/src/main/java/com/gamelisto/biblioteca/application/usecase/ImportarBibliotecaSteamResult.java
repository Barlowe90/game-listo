package com.gamelisto.biblioteca.application.usecase;

public record ImportarBibliotecaSteamResult(
    String listaId,
    String listaNombre,
    int steamOwnedCount,
    int resolvedCount,
    int addedCount,
    int alreadyPresentCount,
    int unresolvedCount) {}
