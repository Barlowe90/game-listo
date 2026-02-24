package com.gamelisto.biblioteca.application.usecase.buscarlistagame;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.application.usecase.ListaGameResult;
import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.ListaGameId;
import com.gamelisto.biblioteca.domain.repositories.RepositorioLista;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuscarListaGameUseCase implements BuscarListaGameHandler {

  private final RepositorioLista repositorioLista;

  public BuscarListaGameUseCase(RepositorioLista repositorioLista) {
    this.repositorioLista = repositorioLista;
  }

  @Transactional
  public ListaGameResult execute(String listaId) {
    EntradaBuscarListaGame result = mapearCommandAEntrada(listaId);
    ListaGame listaGame = obtenerListaPorIdOrThrow(result);
    return ListaGameResult.from(listaGame);
  }

  private ListaGame obtenerListaPorIdOrThrow(EntradaBuscarListaGame result) {
    return repositorioLista
        .findById(result.listaId())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra la lista " + result.listaId()));
  }

  private static EntradaBuscarListaGame mapearCommandAEntrada(String listaId) {
    UUID uuidLista = UUID.fromString(listaId);
    ListaGameId id = ListaGameId.of(uuidLista);
    return new EntradaBuscarListaGame(id);
  }

  private record EntradaBuscarListaGame(ListaGameId listaId) {}
}
