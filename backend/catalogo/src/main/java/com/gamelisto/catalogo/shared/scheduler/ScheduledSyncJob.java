package com.gamelisto.catalogo.shared.scheduler;

import com.gamelisto.catalogo.application.usecases.SyncResultResult;
import com.gamelisto.catalogo.application.usecases.SyncGamesFromIGDBUseCase;
import com.gamelisto.catalogo.application.usecases.SyncPlatformsFromIGDBUseCase;
import com.gamelisto.catalogo.shared.config.IgdbProperties;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@RequiredArgsConstructor
public class ScheduledSyncJob {

  private static final Logger logger = LoggerFactory.getLogger(ScheduledSyncJob.class);

  private final SyncGamesFromIGDBUseCase syncGamesUseCase;
  private final SyncPlatformsFromIGDBUseCase syncPlatformsUseCase;
  private final IgdbProperties igdbProperties;

  @Scheduled(cron = "0 */1 * * * *") 
  public void syncGamesIncremental() {
    logger.info("===== Iniciando sincronización automática de juegos =====");

    try {
      SyncResultResult result = syncGamesUseCase.execute(igdbProperties.getBatchSize());

      logger.info(
          "===== Sincronización automática completada: {} juegos, último ID: {} =====",
          result.totalSynced(),
          result.lastId());

    } catch (Exception e) {
      logger.error("Error en sincronización automática de juegos", e);
      // No lanzar excepción para no interrumpir el scheduler
    }
  }

  @Scheduled(cron = "0 0 0 * * 0") // Los domingos a las 00:00
  public void syncPlatformsDaily() {
    performSyncPlatforms("programado (semanal)");
  }

  @EventListener(ApplicationReadyEvent.class)
  public void syncPlatformsOnStartup() {
    performSyncPlatforms("inicio de aplicación");
  }

  private void performSyncPlatforms(String trigger) {
    logger.info("===== Iniciando sincronización de plataformas: {} =====", trigger);

    try {
      SyncResultResult result = syncPlatformsUseCase.execute();

      logger.info(
          "===== Sincronización de plataformas completada: {} plataformas =====",
          result.totalSynced());

    } catch (Exception e) {
      logger.error("Error en sincronización de plataformas (" + trigger + ")", e);
      // No lanzar excepción para no interrumpir el scheduler
    }
  }
}
