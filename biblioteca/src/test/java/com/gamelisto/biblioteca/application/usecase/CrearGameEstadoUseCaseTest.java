package com.gamelisto.biblioteca.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gamelisto.biblioteca.domain.*;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrearGameEstadoUseCaseTest {

  @Test
  void should_create_new_when_not_exists() {
    GameEstadoRepositorio repo = mock(GameEstadoRepositorio.class);
    CrearGameEstadoUseCase uc = new CrearGameEstadoUseCase(repo);

    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    when(repo.findByUsuarioYGame(userId, GameId.of(10L))).thenReturn(Optional.empty());
    when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    uc.execute(new CrearGameEstadoCommand(userId.toString(), "10", "COMPLETADO"));

    ArgumentCaptor<GameEstado> captor = ArgumentCaptor.forClass(GameEstado.class);
    verify(repo).save(captor.capture());
    assertEquals(userId, captor.getValue().getUsuarioRefId());
    assertEquals(GameId.of(10L), captor.getValue().getGameRefId());
    assertEquals(Estado.COMPLETADO, captor.getValue().getEstado());
  }

  @Test
  void should_update_estado_when_exists() {
    GameEstadoRepositorio repo = mock(GameEstadoRepositorio.class);
    CrearGameEstadoUseCase uc = new CrearGameEstadoUseCase(repo);

    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    GameEstado existing = GameEstado.create(userId, GameId.of(10L), Estado.PENDIENTE, Rating.of(2.0));
    when(repo.findByUsuarioYGame(userId, GameId.of(10L))).thenReturn(Optional.of(existing));
    when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    uc.execute(new CrearGameEstadoCommand(userId.toString(), "10", "COMPLETADO"));

    ArgumentCaptor<GameEstado> captor = ArgumentCaptor.forClass(GameEstado.class);
    verify(repo).save(captor.capture());
    assertEquals(Estado.COMPLETADO, captor.getValue().getEstado());
    assertEquals(2.0, captor.getValue().getRating().value());
  }
}
