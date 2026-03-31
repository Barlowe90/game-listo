package com.gamelisto.social.application.usecases;

import com.gamelisto.social.dominio.JuegoSocialRepositorio;
import com.gamelisto.social.dominio.UserRef;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("ConsultarResumenSocialJuegoUseCase")
class ConsultarResumenSocialJuegoUseCaseTest {

  @Test
  @DisplayName("debe devolver resumen con counts y previews convertidos a UserRefResult")
  void debeDevolverResumenConCountsYPreviews() {
    JuegoSocialRepositorio repo = mock(JuegoSocialRepositorio.class);
    ConsultarResumenSocialJuegoUseCase useCase = new ConsultarResumenSocialJuegoUseCase(repo);

    UUID userId = UUID.randomUUID();
    Long gameId = 42L;

    when(repo.countFriendsWithGameInWishlist(userId, gameId)).thenReturn(2L);
    when(repo.countFriendsPlayingGame(userId, gameId)).thenReturn(1L);

    UserRef u1 = UserRef.of(UUID.randomUUID(), "alice", "avatar1");
    UserRef u2 = UserRef.of(UUID.randomUUID(), "bob", null);
    when(repo.findFriendsWithGameInWishlist(userId, gameId)).thenReturn(List.of(u1, u2));
    when(repo.findFriendsPlayingGame(userId, gameId)).thenReturn(List.of(u2));

    ResumenSocialJuegoResult result = useCase.execute(userId, gameId);

    assertEquals(2L, result.amigosDeseadoCount());
    assertEquals(1L, result.amigosJugandoCount());

    assertEquals(2, result.amigosDeseadoPreview().size());
    assertEquals(1, result.amigosJugandoPreview().size());

    // Check conversion correctness
    assertEquals(u1.id(), result.amigosDeseadoPreview().get(0).id());
    assertEquals(u1.username(), result.amigosDeseadoPreview().get(0).username());
    assertEquals(u1.avatar(), result.amigosDeseadoPreview().get(0).avatar());
  }
}



