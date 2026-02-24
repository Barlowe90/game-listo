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
  public ListaGameResult execute(String idLista) {
    EntradaBuscarListaGame result = mapearCommandAEntrada(idLista);
    ListaGame listaGame = obtenerListaPorIdOrThrow(result);
    return ListaGameResult.from(listaGame);
  }

  private ListaGame obtenerListaPorIdOrThrow(EntradaBuscarListaGame result) {
    return repositorioLista
        .findById(result.idLista())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra la lista " + result.idLista()));
  }

  private static EntradaBuscarListaGame mapearCommandAEntrada(String idLista) {
    UUID uuidLista = UUID.fromString(idLista);
    ListaGameId id = ListaGameId.of(uuidLista);
    return new EntradaBuscarListaGame(id);
  }

  private record EntradaBuscarListaGame(ListaGameId idLista) {}
}
