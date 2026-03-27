package com.gamelisto.biblioteca.application.usecase;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gamelisto.biblioteca.domain.*;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddGameToListUseCaseTest {

  @Mock private ListaGameRepositorio listaRepo;
  @Mock private ListaGameItemRepositorio itemRepo;

  @InjectMocks private AddGameToListUseCase uc;

  @Test
  void should_add_game_to_personalizada_list() {
    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    UUID listUuid = UUID.randomUUID();
    ListaGameId listId = ListaGameId.of(listUuid);

    ListaGame lista =
        ListaGame.reconstitute(listId, userId, NombreListaGame.of("Mi lista"), Tipo.PERSONALIZADA);

    when(listaRepo.findById(listId)).thenReturn(Optional.of(lista));

    uc.execute(userId.value(), listUuid.toString(), "5");

    verify(itemRepo).add(listId, GameId.of(5L));
  }

  @Test
  void should_handle_already_existing_item_gracefully() {
    java.util.UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    UUID listUuid = UUID.randomUUID();
    ListaGameId listId = ListaGameId.of(listUuid);

    ListaGame lista =
        ListaGame.reconstitute(listId, userId, NombreListaGame.of("Mi lista"), Tipo.PERSONALIZADA);

    when(listaRepo.findById(listId)).thenReturn(Optional.of(lista));
    // No esperamos que la capa de uso lance excepción; el repositorio puede ignorar duplicados.

    assertDoesNotThrow(() -> uc.execute(userId.value(), listUuid.toString(), "5"));

    verify(itemRepo).add(listId, GameId.of(5L));
  }
}
