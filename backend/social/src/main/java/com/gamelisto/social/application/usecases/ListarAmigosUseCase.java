package com.gamelisto.social.application.usecases;

import com.gamelisto.social.dominio.AmistadRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListarAmigosUseCase implements ListarAmigosHandle {

  private final AmistadRepositorio amistadRepositorio;

  @Override
  public List<UserRefResult> execute(UUID userId) {
    return amistadRepositorio.getFriends(userId).stream()
        .map(f -> new UserRefResult(f.id(), f.username(), f.avatar()))
        .toList();
  }
}
