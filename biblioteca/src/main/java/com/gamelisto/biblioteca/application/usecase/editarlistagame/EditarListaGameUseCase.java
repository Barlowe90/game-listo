package com.gamelisto.biblioteca.application.usecase.editarlistagame;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.ListaGameId;
import com.gamelisto.biblioteca.domain.listas.NombreListaGame;
import com.gamelisto.biblioteca.domain.repositories.RepositorioLista;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditarListaGameUseCase implements EditarListaGameHandler {

  private final RepositorioLista repositorioLista;

  public EditarListaGameUseCase(RepositorioLista repositorioLista) {
    this.repositorioLista = repositorioLista;
  }

  @Transactional
  public EditarListaGameResult execute(EditarListaGameCommand command) {
    EntradaEditarListaGame result = mapearCommandAEntrada(command);
    ListaGame listaGame = obtenerListaPorIdOrThrow(result);
    ListaGame listaGuardada = actualizarNombreYGuardar(listaGame, result);
    return EditarListaGameResult.from(listaGuardada);
  }

  private ListaGame actualizarNombreYGuardar(ListaGame listaGame, EntradaEditarListaGame result) {
    listaGame.cambiarNombre(result.nuevoNombreListaGame());
    return repositorioLista.save(listaGame);
  }

  private ListaGame obtenerListaPorIdOrThrow(EntradaEditarListaGame result) {
    return repositorioLista
        .findById(result.idLista())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra la lista " + result.idLista()));
  }

  private static EntradaEditarListaGame mapearCommandAEntrada(EditarListaGameCommand command) {
    NombreListaGame nuevoNombreListaGame = NombreListaGame.of(command.nombre());
    UUID uuidLista = UUID.fromString(command.idLista());
    ListaGameId idLista = ListaGameId.of(uuidLista);
    return new EntradaEditarListaGame(nuevoNombreListaGame, idLista);
  }

  private record EntradaEditarListaGame(
      NombreListaGame nuevoNombreListaGame, ListaGameId idLista) {}
}
