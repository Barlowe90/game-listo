package com.gamelisto.catalogo.infrastructure.in.igdb;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IgdbQueryBuilderTest {

  private final IgdbQueryBuilder queryBuilder = new IgdbQueryBuilder();

  @Test
  @DisplayName("Debe pedir image_id para covers y screenshots")
  void debePedirImageIdParaCoversYScreenshots() {
    String query = queryBuilder.buildGamesQuery(0L, 100);

    assertThat(query).contains("cover.image_id,");
    assertThat(query).contains("cover.url,");
    assertThat(query).contains("screenshots.image_id,");
    assertThat(query).contains("screenshots.url,");
  }

  @Test
  @DisplayName("Debe pedir image_id para logos de plataforma")
  void debePedirImageIdParaLogosDePlataforma() {
    String query = queryBuilder.buildPlatformsQuery();

    assertThat(query).contains("platform_logo.image_id,");
    assertThat(query).contains("platform_logo.url,");
  }
}
