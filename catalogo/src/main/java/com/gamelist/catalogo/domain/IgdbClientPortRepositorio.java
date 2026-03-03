package com.gamelist.catalogo.domain;

import com.gamelist.catalogo.application.dto.in.IgdbGameDTO;
import com.gamelist.catalogo.application.dto.in.IgdbPlatformDTO;

import java.util.List;

public interface IgdbClientPortRepositorio {

  /**
   * Obtiene un batch de juegos desde IGDB
   *
   * @param afterId ID del último juego sincronizado (checkpoint)
   * @param limit Máximo de juegos a obtener (máx 500)
   * @return Lista de juegos obtenidos de IGDB
   */
  List<IgdbGameDTO> fetchGamesBatch(Long afterId, int limit);

  List<IgdbPlatformDTO> fetchPlatforms();
}
