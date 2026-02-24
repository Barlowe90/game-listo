package com.gamelisto.biblioteca.application.usecase.eliminargamefromlist;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.application.usecase.ListaGameResult;
import com.gamelisto.biblioteca.domain.gameref.GameRef;
import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.ListaGameId;
import com.gamelisto.biblioteca.domain.repositories.RepositorioGameRef;
import com.gamelisto.biblioteca.domain.repositories.RepositorioLista;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EliminarGameFromListUseCase implements EliminarGameFromListHandler {

  private final RepositorioLista repositorioLista;
  private final RepositorioGameRef repositorioGameRef;

  public EliminarGameFromListUseCase(
      RepositorioLista repositorioLista, RepositorioGameRef repositorioGameRef) {
    this.repositorioLista = repositorioLista;
    this.repositorioGameRef = repositorioGameRef;
  }

  @Transactional
  public ListaGameResult execute(String listaId, String gameId) {
    EntradaBuscarListaGame result = mapearCommandAEntrada(listaId, gameId);

    ListaGame listaGame = obtenerListaPorIdOrThrow(result);
    GameRef gameRef = obtenerGameRefOrThrow(result);

    ListaGame listaGuardada = repositorioLista.save(listaGame);

    return ListaGameResult.from(listaGuardada);
  }

  private GameRef obtenerGameRefOrThrow(EntradaBuscarListaGame result) {
    return repositorioGameRef
        .findByCatalogGameId(result.catalogGameId())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra el gameRef " + result.catalogGameId()));
  }

  private ListaGame obtenerListaPorIdOrThrow(EntradaBuscarListaGame result) {
    return repositorioLista
        .findById(result.listaId())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra la lista " + result.listaId()));
  }

  private static EntradaBuscarListaGame mapearCommandAEntrada(String listaId, String gameId) {
    UUID uuidLista = UUID.fromString(listaId);
    Long catalogGameId = Long.parseLong(gameId);
    ListaGameId id = ListaGameId.of(uuidLista);

    return new EntradaBuscarListaGame(id, catalogGameId);
  }

  private record EntradaBuscarListaGame(ListaGameId listaId, Long catalogGameId) {}
}
