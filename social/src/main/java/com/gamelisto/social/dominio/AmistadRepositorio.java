package com.gamelisto.social.dominio;

import java.util.List;

public interface AmistadRepositorio {

  void addFriendship(String userId, String friendId);

  void removeFriendship(String userId, String friendId);

  List<UserRef> getFriends(String userId);

  List<UserRef> getCommonFriends(String userAId, String userBId);
}
