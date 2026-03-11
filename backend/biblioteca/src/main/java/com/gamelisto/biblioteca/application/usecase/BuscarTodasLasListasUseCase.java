package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.domain.GameEstadoRepositorio;
import com.gamelisto.biblioteca.domain.GameId;
import com.gamelisto.biblioteca.domain.GameRefRepositorio;
import com.gamelisto.biblioteca.domain.ListaGameItemRepositorio;
import com.gamelisto.biblioteca.domain.ListaGameRepositorio;

import java.util.List;
import java.util.UUID;

import com.gamelisto.biblioteca.domain.UsuarioId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuscarTodasLasListasUseCase implements BuscarTodasLasListasGameHandler {

  private final ListaGameRepositorio listaGameRepositorio;
  private final ListaGameItemRepositorio listaGameItemRepositorio;
  private final GameRefRepositorio gameRefRepositorio;
  private final GameEstadoRepositorio gameEstadoRepositorio;

  @Transactional
  public List<ListaGameResult> execute(UUID userId) {
    EntradaBuscarListaGame result = mapearCommandAEntrada(userId);

    return listaGameRepositorio.findByUsuarioRefId(result.userUuid).stream()
        .map(
            lista -> {
              List<GameId> gameIds = listaGameItemRepositorio.findGameIdsByListaId(lista.getId());

              List<ListaGameItemResult> juegos =
                  gameIds.stream()
                      .map(GameId::value)
                      .map(Long::valueOf)
                      .map(
                          gameIdLong -> {
                            var gameRef = gameRefRepositorio.findById(gameIdLong).orElse(null);
                            var estado =
                                gameEstadoRepositorio
                                    .findByUsuarioYGame(result.userUuid, GameId.of(gameIdLong))
                                    .orElse(null);

                            String estadoStr = estado != null ? estado.getEstado().name() : null;
                            String nombre = gameRef != null ? gameRef.getNombre() : null;
                            String cover = gameRef != null ? gameRef.getCover() : null;

                            return ListaGameItemResult.of(gameIdLong, nombre, cover, estadoStr);
                          })
                      .toList();

              return ListaGameResult.from(lista, juegos);
            })
        .toList();
  }

  private static EntradaBuscarListaGame mapearCommandAEntrada(UUID userId) {
    UsuarioId userUuid = UsuarioId.of(userId);
    return new EntradaBuscarListaGame(userUuid);
  }

  private record EntradaBuscarListaGame(UsuarioId userUuid) {}
}
