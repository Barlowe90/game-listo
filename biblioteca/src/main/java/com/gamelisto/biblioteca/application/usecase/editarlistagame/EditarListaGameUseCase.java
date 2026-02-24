package com.gamelisto.biblioteca.application.usecase.editarlistagame;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.application.usecase.ListaGameResult;
import com.gamelisto.biblioteca.domain.listas.ListaGame;
import com.gamelisto.biblioteca.domain.listas.ListaGameId;
import com.gamelisto.biblioteca.domain.listas.NombreListaGame;
import com.gamelisto.biblioteca.domain.listas.Tipo;
import com.gamelisto.biblioteca.domain.repositories.RepositorioLista;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditarListaGameUseCase implements EditarListaGameHandler {

  private final RepositorioLista repositorioLista;

  public EditarListaGameUseCase(RepositorioLista repositorioLista) {
    this.repositorioLista = repositorioLista;
  }

  @Transactional
  public ListaGameResult execute(EditarListaGameCommand command) {
    EntradaEditarListaGame result = mapearCommandAEntrada(command);
    ListaGame listaGame = obtenerListaPorIdOrThrow(result);
    comprobarSiEsListaPredefinidaOrThrow(listaGame);
    ListaGame listaGuardada = actualizarNombreYGuardar(listaGame, result);
    return ListaGameResult.from(listaGuardada);
  }

  private static void comprobarSiEsListaPredefinidaOrThrow(ListaGame listaGame) {
    if (listaGame.getTipo().equals(Tipo.OFICIAL)) {
      throw new ApplicationException("No se puede editar el nombre de una lista predeterminada");
    }
  }

  private ListaGame actualizarNombreYGuardar(ListaGame listaGame, EntradaEditarListaGame result) {
    listaGame.cambiarNombre(result.nuevoNombreListaGame());
    return repositorioLista.save(listaGame);
  }

  private ListaGame obtenerListaPorIdOrThrow(EntradaEditarListaGame result) {
    return repositorioLista
        .findById(result.listaId())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra la lista " + result.listaId()));
  }

  private static EntradaEditarListaGame mapearCommandAEntrada(EditarListaGameCommand command) {
    NombreListaGame nuevoNombreListaGame = NombreListaGame.of(command.nombre());
    UUID uuidLista = UUID.fromString(command.listaId());
    ListaGameId listaId = ListaGameId.of(uuidLista);
    return new EntradaEditarListaGame(nuevoNombreListaGame, listaId);
  }

  private record EntradaEditarListaGame(
      NombreListaGame nuevoNombreListaGame, ListaGameId listaId) {}
}
