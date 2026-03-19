package com.gamelisto.catalogo.infrastructure.in.igdb.mapper;

import com.gamelisto.catalogo.application.usecases.IgdbPlatformDTO;
import com.gamelisto.catalogo.infrastructure.in.igdb.dto.IgdbCoverRequest;
import com.gamelisto.catalogo.infrastructure.in.igdb.dto.IgdbNameRequest;
import com.gamelisto.catalogo.infrastructure.in.igdb.dto.PlatformFromIGDBResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IgdbPlatformMapperTest {

  private final IgdbPlatformMapper mapper = new IgdbPlatformMapper();

  @Test
  @DisplayName("Debe mapear el logo de plataforma con un tamaño mayor")
  void debeMapearLogoAltaCalidad() {
    PlatformFromIGDBResponse response =
        new PlatformFromIGDBResponse(
            48L,
            "PlayStation 4",
            "PS4",
            "PS4",
            new IgdbCoverRequest(
                "//images.igdb.com/igdb/image/upload/t_thumb/pl1234.png", "pl1234", 284, 160),
            new IgdbNameRequest("Console"));

    IgdbPlatformDTO dto = mapper.toApplicationDto(response);

    assertThat(dto.logoURL())
        .isEqualTo("https://images.igdb.com/igdb/image/upload/t_logo_med_2x/pl1234.png");
  }
}
