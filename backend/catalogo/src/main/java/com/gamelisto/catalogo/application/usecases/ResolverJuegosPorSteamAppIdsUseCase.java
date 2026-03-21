package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.domain.GameRepositorio;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResolverJuegosPorSteamAppIdsUseCase implements ResolverJuegosPorSteamAppIdsHandle {

  private final GameRepositorio gameRepositorio;

  @Override
  public Map<Long, Long> execute(List<Long> steamAppIds) {
    return gameRepositorio.findIdsBySteamAppIds(steamAppIds);
  }
}
