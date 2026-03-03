package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddGameToListUseCase implements AddGameToListHandler {

  private final ListaGameRepositorio listaGameRepositorio;
  private final ListaGameItemRepositorio listaGameItemRepositorio;

  public AddGameToListUseCase(
      ListaGameRepositorio listaGameRepositorio,
      ListaGameItemRepositorio listaGameItemRepositorio) {
    this.listaGameRepositorio = listaGameRepositorio;
    this.listaGameItemRepositorio = listaGameItemRepositorio;
  }

  @Transactional
  public ListaGameResult execute(String userId, String listaId, String gameId) {
    com.gamelisto.biblioteca.domain.UsuarioId userUuid = com.gamelisto.biblioteca.domain.UsuarioId.fromString(userId);
    ListaGameId listaUuid = ListaGameId.of(java.util.UUID.fromString(listaId));
    com.gamelisto.biblioteca.domain.GameId gameRefId = com.gamelisto.biblioteca.domain.GameId
        .of(Long.parseLong(gameId));

    ListaGame listaGame = listaGameRepositorio
        .findById(listaUuid)
        .orElseThrow(() -> new ApplicationException("No se encuentra la lista " + listaUuid));

    if (!listaGame.getUsuarioRefId().equals(userUuid)) {
      throw new ApplicationException("Usuario no propietario de la lista");
    }

    if (listaGame.getTipo() != Tipo.PERSONALIZADA) {
      throw new ApplicationException("Solo se pueden modificar listas personalizadas");
    }

    listaGameItemRepositorio.add(listaUuid, gameRefId);

    return ListaGameResult.from(listaGame);
  }
}
