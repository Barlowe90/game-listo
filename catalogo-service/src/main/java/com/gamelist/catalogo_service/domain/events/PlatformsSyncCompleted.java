package com.gamelist.catalogo_service.domain.events;

import java.time.Instant;

/**
 * Evento de dominio lanzado cuando se completa la sincronización de plataformas desde IGDB
 *
 * @param totalPlatforms Total de plataformas sincronizadas
 * @param occurredAt Timestamp del evento
 */
public record PlatformsSyncCompleted(int totalPlatforms, Instant occurredAt) {

    public static PlatformsSyncCompleted of(int totalPlatforms) {
        return new PlatformsSyncCompleted(totalPlatforms, Instant.now());
    }
}
