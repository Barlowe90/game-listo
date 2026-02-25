package com.gamelisto.biblioteca.application.usecase.buscarlistagame;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.application.usecase.ListaGameResult;
import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.ListaGameId;
import com.gamelisto.biblioteca.domain.repositories.RepositorioLista;
import java.util.UUID;

import com.gamelisto.biblioteca.domain.repositories.RepositorioUsuariosRef;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuscarListaGameUseCase implements BuscarListaGameHandler {

  private final RepositorioLista repositorioLista;

  public BuscarListaGameUseCase(RepositorioLista repositorioLista) {
    this.repositorioLista = repositorioLista;
  }

  @Transactional
  public ListaGameResult execute(String userId, String listaId) {
    EntradaBuscarListaGame result = mapearCommandAEntrada(userId, listaId);

    ListaGame listaGame = obtenerListaPorIdOrThrow(result);

    if (!UUID.fromString(userId).equals(listaGame.getUsuarioRefId())) {
      throw new ApplicationException("Usario no propietario de la lista");
    }

    return ListaGameResult.from(listaGame);
  }

  private ListaGame obtenerListaPorIdOrThrow(EntradaBuscarListaGame result) {
    return repositorioLista
        .findById(result.listaId())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra la lista " + result.listaId()));
  }

  private static EntradaBuscarListaGame mapearCommandAEntrada(String userId, String listaId) {
    UUID userUuid = UUID.fromString(userId);
    UUID uuidLista = UUID.fromString(listaId);
    ListaGameId listaGameId = ListaGameId.of(uuidLista);
    return new EntradaBuscarListaGame(userUuid, listaGameId);
  }

  private record EntradaBuscarListaGame(UUID userUuid, ListaGameId listaId) {}
}
