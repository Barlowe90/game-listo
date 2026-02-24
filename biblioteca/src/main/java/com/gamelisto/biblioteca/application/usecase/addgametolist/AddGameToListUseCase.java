package com.gamelisto.biblioteca.application.usecase.addgametolist;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.application.usecase.ListaGameResult;
import com.gamelisto.biblioteca.domain.gameestado.GameEstado;
import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.ListaGameId;
import com.gamelisto.biblioteca.domain.repositories.RepositorioGameEstado;
import com.gamelisto.biblioteca.domain.repositories.RepositorioLista;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddGameToListUseCase implements AddGameToListHandler {

  private final RepositorioLista repositorioLista;
  private final RepositorioGameEstado repositorioGameEstado;

  public AddGameToListUseCase(
      RepositorioLista repositorioLista, RepositorioGameEstado repositorioGameEstado) {
    this.repositorioLista = repositorioLista;
    this.repositorioGameEstado = repositorioGameEstado;
  }

  @Transactional
  public ListaGameResult execute(String listaId, String gameId) {
    EntradaBuscarListaGame result = mapearCommandAEntrada(listaId, gameId);

    ListaGame listaGame = obtenerListaPorIdOrThrow(result);
    GameEstado gameEstado = obtenerGameEstadoOrThrow(result);

    listaGame.addGameEstado(gameEstado);

    ListaGame listaGuardada = repositorioLista.save(listaGame);

    return ListaGameResult.from(listaGuardada);
  }

  private GameEstado obtenerGameEstadoOrThrow(EntradaBuscarListaGame result) {
    return repositorioGameEstado
        .findById(result.gameEstadoUuid())
        .orElseThrow(
            () ->
                new ApplicationException(
                    "No se encuentra el game estado " + result.gameEstadoUuid()));
  }

  private ListaGame obtenerListaPorIdOrThrow(EntradaBuscarListaGame result) {
    return repositorioLista
        .findById(result.listaId())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra la lista " + result.listaId()));
  }

  private static EntradaBuscarListaGame mapearCommandAEntrada(String listaId, String gameId) {
    UUID uuidLista = UUID.fromString(listaId);
    UUID gameEstadoUuid = UUID.fromString(gameId);
    ListaGameId id = ListaGameId.of(uuidLista);

    return new EntradaBuscarListaGame(id, gameEstadoUuid);
  }

  private record EntradaBuscarListaGame(ListaGameId listaId, UUID gameEstadoUuid) {}
}
