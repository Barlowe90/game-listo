package com.gamelist.catalogo.domain;

import java.util.List;

public interface PlataformaRepositorio {

  Platform save(Platform platform);

  List<Platform> findAll();

  List<Platform> saveAll(List<Platform> platforms);
}
