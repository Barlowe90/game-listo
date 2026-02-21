package com.gamelist.catalogo.infrastructure.out.dto;

import com.gamelist.catalogo.application.dto.out.SyncResultDTO;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resultado de la sincronización de IGDB")
public record SyncStatusResponse(
    @Schema(description = "Total de elementos sincronizados", example = "500") int totalSynced,
    @Schema(description = "Último ID procesado (si aplica)", example = "999") Long lastId,
    @Schema(description = "Mensaje descriptivo", example = "Sincronización de juegos completada")
        String message) {

  public static SyncStatusResponse from(SyncResultDTO result, String message) {
    return new SyncStatusResponse(result.totalSynced(), result.lastId(), message);
  }
}
