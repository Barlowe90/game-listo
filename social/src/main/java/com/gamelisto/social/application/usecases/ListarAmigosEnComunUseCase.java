package com.gamelisto.social.application.usecases;

import com.gamelisto.social.dominio.AmistadRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarAmigosEnComunUseCase implements ListarAmigosEnComunHandle {

  private final AmistadRepositorio amistadRepositorio;

  @Override
  public List<UserRefResult> execute(String userAId, String userBId) {
    return amistadRepositorio.getCommonFriends(userAId, userBId).stream()
        .map(f -> new UserRefResult(f.id(), f.username(), f.avatar()))
        .toList();
  }
}
