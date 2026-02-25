package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CrearListaGameUseCase implements CrearListaGameHandler {

  private final ListaGameRepositorio listaGameRepositorio;

  public CrearListaGameUseCase(ListaGameRepositorio listaGameRepositorio) {
    this.listaGameRepositorio = listaGameRepositorio;
  }

  @Override
  @Transactional
  public ListaGameResult execute(CrearListaGameCommand command) {
    MapearCommandToListaGame result = mapperCommandToLista(command);

    comprobarPersonalizadaOrThrow(result);

    ListaGame listaGame =
        ListaGame.create(result.usuarioRefId(), result.nombreListaGame(), result.tipo());

    ListaGame listaGuardada = listaGameRepositorio.save(listaGame);

    return ListaGameResult.from(listaGuardada);
  }

  private static void comprobarPersonalizadaOrThrow(MapearCommandToListaGame result) {
    if (result.tipo != Tipo.PERSONALIZADA) {
      throw new ApplicationException("Solo se puede crear listas personalizadas");
    }
  }

  private static @NonNull MapearCommandToListaGame mapperCommandToLista(
      CrearListaGameCommand command) {
    UUID usuarioRefId = UUID.fromString(command.usuarioRefId());
    NombreListaGame nombreListaGame = NombreListaGame.of(command.nombre());
    Tipo tipo = Tipo.valueOf(command.tipo());
    return new MapearCommandToListaGame(usuarioRefId, nombreListaGame, tipo);
  }

  private record MapearCommandToListaGame(
      UUID usuarioRefId, NombreListaGame nombreListaGame, Tipo tipo) {}
}
