package com.gamelisto.biblioteca.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.*;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EliminarListaGameUseCaseTest {

  @Test
  void should_delete_personalizada_list_when_owner() {
    ListaGameRepositorio listaRepo = mock(ListaGameRepositorio.class);
    EliminarListaGameUseCase uc = new EliminarListaGameUseCase(listaRepo);

    UUID userId = UUID.randomUUID();
    UUID listUuid = UUID.randomUUID();
    ListaGameId listId = ListaGameId.of(listUuid);

    ListaGame lista =
        ListaGame.reconstitute(listId, userId, NombreListaGame.of("XXX"), Tipo.PERSONALIZADA);

    when(listaRepo.findById(listId)).thenReturn(Optional.of(lista));

    uc.execute(userId.toString(), listUuid.toString());

    verify(listaRepo).deleteById(listId);
  }

  @Test
  void should_throw_when_delete_oficial() {
    ListaGameRepositorio listaRepo = mock(ListaGameRepositorio.class);
    EliminarListaGameUseCase uc = new EliminarListaGameUseCase(listaRepo);

    UUID userId = UUID.randomUUID();
    UUID listUuid = UUID.randomUUID();
    ListaGameId listId = ListaGameId.of(listUuid);

    ListaGame lista =
        ListaGame.reconstitute(listId, userId, NombreListaGame.of("Completados"), Tipo.OFICIAL);

    when(listaRepo.findById(listId)).thenReturn(Optional.of(lista));

    assertThrows(
        ApplicationException.class, () -> uc.execute(userId.toString(), listUuid.toString()));
    verify(listaRepo, never()).deleteById(any());
  }
}
