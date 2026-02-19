package com.gamelist.catalogo_service.application.usecases;

import com.gamelist.catalogo_service.application.dto.commands.SyncPlatformsCommand;
import com.gamelist.catalogo_service.application.dto.results.IgdbPlatformDTO;
import com.gamelist.catalogo_service.application.dto.results.SyncResultDTO;
import com.gamelist.catalogo_service.application.ports.IEventPublisherPort;
import com.gamelist.catalogo_service.application.ports.IIgdbClientPort;
import com.gamelist.catalogo_service.domain.catalog.Platform;
import com.gamelist.catalogo_service.domain.catalog.PlatformAbbreviation;
import com.gamelist.catalogo_service.domain.catalog.PlatformId;
import com.gamelist.catalogo_service.domain.catalog.PlatformName;
import com.gamelist.catalogo_service.domain.events.PlatformsSyncCompleted;
import com.gamelist.catalogo_service.domain.repositories.IPlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SyncPlatformsFromIgdbUseCase {

  private final IIgdbClientPort igdbClient;
  private final IPlatformRepository platformRepository;
  private final IEventPublisherPort eventPublisher;

  @Transactional
  public SyncResultDTO execute(SyncPlatformsCommand command) {
    log.info("Iniciando sincronización de plataformas desde IGDB");

    // 1. Fetch todas las plataformas de IGDB
    List<IgdbPlatformDTO> igdbPlatforms = igdbClient.fetchPlatforms();
    log.info("Obtenidas {} plataformas desde IGDB", igdbPlatforms.size());

    // 2. Convertir a dominio y hacer upsert
    List<Platform> platforms = igdbPlatforms.stream().map(this::convertToPlatform).toList();

    platformRepository.saveAll(platforms);
    log.info("Guardadas {} plataformas en la base de datos", platforms.size());

    // 3. Publicar evento de sincronización completada
    eventPublisher.publish(PlatformsSyncCompleted.of(platforms.size()));

    return new SyncResultDTO(platforms.size(), null);
  }

  private Platform convertToPlatform(IgdbPlatformDTO dto) {
    PlatformId id = PlatformId.of(dto.id());
    PlatformName name = PlatformName.of(dto.name());
    PlatformAbbreviation abbreviation =
        dto.abbreviation() != null
            ? PlatformAbbreviation.of(dto.abbreviation())
            : PlatformAbbreviation.empty();

    return Platform.create(id, name, abbreviation);
  }
}
