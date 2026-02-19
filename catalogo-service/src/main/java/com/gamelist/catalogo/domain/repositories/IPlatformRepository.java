package com.gamelist.catalogo_service.domain.repositories;

import com.gamelist.catalogo_service.domain.catalog.Platform;
import com.gamelist.catalogo_service.domain.catalog.PlatformId;

import java.util.List;
import java.util.Optional;

public interface IPlatformRepository {

  Platform save(Platform platform);

  Optional<Platform> findById(PlatformId id);

  List<Platform> findAll();

  List<Platform> saveAll(List<Platform> platforms);

  void deleteById(PlatformId id);
}
