package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.GameEstado;
import com.gamelisto.biblioteca.domain.GameEstadoRepositorio;
import com.gamelisto.biblioteca.domain.GameId;
import com.gamelisto.biblioteca.domain.GameRef;
import com.gamelisto.biblioteca.domain.GameRefRepositorio;
import com.gamelisto.biblioteca.domain.ListaGame;
import com.gamelisto.biblioteca.domain.ListaGameId;
import com.gamelisto.biblioteca.domain.ListaGameItemRepositorio;
import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
import com.gamelisto.biblioteca.domain.UsuarioId;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuscarListaGameUseCase implements BuscarListaGameHandler {

  private final ListaGameRepositorio listaGameRepositorio;
  private final ListaGameItemRepositorio listaGameItemRepositorio;
  private final GameRefRepositorio gameRefRepositorio;
  private final GameEstadoRepositorio gameEstadoRepositorio;

  @Transactional
  public ListaGameResult execute(UUID userId, String listaId) {
    EntradaBuscarListaGame result = mapearCommandAEntrada(userId, listaId);

    ListaGame listaGame = obtenerListaPorIdOrThrow(result);

    if (!result.userUuid.equals(listaGame.getUsuarioRefId())) {
      throw new ApplicationException("Usario no propietario de la lista");
    }

    List<GameId> gameIds = listaGameItemRepositorio.findGameIdsByListaId(result.listaId());

    List<ListaGameItemResult> juegos =
        gameIds.stream()
            .map(GameId::value)
            .map(Long::valueOf)
            .map(
                gameIdLong -> {
                  GameRef gameRef = gameRefRepositorio.findById(gameIdLong).orElse(null);
                  GameEstado estado =
                      gameEstadoRepositorio
                          .findByUsuarioYGame(result.userUuid, GameId.of(gameIdLong))
                          .orElse(null);

                  String estadoStr = estado != null ? estado.getEstado().name() : null;

                  String nombre = gameRef != null ? gameRef.getNombre() : null;
                  String cover = gameRef != null ? gameRef.getCover() : null;

                  return ListaGameItemResult.of(gameIdLong, nombre, cover, estadoStr);
                })
            .toList();

    return ListaGameResult.from(listaGame, juegos);
  }

  private ListaGame obtenerListaPorIdOrThrow(EntradaBuscarListaGame result) {
    return listaGameRepositorio
        .findById(result.listaId())
        .orElseThrow(
            () -> new ApplicationException("No se encuentra la lista " + result.listaId()));
  }

  private static EntradaBuscarListaGame mapearCommandAEntrada(UUID userId, String listaId) {
    UsuarioId userUuid = UsuarioId.of(userId);
    ListaGameId listaGameId = ListaGameId.of(UUID.fromString(listaId));
    return new EntradaBuscarListaGame(userUuid, listaGameId);
  }

  private record EntradaBuscarListaGame(UsuarioId userUuid, ListaGameId listaId) {}
}
