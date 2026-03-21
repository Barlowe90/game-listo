package com.gamelisto.biblioteca.application.usecase;

import java.util.List;
import java.util.Map;

public interface CatalogoSteamResolver {

  Map<Long, Long> resolveGamesBySteamAppIds(List<Long> steamAppIds);
}
