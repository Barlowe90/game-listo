package com.gamelisto.social.application.usecases;

import java.util.UUID;
import com.gamelisto.social.dominio.JuegoSocialRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SincronizarGameEstadoSocialUseCase implements SincronizarGameEstadoSocialHandler {

  private final JuegoSocialRepositorio juegoSocialRepositorio;

  @Override
  @Transactional
  public void execute(UUID userId, Long gameId, String estado) {
    juegoSocialRepositorio.syncGameState(userId, gameId, estado);
  }
}
