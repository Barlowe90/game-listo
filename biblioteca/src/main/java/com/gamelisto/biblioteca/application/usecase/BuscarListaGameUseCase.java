package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.ListaGame;
import com.gamelisto.biblioteca.domain.ListaGameId;
import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
import com.gamelisto.biblioteca.domain.UsuarioId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarListaGameUseCase implements BuscarListaGameHandler {

  private final ListaGameRepositorio listaGameRepositorio;

  @Transactional
  public ListaGameResult execute(UUID userId, String listaId) {
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

  private static EntradaBuscarListaGame mapearCommandAEntrada(UUID userId, String listaId) {
    UsuarioId userUuid = UsuarioId.of(userId);
    ListaGameId listaGameId = ListaGameId.of(UUID.fromString(listaId));
    return new EntradaBuscarListaGame(userUuid, listaGameId);
  }

  private record EntradaBuscarListaGame(UsuarioId userUuid, ListaGameId listaId) {}
}
