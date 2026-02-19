package com.gamelist.catalogo_service.domain.events;

import java.time.Instant;

/**
 * Evento de dominio lanzado cuando se completa una sincronización completa con IGDB.
 *
 * @param totalGames Total de juegos sincronizados
 * @param occurredAt Timestamp del evento
 */
public record CatalogSyncCompleted(int totalGames, Instant occurredAt) {
  public static CatalogSyncCompleted of(int totalGames) {
    return new CatalogSyncCompleted(totalGames, Instant.now());
  }
}
