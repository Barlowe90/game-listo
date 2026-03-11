package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;
import com.gamelisto.biblioteca.domain.eventos.EstadoActualizado;
import com.gamelisto.biblioteca.domain.eventos.IBibliotecaPublisher;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrearGameEstadoUseCase implements CrearGameEstadoHandler {

  private final GameEstadoRepositorio gameEstadoRepositorio;
  private final ListaGameItemRepositorio listaGameItemRepositorio;
  private final ListaGameRepositorio listaGameRepositorio;
  private final GameRefRepositorio gameRefRepositorio;
  private final IBibliotecaPublisher bibliotecaPublisher;

  @Transactional
  public void execute(CrearGameEstadoCommand command) {
    UsuarioId userId = UsuarioId.of(command.userId());
    GameId gameId = GameId.of(Long.parseLong(command.gameId()));
    Estado estadoNuevo = Estado.valueOf(command.estado());

    comprobarSiExisteElGameRef(gameId);

    Optional<GameEstado> juegoActual = gameEstadoRepositorio.findByUsuarioYGame(userId, gameId);
    Estado estadoAnterior = juegoActual.map(GameEstado::getEstado).orElse(null);

    if (estadoAnterior == estadoNuevo) {
      return;
    }

    List<ListaGame> listasUsuario = listaGameRepositorio.findByUsuarioRefId(userId);

    tieneEstadoAnteriorAndEliminarListaAnterior(estadoAnterior, listasUsuario, gameId);

    GameEstado actualizado =
        actualizarJuegoConNuevoEstado(juegoActual, userId, gameId, estadoNuevo);

    ListaGame listaJuegoNuevo = buscarListaOficialPorEstado(listasUsuario, estadoNuevo);

    listaGameItemRepositorio.add(listaJuegoNuevo.getId(), actualizado.getGameRefId());

    publicarEventoEstadoActualizado(actualizado);
  }

  private void comprobarSiExisteElGameRef(GameId gameId) {
    gameRefRepositorio
        .findById(gameId.value())
        .orElseThrow(() -> new ApplicationException("No existe el game"));
  }

  private static @NonNull ListaGame buscarListaOficialPorEstado(
      List<ListaGame> listasUsuario, Estado estadoNuevo) {
    return listasUsuario.stream()
        .filter(lista -> lista.getTipo() == Tipo.OFICIAL)
        .filter(lista -> lista.getNombreLista().value().equals(estadoNuevo.name()))
        .findFirst()
        .orElseThrow(() -> new ApplicationException("No hay listas con el nombre " + estadoNuevo));
  }

  private @NonNull GameEstado actualizarJuegoConNuevoEstado(
      Optional<GameEstado> juegoActual, UsuarioId userId, GameId gameId, Estado estadoNuevo) {
    GameEstado actualizado =
        juegoActual
            .map(
                existente ->
                    GameEstado.reconstitute(
                        existente.getId(), userId, gameId, estadoNuevo, existente.getRating()))
            .orElse(GameEstado.create(userId, gameId, estadoNuevo, Rating.of(0.0)));
    gameEstadoRepositorio.save(actualizado);
    return actualizado;
  }

  private void tieneEstadoAnteriorAndEliminarListaAnterior(
      Estado estadoAnterior, List<ListaGame> listasUsuario, GameId gameId) {
    if (estadoAnterior != null) {
      ListaGame listaDelJuegoAnterior = buscarListaOficialPorEstado(listasUsuario, estadoAnterior);
      listaGameItemRepositorio.remove(listaDelJuegoAnterior.getId(), gameId);
    }
  }

  private void publicarEventoEstadoActualizado(GameEstado actualizado) {
    EstadoActualizado evento =
        EstadoActualizado.of(
            actualizado.getUsuarioRefId().value(),
            actualizado.getGameRefId().value(),
            actualizado.getEstado().toString());
    bibliotecaPublisher.publicarEstadoActualizado(evento);
  }
}
