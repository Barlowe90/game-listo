package com.gamelisto.biblioteca.application.usecase.addgametolist;

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
public class AddGameToListUseCase implements AddGameToListHandler {

  private final RepositorioLista repositorioLista;
  private final RepositorioGameRef repositorioGameRef;

  public AddGameToListUseCase(
      RepositorioLista repositorioLista, RepositorioGameRef repositorioGameRef) {
    this.repositorioLista = repositorioLista;
    this.repositorioGameRef = repositorioGameRef;
  }

  @Transactional
  public ListaGameResult execute(String listaId, String gameId) {
    EntradaBuscarListaGame result = mapearCommandAEntrada(listaId, gameId);

    ListaGame listaGame = obtenerListaPorIdOrThrow(result);
    GameRef gameRef = obtenerGameRefOrThrow(result);

    listaGame.addGameRef(gameRef);

    ListaGame listaGuardada = repositorioLista.save(listaGame);

    return ListaGameResult.from(listaGuardada);
  }

  private GameRef obtenerGameRefOrThrow(EntradaBuscarListaGame result) {
    return repositorioGameRef
        .findByGameRefId(result.gameRefId())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra el gameRef " + result.gameRefId()));
  }

  private ListaGame obtenerListaPorIdOrThrow(EntradaBuscarListaGame result) {
    return repositorioLista
        .findById(result.listaId())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra la lista " + result.listaId()));
  }

  private static EntradaBuscarListaGame mapearCommandAEntrada(String listaId, String gameId) {
    UUID uuidLista = UUID.fromString(listaId);
    Long gameRefId = Long.parseLong(gameId);
    ListaGameId id = ListaGameId.of(uuidLista);

    return new EntradaBuscarListaGame(id, gameRefId);
  }

  private record EntradaBuscarListaGame(ListaGameId listaId, Long gameRefId) {}
}
