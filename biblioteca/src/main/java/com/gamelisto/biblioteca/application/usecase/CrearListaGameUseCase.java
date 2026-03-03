package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;
import org.jspecify.annotations.NonNull;
import com.gamelisto.biblioteca.domain.UsuarioId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CrearListaGameUseCase implements CrearListaGameHandler {

  private final ListaGameRepositorio listaGameRepositorio;
  private final UsuariosRefRepositorio usuariosRefRepositorio;

  public CrearListaGameUseCase(
      ListaGameRepositorio listaGameRepositorio, UsuariosRefRepositorio usuariosRefRepositorio) {
    this.listaGameRepositorio = listaGameRepositorio;
    this.usuariosRefRepositorio = usuariosRefRepositorio;
  }

  @Override
  @Transactional
  public ListaGameResult execute(CrearListaGameCommand command) {
    MapearCommandToLista result = mapperCommandToLista(command);

    if (usuariosRefRepositorio.findById(result.usuarioRefId()).isEmpty()) {
      throw new ApplicationException("Usuario no sincronizado");
    }

    comprobarPersonalizadaOrThrow(result);

    ListaGame listaGame = ListaGame.create(result.usuarioRefId(), result.nombreListaGame(), result.tipo());

    ListaGame listaGuardada = listaGameRepositorio.save(listaGame);

    if (listaGuardada == null) {
      throw new ApplicationException("No se pudo crear la lista");
    }

    return ListaGameResult.from(listaGuardada);
  }

  private static void comprobarPersonalizadaOrThrow(MapearCommandToLista result) {
    if (result.tipo != Tipo.PERSONALIZADA) {
      throw new ApplicationException("Solo se puede crear listas personalizadas");
    }
  }

  private static @NonNull MapearCommandToLista mapperCommandToLista(
      CrearListaGameCommand command) {
    UsuarioId usuarioRefId = UsuarioId.fromString(command.userId());
    NombreListaGame nombreListaGame = NombreListaGame.of(command.nombre());
    Tipo tipo = Tipo.valueOf(command.tipo());
    return new MapearCommandToLista(usuarioRefId, nombreListaGame, tipo);
  }

  private record MapearCommandToLista(UsuarioId usuarioRefId, NombreListaGame nombreListaGame, Tipo tipo) {
  }
}
