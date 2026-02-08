package com.gamelist.catalogo_service.infrastructure.scheduler;

import com.gamelist.catalogo_service.application.dto.commands.SyncIgdbGamesCommand;
import com.gamelist.catalogo_service.application.dto.commands.SyncPlatformsCommand;
import com.gamelist.catalogo_service.application.dto.results.SyncResultDTO;
import com.gamelist.catalogo_service.application.usecases.SyncIgdbGamesUseCase;
import com.gamelist.catalogo_service.application.usecases.SyncPlatformsFromIgdbUseCase;
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

  @Scheduled(cron = "0 0 */6 * * *") // Cada 6 horas (a las 00:00, 06:00, 12:00, 18:00)
  public void syncGamesIncremental() {
    log.info("===== Iniciando sincronización automática de juegos =====");

    try {
      // fromId = null para que use el checkpoint guardado
      SyncIgdbGamesCommand command = new SyncIgdbGamesCommand(null, 500);
      SyncResultDTO result = syncGamesUseCase.execute(command);

      log.info(
          "===== Sincronización automática completada: {} juegos, último ID: {} =====",
          result.totalSynced(),
          result.lastId());

    } catch (Exception e) {
      log.error("Error en sincronización automática de juegos", e);
      // No lanzar excepción para no interrumpir el scheduler
    }
  }

  @Scheduled(cron = "0 0 3 * * *") // Diariamente a las 03:00 de la madrugada
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
