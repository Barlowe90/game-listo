package com.gamelisto.biblioteca.infrastructure.in.api.dto;

import com.gamelisto.biblioteca.application.usecase.ImportarBibliotecaSteamResult;

public record ImportarBibliotecaSteamResponse(
    String listaId,
    String listaNombre,
    int steamOwnedCount,
    int resolvedCount,
    int addedCount,
    int alreadyPresentCount,
    int unresolvedCount) {

  public static ImportarBibliotecaSteamResponse from(ImportarBibliotecaSteamResult result) {
    return new ImportarBibliotecaSteamResponse(
        result.listaId(),
        result.listaNombre(),
        result.steamOwnedCount(),
        result.resolvedCount(),
        result.addedCount(),
        result.alreadyPresentCount(),
        result.unresolvedCount());
  }
}
