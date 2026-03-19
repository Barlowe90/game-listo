package com.gamelisto.biblioteca.application.usecase;

import static org.mockito.Mockito.*;

import com.gamelisto.biblioteca.domain.Estado;
import com.gamelisto.biblioteca.domain.GameEstado;
import com.gamelisto.biblioteca.domain.GameEstadoRepositorio;
import com.gamelisto.biblioteca.domain.GameId;
import com.gamelisto.biblioteca.domain.ListaGame;
import com.gamelisto.biblioteca.domain.ListaGameId;
import com.gamelisto.biblioteca.domain.ListaGameItemRepositorio;
import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
import com.gamelisto.biblioteca.domain.NombreListaGame;
import com.gamelisto.biblioteca.domain.Rating;
import com.gamelisto.biblioteca.domain.Tipo;
import com.gamelisto.biblioteca.domain.UsuarioId;
import com.gamelisto.biblioteca.domain.eventos.IBibliotecaPublisher;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EliminarGameEstadoUseCaseTest {

  @Mock private GameEstadoRepositorio gameEstadoRepositorio;
  @Mock private ListaGameRepositorio listaGameRepositorio;
  @Mock private ListaGameItemRepositorio listaGameItemRepositorio;
  @Mock private IBibliotecaPublisher bibliotecaPublisher;

  @InjectMocks private EliminarGameEstadoUseCase useCase;

  @Test
  void should_delete_game_estado_and_remove_from_official_list() {
    UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    GameId gameId = GameId.of(10L);
    GameEstado existing =
        GameEstado.create(userId, gameId, Estado.DESEADO, Rating.of(0.0));
    ListaGame listaOficial =
        ListaGame.reconstitute(
            ListaGameId.generate(), userId, NombreListaGame.of("DESEADO"), Tipo.OFICIAL);

    when(gameEstadoRepositorio.findByUsuarioYGame(userId, gameId)).thenReturn(Optional.of(existing));
    when(listaGameRepositorio.findByUsuarioRefId(userId)).thenReturn(List.of(listaOficial));

    useCase.execute(userUuid, "10");

    verify(listaGameItemRepositorio).remove(listaOficial.getId(), gameId);
    verify(gameEstadoRepositorio).deleteById(existing.getId());
    verify(bibliotecaPublisher).publicarEstadoActualizado(argThat(evento ->
        evento.usuarioId().equals(userUuid)
            && evento.gameId().equals(10L)
            && evento.estado() == null));
  }

  @Test
  void should_ignore_when_game_estado_does_not_exist() {
    UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    GameId gameId = GameId.of(10L);

    when(gameEstadoRepositorio.findByUsuarioYGame(userId, gameId)).thenReturn(Optional.empty());

    useCase.execute(userUuid, "10");

    verify(listaGameRepositorio, never()).findByUsuarioRefId(any());
    verify(listaGameItemRepositorio, never()).remove(any(), any());
    verify(gameEstadoRepositorio, never()).deleteById(any());
    verify(bibliotecaPublisher, never()).publicarEstadoActualizado(any());
  }
}
