package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AddGameToListUseCase implements AddGameToListHandler {

  private final ListaGameRepositorio listaGameRepositorio;
  private final ListaGameItemRepositorio listaGameItemRepositorio;

  @Transactional
  public ListaGameResult execute(UUID userId, String listaId, String gameId) {
    UsuarioId userUuid = UsuarioId.of(userId);
    ListaGameId listaUuid = ListaGameId.of(UUID.fromString(listaId));
    GameId gameRefId = GameId.of(Long.parseLong(gameId));

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

    listaGameItemRepositorio.add(listaUuid, gameRefId);

    return ListaGameResult.from(listaGame);
  }
}
