package com.gamelisto.biblioteca.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.Estado;
import com.gamelisto.biblioteca.domain.GameEstado;
import com.gamelisto.biblioteca.domain.GameEstadoRepositorio;
import com.gamelisto.biblioteca.domain.GameId;
import com.gamelisto.biblioteca.domain.Rating;
import com.gamelisto.biblioteca.domain.UsuarioId;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RateGameEstadoUseCaseTest {

  @Mock private GameEstadoRepositorio repo;

  @InjectMocks private RateGameEstadoUseCase uc;

  @Test
  void should_rate_existing_game_estado() {
    UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    GameEstado existing = GameEstado.create(userId, GameId.of(7L), Estado.JUGANDO, Rating.of(1.0));

    when(repo.findByUsuarioYGame(userId, GameId.of(7L))).thenReturn(Optional.of(existing));
    when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    uc.execute(new RateGameEstadoCommand(userId.value(), "7", 8.25));

    ArgumentCaptor<GameEstado> captor = ArgumentCaptor.forClass(GameEstado.class);
    verify(repo).save(captor.capture());
    assertEquals(8.25, captor.getValue().getRating().value());
    assertEquals(Estado.JUGANDO, captor.getValue().getEstado());
  }

  @Test
  void should_throw_when_game_estado_missing() {
    UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    when(repo.findByUsuarioYGame(userId, GameId.of(7L))).thenReturn(Optional.empty());

    assertThrows(
        ApplicationException.class,
        () -> uc.execute(new RateGameEstadoCommand(userId.value(), "7", 8.0)));

    verify(repo, never()).save(any());
  }
}
