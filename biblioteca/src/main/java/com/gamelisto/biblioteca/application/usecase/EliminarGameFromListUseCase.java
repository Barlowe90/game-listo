package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarGameFromListUseCase implements EliminarGameFromListHandler {

  private final ListaGameRepositorio listaGameRepositorio;
  private final ListaGameItemRepositorio listaGameItemRepositorio;

  @Transactional
  public ListaGameResult execute(UUID userId, String listaId, String gameId) {
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
      UUID userId, String listaId, String gameId) {
    UsuarioId userUuid = UsuarioId.of(userId);
    ListaGameId listaUuid = ListaGameId.of(UUID.fromString(listaId));
    GameId gameRefId = GameId.of(Long.parseLong(gameId));

    return new EntradaBuscarListaGame(userUuid, listaUuid, gameRefId);
  }

  private record EntradaBuscarListaGame(
      UsuarioId userUuid, ListaGameId listaId, GameId gameRefId) {}
}
