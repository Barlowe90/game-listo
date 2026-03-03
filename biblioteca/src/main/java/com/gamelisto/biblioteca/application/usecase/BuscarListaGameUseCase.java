package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.ListaGame;
import com.gamelisto.biblioteca.domain.ListaGameId;
import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
import com.gamelisto.biblioteca.domain.UsuarioId;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BuscarListaGameUseCase implements BuscarListaGameHandler {

  private final ListaGameRepositorio listaGameRepositorio;

  public BuscarListaGameUseCase(ListaGameRepositorio listaGameRepositorio) {
    this.listaGameRepositorio = listaGameRepositorio;
  }

  @Transactional
  public ListaGameResult execute(String userId, String listaId) {
    EntradaBuscarListaGame result = mapearCommandAEntrada(userId, listaId);

    ListaGame listaGame = obtenerListaPorIdOrThrow(result);

    if (!result.userUuid.equals(listaGame.getUsuarioRefId())) {
      throw new ApplicationException("Usario no propietario de la lista");
    }

    return ListaGameResult.from(listaGame);
  }

  private ListaGame obtenerListaPorIdOrThrow(EntradaBuscarListaGame result) {
    return listaGameRepositorio
        .findById(result.listaId())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra la lista " + result.listaId()));
  }

  private static EntradaBuscarListaGame mapearCommandAEntrada(String userId, String listaId) {
    UsuarioId userUuid = UsuarioId.fromString(userId);
    ListaGameId listaGameId = ListaGameId.of(java.util.UUID.fromString(listaId));
    return new EntradaBuscarListaGame(userUuid, listaGameId);
  }

  private record EntradaBuscarListaGame(UsuarioId userUuid, ListaGameId listaId) {
  }
}
