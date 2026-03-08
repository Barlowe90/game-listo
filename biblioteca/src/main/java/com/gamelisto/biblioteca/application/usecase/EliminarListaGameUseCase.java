package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarListaGameUseCase implements EliminarListaGameHandler {

  private final ListaGameRepositorio listaGameRepositorio;

  @Transactional
  public void execute(UUID userId, String listaId) {

    EntradaEliminarListaGame result = mapearEntrada(userId, listaId);
    ListaGame listaGame = obtenerListaPorIdOrThrow(result);

    comprobarSiEsListaPredefinidaOrThrow(listaGame);

    comprobarUsuarioPropietarioOrThrow(result, listaGame);

    eliminarLista(listaGame);
  }

  private static void comprobarUsuarioPropietarioOrThrow(
      EntradaEliminarListaGame result, ListaGame listaGame) {
    if (!result.userUuid.equals(listaGame.getUsuarioRefId())) {
      throw new ApplicationException("Usuario no propietario de la lista");
    }
  }

  private static void comprobarSiEsListaPredefinidaOrThrow(ListaGame listaGame) {
    if (listaGame.getTipo().equals(Tipo.OFICIAL)) {
      throw new ApplicationException("No se puede eliminar una lista predeterminada");
    }
  }

  private void eliminarLista(ListaGame listaGame) {
    listaGameRepositorio.deleteById(listaGame.getId());
  }

  private ListaGame obtenerListaPorIdOrThrow(EntradaEliminarListaGame result) {
    return listaGameRepositorio
        .findById(result.idLista())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra la lista " + result.idLista()));
  }

  private static EntradaEliminarListaGame mapearEntrada(UUID userId, String listId) {
    UsuarioId userUuid = UsuarioId.of(userId);
    ListaGameId id = ListaGameId.of(UUID.fromString(listId));
    return new EntradaEliminarListaGame(userUuid, id);
  }

  private record EntradaEliminarListaGame(UsuarioId userUuid, ListaGameId idLista) {}
}
