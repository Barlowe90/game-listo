package com.gamelisto.biblioteca.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EditarListaGameUseCaseTest {

  @Mock private ListaGameRepositorio listaRepo;

  @InjectMocks private EditarListaGameUseCase uc;

  @Test
  void should_rename_personalizada_list_when_owner() {
    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    UUID listUuid = UUID.randomUUID();
    ListaGameId listId = ListaGameId.of(listUuid);

    ListaGame lista =
        ListaGame.reconstitute(listId, userId, NombreListaGame.of("Mi lista"), Tipo.PERSONALIZADA);

    when(listaRepo.findById(listId)).thenReturn(Optional.of(lista));
    when(listaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    EditarListaGameCommand cmd =
        new EditarListaGameCommand(userId.value(), listUuid.toString(), "Nuevo nombre");

    ListaGameResult out = uc.execute(cmd);

    assertEquals("Nuevo nombre", out.nombre());
    verify(listaRepo).save(any(ListaGame.class));
  }

  @Test
  void should_throw_when_try_rename_oficial() {
    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    UUID listUuid = UUID.randomUUID();
    ListaGameId listId = ListaGameId.of(listUuid);

    ListaGame lista =
        ListaGame.reconstitute(listId, userId, NombreListaGame.of("Completados"), Tipo.OFICIAL);

    when(listaRepo.findById(listId)).thenReturn(Optional.of(lista));

    EditarListaGameCommand cmd =
        new EditarListaGameCommand(userId.value(), listUuid.toString(), "X");

    assertThrows(ApplicationException.class, () -> uc.execute(cmd));
    verify(listaRepo, never()).save(any());
  }
}
