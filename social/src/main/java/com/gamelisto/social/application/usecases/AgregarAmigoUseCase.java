package com.gamelisto.social.application.usecases;

import com.gamelisto.social.dominio.Amistad;
import com.gamelisto.social.dominio.AmistadRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AgregarAmigoUseCase implements AgregarAmigoHandle {

  private final AmistadRepositorio amistadRepositorio;

  @Transactional
  public void execute(UUID userId, UUID friendId) {
    Amistad.of(userId, friendId);
    amistadRepositorio.addFriendship(userId, friendId);
  }
}
