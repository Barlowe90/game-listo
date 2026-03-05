package com.gamelisto.social.application.usecases;

import java.util.List;

public interface ListarAmigosEnComunHandle {
  List<UserRefResult> execute(String userAId, String userBId);
}
