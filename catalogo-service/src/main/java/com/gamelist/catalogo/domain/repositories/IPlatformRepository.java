package com.gamelist.catalogo.domain.repositories;

import com.gamelist.catalogo.domain.catalog.Platform;
import com.gamelist.catalogo.domain.catalog.PlatformId;

import java.util.List;
import java.util.Optional;

public interface IPlatformRepository {

  Platform save(Platform platform);

  Optional<Platform> findById(PlatformId id);

  List<Platform> findAll();

  List<Platform> saveAll(List<Platform> platforms);
}
