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
class EliminarGameFromListUseCaseTest {

  @Mock private ListaGameRepositorio listaRepo;
  @Mock private ListaGameItemRepositorio itemRepo;

  @InjectMocks private EliminarGameFromListUseCase uc;

  @Test
  void should_remove_game_from_personalizada_list() {
    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    UUID listUuid = UUID.randomUUID();
    ListaGameId listId = ListaGameId.of(listUuid);

    ListaGame lista =
        ListaGame.reconstitute(listId, userId, NombreListaGame.of("Mi lista"), Tipo.PERSONALIZADA);

    when(listaRepo.findById(listId)).thenReturn(Optional.of(lista));
    // El repositorio de items no necesita stub adicional para este test

    uc.execute(userId.value(), listUuid.toString(), "5");

    verify(itemRepo).remove(listId, GameId.of(5L));
  }

  @Test
  void should_throw_when_try_modify_oficial() {
    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    UUID listUuid = UUID.randomUUID();
    ListaGameId listId = ListaGameId.of(listUuid);

    ListaGame lista =
        ListaGame.reconstitute(listId, userId, NombreListaGame.of("Completados"), Tipo.OFICIAL);

    when(listaRepo.findById(listId)).thenReturn(Optional.of(lista));

    assertThrows(
        ApplicationException.class, () -> uc.execute(userId.value(), listUuid.toString(), "5"));

    verify(itemRepo, never()).remove(any(), any());
  }
}
