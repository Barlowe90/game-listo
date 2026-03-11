package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EditarListaGameUseCase implements EditarListaGameHandler {

  private final ListaGameRepositorio listaGameRepositorio;

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
    UsuarioId usuarioId = UsuarioId.of(command.userId());
    ListaGameId listaId = ListaGameId.of(UUID.fromString(command.listaId()));
    return new EntradaEditarListaGame(usuarioId, command.nombre(), listaId);
  }

  private record EntradaEditarListaGame(
      UsuarioId usuarioId, String nuevoNombreRaw, ListaGameId listaId) {}
}
