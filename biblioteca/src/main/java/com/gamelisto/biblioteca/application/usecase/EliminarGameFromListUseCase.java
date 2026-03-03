package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EliminarGameFromListUseCase implements EliminarGameFromListHandler {

  private final ListaGameRepositorio listaGameRepositorio;
  private final ListaGameItemRepositorio listaGameItemRepositorio;

  public EliminarGameFromListUseCase(
      ListaGameRepositorio listaGameRepositorio,
      ListaGameItemRepositorio listaGameItemRepositorio) {
    this.listaGameRepositorio = listaGameRepositorio;
    this.listaGameItemRepositorio = listaGameItemRepositorio;
  }

  @Transactional
  public ListaGameResult execute(String userId, String listaId, String gameId) {
    EntradaBuscarListaGame result = mapearCommandAEntrada(userId, listaId, gameId);

    ListaGame listaGame = obtenerListaPorIdOrThrow(result);

    if (!listaGame.getUsuarioRefId().equals(result.userUuid())) {
      throw new ApplicationException("Usuario no propietario de la lista");
    }

    if (listaGame.getTipo() != Tipo.PERSONALIZADA) {
      throw new ApplicationException("Solo se pueden modificar listas personalizadas");
    }

    listaGameItemRepositorio.remove(result.listaId, result.gameRefId);

    return ListaGameResult.from(listaGame);
  }

  private ListaGame obtenerListaPorIdOrThrow(EntradaBuscarListaGame result) {
    return listaGameRepositorio
        .findById(result.listaId())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra la lista " + result.listaId()));
  }

  private static EntradaBuscarListaGame mapearCommandAEntrada(
      String userId, String listaId, String gameId) {
    com.gamelisto.biblioteca.domain.UsuarioId userUuid = com.gamelisto.biblioteca.domain.UsuarioId.fromString(userId);
    ListaGameId listaUuid = ListaGameId.of(java.util.UUID.fromString(listaId));
    com.gamelisto.biblioteca.domain.GameId gameRefId = com.gamelisto.biblioteca.domain.GameId
        .of(Long.parseLong(gameId));

    return new EntradaBuscarListaGame(userUuid, listaUuid, gameRefId);
  }

  private record EntradaBuscarListaGame(com.gamelisto.biblioteca.domain.UsuarioId userUuid, ListaGameId listaId,
      com.gamelisto.biblioteca.domain.GameId gameRefId) {
  }
}
