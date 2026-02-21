package com.gamelist.catalogo.application.usecases;

import com.gamelist.catalogo.application.dto.in.IgdbPlatformDTO;
import com.gamelist.catalogo.application.dto.out.SyncResultDTO;
import com.gamelist.catalogo.domain.repositories.IIgdbClientPort;
import com.gamelist.catalogo.domain.platform.Platform;
import com.gamelist.catalogo.domain.repositories.RepositorioPlataforma;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Se trae de IGDB la info de plataformas */
@Service
@RequiredArgsConstructor
public class SyncPlatformsFromIGDBUseCase {

  private static final Logger logger = LoggerFactory.getLogger(SyncPlatformsFromIGDBUseCase.class);

  private final IIgdbClientPort igdbClient;
  private final RepositorioPlataforma platformRepository;

  @Transactional
  public SyncResultDTO execute() {
    logger.info("Iniciando sincronización de plataformas desde IGDB");

    List<IgdbPlatformDTO> igdbPlatforms = igdbClient.fetchPlatforms();
    if (igdbPlatforms == null || igdbPlatforms.isEmpty()) {
      logger.info("No se recibieron plataformas de IGDB");
      return new SyncResultDTO(0, null);
    }

    List<Platform> platforms = igdbPlatforms.stream().map(IgdbPlatformDTO::toDomain).toList();
    platformRepository.saveAll(platforms);

    logger.info("Sincronización completada: {} plataformas guardadas", platforms.size());
    return new SyncResultDTO(platforms.size(), null);
  }
}
