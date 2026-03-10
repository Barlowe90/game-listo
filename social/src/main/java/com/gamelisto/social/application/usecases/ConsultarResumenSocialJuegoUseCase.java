package com.gamelisto.social.application.usecases;

import com.gamelisto.social.dominio.JuegoSocialRepositorio;
import com.gamelisto.social.dominio.UserRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultarResumenSocialJuegoUseCase implements ConsultarResumenSocialJuegoHandle {

  private final JuegoSocialRepositorio juegoSocialRepositorio;

  @Override
  @Transactional(readOnly = true)
  public ResumenSocialJuegoResult execute(UUID userId, Long gameId) {

    long wishlistedCount = juegoSocialRepositorio.countFriendsWithGameInWishlist(userId, gameId);
    long playingCount = juegoSocialRepositorio.countFriendsPlayingGame(userId, gameId);

    List<UserRef> wishlisted = juegoSocialRepositorio.findFriendsWithGameInWishlist(userId, gameId);
    List<UserRef> playing = juegoSocialRepositorio.findFriendsPlayingGame(userId, gameId);

    // Convertir UserRef (dominio) a UserRefResult (usecase/dto)
    List<UserRefResult> wishlistedPreview =
        wishlisted.stream().map(u -> new UserRefResult(u.id(), u.username(), u.avatar())).toList();
    List<UserRefResult> playingPreview =
        playing.stream().map(u -> new UserRefResult(u.id(), u.username(), u.avatar())).toList();

    return new ResumenSocialJuegoResult(
        wishlistedCount, playingCount, wishlistedPreview, playingPreview);
  }
}
