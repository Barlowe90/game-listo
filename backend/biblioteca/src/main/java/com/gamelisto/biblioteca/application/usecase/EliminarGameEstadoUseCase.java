package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.domain.Estado;
import com.gamelisto.biblioteca.domain.GameEstado;
import com.gamelisto.biblioteca.domain.GameEstadoRepositorio;
import com.gamelisto.biblioteca.domain.GameId;
import com.gamelisto.biblioteca.domain.ListaGame;
import com.gamelisto.biblioteca.domain.ListaGameItemRepositorio;
import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
import com.gamelisto.biblioteca.domain.Tipo;
import com.gamelisto.biblioteca.domain.UsuarioId;
import com.gamelisto.biblioteca.domain.eventos.EstadoActualizado;
import com.gamelisto.biblioteca.domain.eventos.IBibliotecaPublisher;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarGameEstadoUseCase implements EliminarGameEstadoHandler {

  private final GameEstadoRepositorio gameEstadoRepositorio;
  private final ListaGameRepositorio listaGameRepositorio;
  private final ListaGameItemRepositorio listaGameItemRepositorio;
  private final IBibliotecaPublisher bibliotecaPublisher;

  @Override
  @Transactional
  public void execute(UUID userId, String gameId) {
    UsuarioId usuarioId = UsuarioId.of(userId);
    GameId gameRefId = GameId.of(Long.parseLong(gameId));

    Optional<GameEstado> juegoActual = gameEstadoRepositorio.findByUsuarioYGame(usuarioId, gameRefId);

    if (juegoActual.isEmpty()) {
      return;
    }

    GameEstado gameEstado = juegoActual.get();
    List<ListaGame> listasUsuario = listaGameRepositorio.findByUsuarioRefId(usuarioId);

    buscarListaOficialPorEstado(listasUsuario, gameEstado.getEstado())
        .ifPresent(lista -> listaGameItemRepositorio.remove(lista.getId(), gameRefId));

    gameEstadoRepositorio.deleteById(gameEstado.getId());
    bibliotecaPublisher.publicarEstadoActualizado(
        EstadoActualizado.of(usuarioId.value(), gameRefId.value(), null));
  }

  private static Optional<ListaGame> buscarListaOficialPorEstado(
      List<ListaGame> listasUsuario, Estado estado) {
    return listasUsuario.stream()
        .filter(lista -> lista.getTipo() == Tipo.OFICIAL)
        .filter(lista -> lista.getNombreLista().value().equals(estado.name()))
        .findFirst();
  }
}
