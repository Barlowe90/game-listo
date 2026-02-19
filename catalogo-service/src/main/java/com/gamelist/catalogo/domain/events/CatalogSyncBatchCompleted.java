package com.gamelist.catalogo.domain.events;

import java.time.Instant;

/**
 * Evento de dominio lanzado cuando se completa un batch de sincronización con IGDB.
 *
 * @param batchSize Cantidad de juegos procesados en el batch
 * @param lastGameId ID del último juego sincronizado (checkpoint)
 * @param occurredAt Timestamp del evento
 */
public record CatalogSyncBatchCompleted(int batchSize, Long lastGameId, Instant occurredAt) {
  public static CatalogSyncBatchCompleted of(int batchSize, Long lastGameId) {
    return new CatalogSyncBatchCompleted(batchSize, lastGameId, Instant.now());
  }
}
