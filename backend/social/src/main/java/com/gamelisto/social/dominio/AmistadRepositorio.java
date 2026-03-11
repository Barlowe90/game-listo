package com.gamelisto.social.dominio;

import java.util.List;
import java.util.UUID;

public interface AmistadRepositorio {

  void addFriendship(UUID userId, UUID friendId);

  void removeFriendship(UUID userId, UUID friendId);

  List<UserRef> getFriends(UUID userId);
}
