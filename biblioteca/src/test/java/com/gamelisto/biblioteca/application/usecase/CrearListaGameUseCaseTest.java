package com.gamelisto.biblioteca.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
class CrearListaGameUseCaseTest {

  @Test
  void should_throw_when_tipo_not_personalizada() {
    ListaGameRepositorio listaRepo = mock(ListaGameRepositorio.class);
    UsuariosRefRepositorio usuariosRepo = mock(UsuariosRefRepositorio.class);
    CrearListaGameUseCase uc = new CrearListaGameUseCase(listaRepo, usuariosRepo);

    UUID userId = UUID.randomUUID();
    CrearListaGameCommand cmd =
        new CrearListaGameCommand(userId.toString(), "Completados 2026", "OFICIAL");

    assertThrows(ApplicationException.class, () -> uc.execute(cmd));
    verifyNoInteractions(listaRepo);
  }

  @Test
  void should_throw_when_user_not_synced() {
    ListaGameRepositorio listaRepo = mock(ListaGameRepositorio.class);
    UsuariosRefRepositorio usuariosRepo = mock(UsuariosRefRepositorio.class);

    CrearListaGameUseCase uc = new CrearListaGameUseCase(listaRepo, usuariosRepo);

    UUID userId = UUID.randomUUID();
    CrearListaGameCommand cmd =
        new CrearListaGameCommand(userId.toString(), "Completados 2026", "PERSONALIZADA");

    when(usuariosRepo.findById(userId)).thenReturn(Optional.empty());

    assertThrows(ApplicationException.class, () -> uc.execute(cmd));
    verifyNoInteractions(listaRepo);
  }

  @Test
  void should_create_and_save_personalizada_list() {
    ListaGameRepositorio listaRepo = mock(ListaGameRepositorio.class);
    UsuariosRefRepositorio usuariosRepo = mock(UsuariosRefRepositorio.class);

    ArgumentCaptor<ListaGame> captor = ArgumentCaptor.forClass(ListaGame.class);
    when(listaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    CrearListaGameUseCase uc = new CrearListaGameUseCase(listaRepo, usuariosRepo);

    UUID userId = UUID.randomUUID();
    CrearListaGameCommand cmd =
        new CrearListaGameCommand(userId.toString(), "Completados 2026", "PERSONALIZADA");

    // mock usuario presente
    when(usuariosRepo.findById(userId))
        .thenReturn(Optional.of(UsuarioRef.reconstitute(userId, "u", "a", "USER")));

    ListaGameResult out = uc.execute(cmd);

    verify(listaRepo).save(captor.capture());
    assertEquals(userId.toString(), out.usuarioRefId());
    assertEquals("Completados 2026", out.nombre());
    assertEquals("PERSONALIZADA", out.tipo());
  }
}
