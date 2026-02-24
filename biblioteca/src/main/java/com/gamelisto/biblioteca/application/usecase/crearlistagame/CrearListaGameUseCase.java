package com.gamelisto.biblioteca.application.usecase.crearlistagame;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.application.usecase.ListaGameResult;
import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.NombreListaGame;
import com.gamelisto.biblioteca.domain.listas.Tipo;
import com.gamelisto.biblioteca.domain.repositories.RepositorioLista;
import com.gamelisto.biblioteca.domain.repositories.RepositorioUsuariosRef;
import com.gamelisto.biblioteca.domain.usuario.UsuarioRef;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CrearListaGameUseCase implements CrearListaGameHandler {

  private final RepositorioLista repositorioLista;
  private final RepositorioUsuariosRef repositorioUsuariosRef;

  public CrearListaGameUseCase(
      RepositorioLista repositorioLista, RepositorioUsuariosRef repositorioUsuariosRef) {
    this.repositorioLista = repositorioLista;
    this.repositorioUsuariosRef = repositorioUsuariosRef;
  }

  @Transactional
  public ListaGameResult execute(CrearListaGameCommand command) {
    MapearCommandToListaGame result = mapperCommandToLista(command);

    ListaGame listaGame =
        ListaGame.create(result.usuarioRefId(), result.nombreListaGame(), result.tipo());
    ListaGame listaGuardada = repositorioLista.save(listaGame);

    UsuarioRef usuarioRef = buscarUsuarioOrThrow(command);
    usuarioRef.addNuevaLista(listaGuardada);

    return ListaGameResult.from(listaGuardada);
  }

  private @NonNull UsuarioRef buscarUsuarioOrThrow(CrearListaGameCommand command) {
    UUID uuidUsuarioRef = UUID.fromString(command.usuarioRefId());
    return repositorioUsuariosRef
        .findById(uuidUsuarioRef)
        .orElseThrow(() -> new ApplicationException("Usuario no encontrado"));
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
