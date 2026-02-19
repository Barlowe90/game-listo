package com.gamelist.catalogo.domain.events;

import java.time.Instant;

/**
 * Evento de dominio lanzado cuando un juego es insertado o actualizado en el catálogo.
 *
 * @param gameId ID del juego afectado
 * @param occurredAt Timestamp del evento
 * @param source Origen de los datos (IGDB)
 */
public record CatalogGameUpserted(Long gameId, Instant occurredAt, String source) {
  public static CatalogGameUpserted of(Long gameId) {
    return new CatalogGameUpserted(gameId, Instant.now(), "IGDB");
  }

  public static CatalogGameUpserted of(Long gameId, String source) {
    return new CatalogGameUpserted(gameId, Instant.now(), source);
  }
}
