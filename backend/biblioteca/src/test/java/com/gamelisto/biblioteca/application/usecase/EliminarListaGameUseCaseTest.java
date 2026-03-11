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
class EliminarListaGameUseCaseTest {

  @Mock private ListaGameRepositorio listaRepo;

  @InjectMocks private EliminarListaGameUseCase uc;

  @Test
  void should_delete_personalizada_list_when_owner() {
    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    UUID listUuid = UUID.randomUUID();
    ListaGameId listId = ListaGameId.of(listUuid);

    ListaGame lista =
        ListaGame.reconstitute(listId, userId, NombreListaGame.of("XXX"), Tipo.PERSONALIZADA);

    when(listaRepo.findById(listId)).thenReturn(Optional.of(lista));

    uc.execute(userId.value(), listUuid.toString());

    verify(listaRepo).deleteById(listId);
  }

  @Test
  void should_throw_when_delete_oficial() {
    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    UUID listUuid = UUID.randomUUID();
    ListaGameId listId = ListaGameId.of(listUuid);

    ListaGame lista =
        ListaGame.reconstitute(listId, userId, NombreListaGame.of("Completados"), Tipo.OFICIAL);

    when(listaRepo.findById(listId)).thenReturn(Optional.of(lista));

    assertThrows(ApplicationException.class, () -> uc.execute(userId.value(), listUuid.toString()));
    verify(listaRepo, never()).deleteById(any());
  }
}
