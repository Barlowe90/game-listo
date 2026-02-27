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

    UUID userId = UUID.randomUUID();
    when(repo.findByUsuarioYGame(userId, 10L)).thenReturn(Optional.empty());
    when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    uc.execute(new CrearGameEstadoCommand(userId.toString(), "10", "COMPLETADO"));

    ArgumentCaptor<GameEstado> captor = ArgumentCaptor.forClass(GameEstado.class);
    verify(repo).save(captor.capture());
    assertEquals(userId, captor.getValue().getUsuarioRefId());
    assertEquals(10L, captor.getValue().getGameRefId());
    assertEquals(Estado.COMPLETADO, captor.getValue().getEstado());
  }

  @Test
  void should_update_estado_when_exists() {
    GameEstadoRepositorio repo = mock(GameEstadoRepositorio.class);
    CrearGameEstadoUseCase uc = new CrearGameEstadoUseCase(repo);

    UUID userId = UUID.randomUUID();
    GameEstado existing = GameEstado.create(userId, 10L, Estado.PENDIENTE, 2.0);
    when(repo.findByUsuarioYGame(userId, 10L)).thenReturn(Optional.of(existing));
    when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    uc.execute(new CrearGameEstadoCommand(userId.toString(), "10", "COMPLETADO"));

    ArgumentCaptor<GameEstado> captor = ArgumentCaptor.forClass(GameEstado.class);
    verify(repo).save(captor.capture());
    assertEquals(Estado.COMPLETADO, captor.getValue().getEstado());
    assertEquals(2.0, captor.getValue().getRating());
  }
}
