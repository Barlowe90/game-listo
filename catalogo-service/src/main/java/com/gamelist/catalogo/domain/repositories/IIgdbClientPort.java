package com.gamelist.catalogo.application.ports;

import com.gamelist.catalogo.application.dto.results.IgdbGameDTO;
import com.gamelist.catalogo.application.dto.results.IgdbPlatformDTO;

import java.util.List;

public interface IIgdbClientPort {

  /**
   * Obtiene un batch de juegos desde IGDB
   *
   * @param afterId ID del último juego sincronizado (checkpoint)
   * @param limit Máximo de juegos a obtener (máx 500)
   * @return Lista de juegos obtenidos de IGDB
   */
  List<IgdbGameDTO> fetchGamesBatch(Long afterId, int limit);

  /**
   * Obtiene todas las plataformas desde IGDB
   *
   * @return Lista de plataformas disponibles en IGDB
   */
  List<IgdbPlatformDTO> fetchPlatforms();
}
