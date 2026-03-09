package com.gamelisto.biblioteca.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gamelisto.biblioteca.domain.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrearGameEstadoUseCaseTest {

  @Mock private GameEstadoRepositorio repo;
  @Mock private ListaGameItemRepositorio listaGameItemRepositorio;
  @Mock private ListaGameRepositorio listaGameRepositorio;

  @InjectMocks private CrearGameEstadoUseCase uc;

  @Test
  void should_create_new_when_not_exists() {
    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    when(repo.findByUsuarioYGame(userId, GameId.of(10L))).thenReturn(Optional.empty());
    when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    // mock listas del usuario: incluir la lista oficial con nombre igual al estado "COMPLETADO"
    ListaGame listaOficial =
        ListaGame.reconstitute(
            ListaGameId.generate(), userId, NombreListaGame.of("COMPLETADO"), Tipo.OFICIAL);
    when(listaGameRepositorio.findByUsuarioRefId(userId)).thenReturn(List.of(listaOficial));

    uc.execute(new CrearGameEstadoCommand(userId.value(), "10", "COMPLETADO"));

    ArgumentCaptor<GameEstado> captor = ArgumentCaptor.forClass(GameEstado.class);
    verify(repo).save(captor.capture());
    assertEquals(userId, captor.getValue().getUsuarioRefId());
    assertEquals(GameId.of(10L), captor.getValue().getGameRefId());
    assertEquals(Estado.COMPLETADO, captor.getValue().getEstado());
  }

  @Test
  void should_update_estado_when_exists() {
    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    GameEstado existing =
        GameEstado.create(userId, GameId.of(10L), Estado.PENDIENTE, Rating.of(2.0));
    when(repo.findByUsuarioYGame(userId, GameId.of(10L))).thenReturn(Optional.of(existing));
    when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    // mock listas del usuario: incluir la lista oficial con nombre igual al estado anterior
    // "PENDIENTE"
    ListaGame listaOficialPrev =
        ListaGame.reconstitute(
            ListaGameId.generate(), userId, NombreListaGame.of("PENDIENTE"), Tipo.OFICIAL);
    // también incluir la lista oficial para el nuevo estado "COMPLETADO"
    ListaGame listaOficialNew =
        ListaGame.reconstitute(
            ListaGameId.generate(), userId, NombreListaGame.of("COMPLETADO"), Tipo.OFICIAL);
    when(listaGameRepositorio.findByUsuarioRefId(userId))
        .thenReturn(List.of(listaOficialPrev, listaOficialNew));

    uc.execute(new CrearGameEstadoCommand(userId.value(), "10", "COMPLETADO"));

    ArgumentCaptor<GameEstado> captor = ArgumentCaptor.forClass(GameEstado.class);
    verify(repo).save(captor.capture());
    assertEquals(Estado.COMPLETADO, captor.getValue().getEstado());
    assertEquals(2.0, captor.getValue().getRating().value());
  }
}
