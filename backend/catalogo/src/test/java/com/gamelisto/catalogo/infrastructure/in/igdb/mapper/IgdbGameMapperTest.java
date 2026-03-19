package com.gamelisto.catalogo.infrastructure.in.igdb.mapper;

import com.gamelisto.catalogo.application.usecases.IgdbGameDTO;
import com.gamelisto.catalogo.infrastructure.in.igdb.dto.GameFromIGDBResponse;
import com.gamelisto.catalogo.infrastructure.in.igdb.dto.IgdbCoverRequest;
import com.gamelisto.catalogo.infrastructure.in.igdb.dto.IgdbScreenshotRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class IgdbGameMapperTest {

  private final IgdbGameMapper mapper = new IgdbGameMapper();

  @Test
  @DisplayName("Debe mapear cover y screenshots a URLs de alta calidad")
  void debeMapearCoverYScreenshotsAltaCalidad() {
    GameFromIGDBResponse response =
        new GameFromIGDBResponse(
            null,
            new IgdbCoverRequest(
                "//images.igdb.com/igdb/image/upload/t_thumb/co1234.jpg", "co1234", 264, 374),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "Halo",
            null,
            null,
            null,
            null,
            null,
            List.of(
                new IgdbScreenshotRequest(
                    "//images.igdb.com/igdb/image/upload/t_thumb/sc5678.jpg",
                    "sc5678",
                    1920,
                    1080)),
            null,
            "Resumen",
            null,
            null,
            1942L);

    IgdbGameDTO dto = mapper.toApplicationDto(response);

    assertThat(dto.coverUrl())
        .isEqualTo("https://images.igdb.com/igdb/image/upload/t_1080p/co1234.jpg");
    assertThat(dto.screenshots())
        .containsExactly("https://images.igdb.com/igdb/image/upload/t_1080p/sc5678.jpg");
  }
}
