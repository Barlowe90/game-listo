package com.gamelisto.catalogo.application.usecases;

import com.gamelisto.catalogo.application.dto.in.IgdbPlatformDTO;
import com.gamelisto.catalogo.application.dto.out.SyncResultDTO;
import com.gamelisto.catalogo.domain.IgdbClientPortRepositorio;
import com.gamelisto.catalogo.domain.Platform;
import com.gamelisto.catalogo.domain.PlataformaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Se trae de IGDB la info de plataformas */
@Service
@RequiredArgsConstructor
public class SyncPlatformsFromIGDBUseCase implements SyncPlatformsFromIGDBHandle {

  private static final Logger logger = LoggerFactory.getLogger(SyncPlatformsFromIGDBUseCase.class);

  private final IgdbClientPortRepositorio igdbClient;
  private final PlataformaRepositorio platformRepository;

  @Override
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
