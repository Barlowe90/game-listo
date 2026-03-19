package com.gamelisto.catalogo.infrastructure.in.igdb;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IgdbImageUrlBuilderTest {

  @Test
  @DisplayName("Debe construir una URL de alta calidad a partir de image_id")
  void debeConstruirUrlDeAltaCalidad() {
    String url =
        IgdbImageUrlBuilder.buildSizedUrl(
            "co1234", "//images.igdb.com/igdb/image/upload/t_thumb/co1234.png", "1080p");

    assertThat(url).isEqualTo("https://images.igdb.com/igdb/image/upload/t_1080p/co1234.png");
  }

  @Test
  @DisplayName("Debe usar jpg por defecto si la URL original no tiene extension")
  void debeUsarJpgPorDefecto() {
    String url = IgdbImageUrlBuilder.buildSizedUrl("sc5678", null, "1080p");

    assertThat(url).isEqualTo("https://images.igdb.com/igdb/image/upload/t_1080p/sc5678.jpg");
  }

  @Test
  @DisplayName("Debe normalizar URLs protocol-relative")
  void debeNormalizarUrlsProtocolRelative() {
    String url =
        IgdbImageUrlBuilder.normalizeUrl("//images.igdb.com/igdb/image/upload/t_thumb/a.jpg");

    assertThat(url).isEqualTo("https://images.igdb.com/igdb/image/upload/t_thumb/a.jpg");
  }

  @Test
  @DisplayName("Debe mantener la URL original normalizada si no hay image_id")
  void debeMantenerUrlOriginalSiNoHayImageId() {
    String url =
        IgdbImageUrlBuilder.buildSizedUrl(
            null, "//images.igdb.com/igdb/image/upload/t_thumb/co1234.jpg", "1080p");

    assertThat(url).isEqualTo("https://images.igdb.com/igdb/image/upload/t_thumb/co1234.jpg");
  }
}
