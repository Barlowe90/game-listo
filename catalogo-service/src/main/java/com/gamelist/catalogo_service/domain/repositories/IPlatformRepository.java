package com.gamelist.catalogo_service.domain.repositories;

import com.gamelist.catalogo_service.domain.catalog.Platform;
import com.gamelist.catalogo_service.domain.catalog.PlatformId;

import java.util.List;
import java.util.Optional;

public interface IPlatformRepository {

  Optional<Platform> findById(PlatformId id);

  Platform save(Platform platform);

  List<Platform> findAll();

  void deleteById(PlatformId id);
}
