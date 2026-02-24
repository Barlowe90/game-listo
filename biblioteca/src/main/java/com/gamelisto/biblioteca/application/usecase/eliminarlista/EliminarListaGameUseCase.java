package com.gamelisto.biblioteca.application.usecase.eliminarlista;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.ListaGameId;
import com.gamelisto.biblioteca.domain.listas.Tipo;
import com.gamelisto.biblioteca.domain.repositories.RepositorioLista;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EliminarListaGameUseCase implements EliminarListaGameHandler {

  private final RepositorioLista repositorioLista;

  public EliminarListaGameUseCase(RepositorioLista repositorioLista) {
    this.repositorioLista = repositorioLista;
  }

  @Transactional
  public void execute(String idLista) {
    EntradaEliminarListaGame result = mapearEntrada(idLista);
    ListaGame listaGame = obtenerListaPorIdOrThrow(result);
    comprobarSiEsListaPredefinidaOrThrow(listaGame);
    eliminarLista(listaGame);
  }

  private static void comprobarSiEsListaPredefinidaOrThrow(ListaGame listaGame) {
    if (listaGame.getTipo().equals(Tipo.OFICIAL)) {
      throw new ApplicationException("No se puede eliminar una lista predeterminada");
    }
  }

  private void eliminarLista(ListaGame listaGame) {
    repositorioLista.delete(listaGame);
  }

  private ListaGame obtenerListaPorIdOrThrow(EntradaEliminarListaGame result) {
    return repositorioLista
        .findById(result.idLista())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra la lista " + result.idLista()));
  }

  private static EntradaEliminarListaGame mapearEntrada(String idLista) {
    UUID uuidLista = UUID.fromString(idLista);
    ListaGameId id = ListaGameId.of(uuidLista);
    return new EntradaEliminarListaGame(id);
  }

  private record EntradaEliminarListaGame(ListaGameId idLista) {}
}
