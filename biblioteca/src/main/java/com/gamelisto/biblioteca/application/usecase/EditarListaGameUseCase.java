package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.ListaGame;
import com.gamelisto.biblioteca.domain.ListaGameId;
import com.gamelisto.biblioteca.domain.NombreListaGame;
import com.gamelisto.biblioteca.domain.Tipo;
import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EditarListaGameUseCase implements EditarListaGameHandler {

  private final ListaGameRepositorio listaGameRepositorio;

  public EditarListaGameUseCase(ListaGameRepositorio listaGameRepositorio) {
    this.listaGameRepositorio = listaGameRepositorio;
  }

  @Transactional
  public ListaGameResult execute(EditarListaGameCommand command) {
    EntradaEditarListaGame result = mapearCommandAEntrada(command);
    ListaGame listaGame = obtenerListaPorIdOrThrow(result);

    if (!listaGame.getUsuarioRefId().equals(result.usuarioId)) {
      throw new ApplicationException("Usuario no propietario de la lista");
    }

    if (listaGame.getTipo() == Tipo.OFICIAL) {
      throw new ApplicationException("Solo se pueden editar listas personalizadas");
    }

    NombreListaGame nuevoNombre = NombreListaGame.of(result.nuevoNombreRaw);

    ListaGame listaGuardada = actualizarNombreYGuardar(listaGame, nuevoNombre);

    return ListaGameResult.from(listaGuardada);
  }

  private ListaGame actualizarNombreYGuardar(ListaGame listaGame, NombreListaGame nuevoNombre) {
    listaGame.cambiarNombre(nuevoNombre);
    return listaGameRepositorio.save(listaGame);
  }

  private ListaGame obtenerListaPorIdOrThrow(EntradaEditarListaGame result) {
    return listaGameRepositorio
        .findById(result.listaId())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra la lista " + result.listaId()));
  }

  private static EntradaEditarListaGame mapearCommandAEntrada(EditarListaGameCommand command) {
    UUID usuarioId = UUID.fromString(command.userId());
    UUID uuidLista = UUID.fromString(command.listaId());
    ListaGameId listaId = ListaGameId.of(uuidLista);
    return new EntradaEditarListaGame(usuarioId, command.nombre(), listaId);
  }

  private record EntradaEditarListaGame(
      UUID usuarioId, String nuevoNombreRaw, ListaGameId listaId) {}
}
