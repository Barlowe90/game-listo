package com.gamelisto.catalogo.domain.game;

import com.gamelisto.catalogo.domain.CoverUrl;
import com.gamelisto.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/** Tests para el Value Object CoverUrl. */
class CoverUrlTest {

  @Test
  @DisplayName("Debe crear CoverUrl válido con URL")
  void debeCrearCoverUrlValido() {
    // Arrange & Act
    CoverUrl coverUrl =
        CoverUrl.of("https://images.igdb.com/igdb/image/upload/t_cover_big/co1234.jpg");

    // Assert
    assertThat(coverUrl.value())
        .isEqualTo("https://images.igdb.com/igdb/image/upload/t_cover_big/co1234.jpg");
    assertThat(coverUrl.isEmpty()).isFalse();
  }

  @Test
  @DisplayName("Debe crear CoverUrl vacío si se pasa null")
  void debeCrearCoverUrlVacioConNull() {
    // Arrange & Act
    CoverUrl coverUrl = CoverUrl.of(null);

    // Assert
    assertThat(coverUrl.value()).isNull();
    assertThat(coverUrl.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Debe crear CoverUrl vacío con método empty()")
  void debeCrearCoverUrlVacioConMetodoEmpty() {
    // Arrange & Act
    CoverUrl coverUrl = CoverUrl.empty();

    // Assert
    assertThat(coverUrl.value()).isNull();
    assertThat(coverUrl.isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Debe eliminar espacios en blanco")
  void debeEliminarEspaciosEnBlanco() {
    // Arrange & Act
    CoverUrl coverUrl = CoverUrl.of("  https://example.com/cover.jpg  ");

    // Assert
    assertThat(coverUrl.value()).isEqualTo("https://example.com/cover.jpg");
  }

  @Test
  @DisplayName("Debe lanzar excepción si excede 500 caracteres")
  void debeLanzarExcepcionSiExcede500Caracteres() {
    // Arrange
    String urlLarga = "https://example.com/" + "A".repeat(500);

    // Act & Assert
    assertThatThrownBy(() -> CoverUrl.of(urlLarga))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("excede los 500 caracteres");
  }

  @Test
  @DisplayName("Debe aceptar exactamente 500 caracteres")
  void debeAceptar500Caracteres() {
    // Arrange
    String url500 = "https://example.com/" + "A".repeat(480); // Total 500

    // Act
    CoverUrl coverUrl = CoverUrl.of(url500);

    // Assert
    assertThat(coverUrl.value()).hasSize(500);
  }

  @Test
  @DisplayName("Debe ser igual si tienen el mismo valor")
  void debeSerIgualSiMismoValor() {
    // Arrange
    CoverUrl url1 = CoverUrl.of("https://example.com/cover.jpg");
    CoverUrl url2 = CoverUrl.of("https://example.com/cover.jpg");

    // Assert
    assertThat(url1).isEqualTo(url2);
    assertThat(url1.hashCode()).hasSameHashCodeAs(url2.hashCode());
  }

  @Test
  @DisplayName("Dos CoverUrls vacíos deben ser iguales")
  void dosCoverUrlsVaciosDebenSerIguales() {
    // Arrange
    CoverUrl url1 = CoverUrl.empty();
    CoverUrl url2 = CoverUrl.of(null);

    // Assert
    assertThat(url1).isEqualTo(url2);
  }
}
