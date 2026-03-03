package com.gamelisto.biblioteca.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RateGameEstadoUseCaseTest {

  @Test
  void should_rate_existing_game_estado() {
    GameEstadoRepositorio repo = mock(GameEstadoRepositorio.class);
    RateGameEstadoUseCase uc = new RateGameEstadoUseCase(repo);

    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    GameEstado existing = GameEstado.create(userId, GameId.of(7L), Estado.JUGANDO, Rating.of(1.0));

    when(repo.findByUsuarioYGame(userId, GameId.of(7L))).thenReturn(Optional.of(existing));
    when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    uc.execute(new RateGameEstadoCommand(userId.toString(), "7", 4.25));

    ArgumentCaptor<GameEstado> captor = ArgumentCaptor.forClass(GameEstado.class);
    verify(repo).save(captor.capture());
    assertEquals(4.25, captor.getValue().getRating().value());
    assertEquals(Estado.JUGANDO, captor.getValue().getEstado());
  }

  @Test
  void should_throw_when_game_estado_missing() {
    GameEstadoRepositorio repo = mock(GameEstadoRepositorio.class);
    RateGameEstadoUseCase uc = new RateGameEstadoUseCase(repo);

    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    when(repo.findByUsuarioYGame(userId, GameId.of(7L))).thenReturn(Optional.empty());

    assertThrows(
        ApplicationException.class,
        () -> uc.execute(new RateGameEstadoCommand(userId.toString(), "7", 4.0)));

    verify(repo, never()).save(any());
  }
}
