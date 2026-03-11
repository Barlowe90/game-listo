package com.gamelisto.social.dominio;

import java.util.List;
import java.util.UUID;

public interface JuegoSocialRepositorio {

  void syncGameState(UUID userId, Long gameId, String estado);

  long countFriendsWithGameInWishlist(UUID userId, Long gameId);

  long countFriendsPlayingGame(UUID userId, Long gameId);

  List<UserRef> findFriendsWithGameInWishlist(UUID userId, Long gameId);

  List<UserRef> findFriendsPlayingGame(UUID userId, Long gameId);
}
