package com.gamelisto.biblioteca.application.usecase.crearlistagame;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.application.usecase.ListaGameResult;
import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.NombreListaGame;
import com.gamelisto.biblioteca.domain.listas.Tipo;
import com.gamelisto.biblioteca.domain.repositories.RepositorioLista;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CrearListaGameUseCase implements CrearListaGameHandler {

  private final RepositorioLista repositorioLista;

  public CrearListaGameUseCase(RepositorioLista repositorioLista) {
    this.repositorioLista = repositorioLista;
  }

  @Transactional
  public ListaGameResult execute(CrearListaGameCommand command) {
    MapearCommandToListaGame result = mapperCommandToLista(command);

    comprobarPersonalizadaOrThrow(result);

    ListaGame listaGame =
        ListaGame.create(result.usuarioRefId(), result.nombreListaGame(), result.tipo());

    ListaGame listaGuardada = repositorioLista.save(listaGame);

    return ListaGameResult.from(listaGuardada);
  }

  private static void comprobarPersonalizadaOrThrow(MapearCommandToListaGame result) {
    if (!result.tipo.equals(Tipo.PERSONALIZADA)) {
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
