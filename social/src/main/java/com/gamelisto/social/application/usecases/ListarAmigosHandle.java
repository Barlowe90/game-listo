package com.gamelisto.social.application.usecases;

import java.util.List;

public interface ListarAmigosHandle {
  List<UserRefResult> execute(String userId);
}
