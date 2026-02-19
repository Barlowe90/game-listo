package com.gamelist.catalogo.infrastructure.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/** Request DTO para sincronizar juegos desde IGDB. */
@Schema(description = "Petición para sincronizar juegos desde IGDB")
public record SyncIgdbRequest(
    @Schema(
            description =
                "ID del último juego sincronizado (checkpoint). Null para empezar desde el inicio",
            example = "150000")
        Long fromId,
    @Min(1)
        @Max(500)
        @Schema(
            description = "Número máximo de juegos a sincronizar",
            example = "500",
            defaultValue = "500")
        Integer limit) {

  public SyncIgdbRequest {
    if (limit == null || limit <= 0 || limit > 500) {
      limit = 500;
    }
  }
}
