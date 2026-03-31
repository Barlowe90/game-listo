package com.gamelisto.social.application.usecases;

import com.gamelisto.social.dominio.JuegoSocialRepositorio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("SincronizarGameEstadoSocialUseCase")
class SincronizarGameEstadoSocialUseCaseTest {

  @Test
  @DisplayName("debe delegar la sincronizaciÃ³n del estado del juego al repositorio")
  void debeDelegarSincronizacionAlRepositorio() {
    JuegoSocialRepositorio repo = mock(JuegoSocialRepositorio.class);
    SincronizarGameEstadoSocialUseCase useCase = new SincronizarGameEstadoSocialUseCase(repo);

    UUID userId = UUID.randomUUID();
    Long gameId = 123L;
    String estado = "PLAYING";

    useCase.execute(userId, gameId, estado);

    ArgumentCaptor<UUID> userCaptor = ArgumentCaptor.forClass(UUID.class);
    ArgumentCaptor<Long> gameCaptor = ArgumentCaptor.forClass(Long.class);
    ArgumentCaptor<String> estadoCaptor = ArgumentCaptor.forClass(String.class);

    verify(repo).syncGameState(userCaptor.capture(), gameCaptor.capture(), estadoCaptor.capture());

    assertEquals(userId, userCaptor.getValue());
    assertEquals(gameId, gameCaptor.getValue());
    assertEquals(estado, estadoCaptor.getValue());
  }
}



