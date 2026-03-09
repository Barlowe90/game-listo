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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrearListaGameUseCaseTest {

  @Mock private ListaGameRepositorio listaRepo;
  @Mock private UsuariosRefRepositorio usuariosRepo;

  @InjectMocks private CrearListaGameUseCase uc;

  @Test
  void should_throw_when_tipo_not_personalizada() {
    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    CrearListaGameCommand cmd =
        new CrearListaGameCommand(userId.value(), "Completados 2026", "OFICIAL");

    assertThrows(ApplicationException.class, () -> uc.execute(cmd));
    verifyNoInteractions(listaRepo);
  }

  @Test
  void should_throw_when_user_not_synced() {
    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    CrearListaGameCommand cmd =
        new CrearListaGameCommand(userId.value(), "Completados 2026", "PERSONALIZADA");

    when(usuariosRepo.findById(userId)).thenReturn(Optional.empty());

    assertThrows(ApplicationException.class, () -> uc.execute(cmd));
    verifyNoInteractions(listaRepo);
  }

  @Test
  void should_create_and_save_personalizada_list() {
    ArgumentCaptor<ListaGame> captor = ArgumentCaptor.forClass(ListaGame.class);
    when(listaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    CrearListaGameCommand cmd =
        new CrearListaGameCommand(userId.value(), "Completados 2026", "PERSONALIZADA");

    // mock usuario presente
    when(usuariosRepo.findById(userId))
        .thenReturn(Optional.of(UsuarioRef.reconstitute(userId, "u", "a")));

    ListaGameResult out = uc.execute(cmd);

    verify(listaRepo).save(captor.capture());
    assertEquals(userId.toString(), out.usuarioRefId());
    assertEquals("Completados 2026", out.nombre());
    assertEquals("PERSONALIZADA", out.tipo());
  }
}
