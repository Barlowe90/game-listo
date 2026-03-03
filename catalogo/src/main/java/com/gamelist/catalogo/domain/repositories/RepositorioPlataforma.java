package com.gamelist.catalogo.domain.repositories;

import com.gamelist.catalogo.domain.platform.Platform;

import java.util.List;

public interface RepositorioPlataforma {

  Platform save(Platform platform);

  List<Platform> findAll();

  List<Platform> saveAll(List<Platform> platforms);
}
