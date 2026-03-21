package com.gamelisto.catalogo.application.usecases;

import java.util.List;
import java.util.Map;

public interface ResolverJuegosPorSteamAppIdsHandle {

  Map<Long, Long> execute(List<Long> steamAppIds);
}
