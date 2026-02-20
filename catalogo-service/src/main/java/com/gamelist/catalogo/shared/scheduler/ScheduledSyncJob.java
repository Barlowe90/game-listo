package com.gamelist.catalogo.shared.scheduler;

import com.gamelist.catalogo.application.dto.commands.SyncPlatformsCommand;
import com.gamelist.catalogo.application.dto.results.SyncResultDTO;
import com.gamelist.catalogo.application.usecases.SyncIgdbGamesUseCase;
import com.gamelist.catalogo.application.usecases.SyncPlatformsFromIgdbUseCase;
import com.gamelist.catalogo.shared.config.IgdbProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledSyncJob {

  private final SyncIgdbGamesUseCase syncGamesUseCase;
  private final SyncPlatformsFromIgdbUseCase syncPlatformsUseCase;
  private final IgdbProperties igdbProperties;

  @Scheduled(cron = "0 0 */12 * * *") // Cada 12 horas (a las 00:00, 12:00, 00:00)
  public void syncGamesIncremental() {
    log.info("===== Iniciando sincronización automática de juegos =====");

    try {
      SyncResultDTO result = syncGamesUseCase.execute(igdbProperties.getBatchSize());

      log.info(
          "===== Sincronización automática completada: {} juegos, último ID: {} =====",
          result.totalSynced(),
          result.lastId());

    } catch (Exception e) {
      log.error("Error en sincronización automática de juegos", e);
      // No lanzar excepción para no interrumpir el scheduler
    }
  }

  @Scheduled(cron = "0 0 0 * * 0") // Los domingos a las 00:00
  public void syncPlatformsDaily() {
    log.info("===== Iniciando sincronización diaria de plataformas =====");

    try {
      SyncPlatformsCommand command = new SyncPlatformsCommand();
      SyncResultDTO result = syncPlatformsUseCase.execute(command);

      log.info(
          "===== Sincronización de plataformas completada: {} plataformas =====",
          result.totalSynced());

    } catch (Exception e) {
      log.error("Error en sincronización de plataformas", e);
      // No lanzar excepción para no interrumpir el scheduler
    }
  }
}
