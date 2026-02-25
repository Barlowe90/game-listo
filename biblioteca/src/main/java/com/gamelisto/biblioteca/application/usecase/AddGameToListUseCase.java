package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddGameToListUseCase implements AddGameToListHandler {

  private final ListaGameRepositorio listaGameRepositorio;
  private final ListaGameItemRepositorio listaGameItemRepositorio;

  public AddGameToListUseCase(
      ListaGameRepositorio listaGameRepositorio,
      ListaGameItemRepositorio listaGameItemRepositorio) {
    this.listaGameRepositorio = listaGameRepositorio;
    this.listaGameItemRepositorio = listaGameItemRepositorio;
  }

  @Transactional
  public ListaGameResult execute(String userId, String listaId, String gameId) {
    UUID userUuid = UUID.fromString(userId);
    ListaGameId listaUuid = ListaGameId.of(UUID.fromString(listaId));
    Long gameRefId = Long.parseLong(gameId);

    ListaGame listaGame =
        listaGameRepositorio
            .findById(listaUuid)
            .orElseThrow(() -> new ApplicationException("No se encuentra la lista " + listaUuid));

    if (!listaGame.getUsuarioRefId().equals(userUuid)) {
      throw new ApplicationException("Usuario no propietario de la lista");
    }

    if (listaGame.getTipo() != Tipo.PERSONALIZADA) {
      throw new ApplicationException("Solo se pueden modificar listas personalizadas");
    }

    listaGameItemRepositorio.add(listaUuid.value(), gameRefId);

    return ListaGameResult.from(listaGame);
  }
}
