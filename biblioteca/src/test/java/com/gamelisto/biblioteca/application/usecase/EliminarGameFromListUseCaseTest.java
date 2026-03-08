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
class EliminarGameFromListUseCaseTest {

  @Test
  void should_remove_game_from_personalizada_list() {
    ListaGameRepositorio listaRepo = mock(ListaGameRepositorio.class);
    ListaGameItemRepositorio itemRepo = mock(ListaGameItemRepositorio.class);
    EliminarGameFromListUseCase uc = new EliminarGameFromListUseCase(listaRepo, itemRepo);

    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    UUID listUuid = UUID.randomUUID();
    ListaGameId listId = ListaGameId.of(listUuid);

    ListaGame lista =
        ListaGame.reconstitute(listId, userId, NombreListaGame.of("Mi lista"), Tipo.PERSONALIZADA);

    when(listaRepo.findById(listId)).thenReturn(Optional.of(lista));

    ListaGameResult out = uc.execute(userId.value(), listUuid.toString(), "999");

    verify(itemRepo).remove(listId, GameId.of(999L));
    assertEquals(listUuid.toString(), out.id());
  }

  @Test
  void should_throw_when_try_modify_oficial() {
    ListaGameRepositorio listaRepo = mock(ListaGameRepositorio.class);
    ListaGameItemRepositorio itemRepo = mock(ListaGameItemRepositorio.class);
    EliminarGameFromListUseCase uc = new EliminarGameFromListUseCase(listaRepo, itemRepo);

    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    UUID listUuid = UUID.randomUUID();
    ListaGameId listId = ListaGameId.of(listUuid);

    ListaGame lista =
        ListaGame.reconstitute(listId, userId, NombreListaGame.of("Completados"), Tipo.OFICIAL);

    when(listaRepo.findById(listId)).thenReturn(Optional.of(lista));

    assertThrows(
        ApplicationException.class, () -> uc.execute(userId.value(), listUuid.toString(), "1"));
    verifyNoInteractions(itemRepo);
  }
}
