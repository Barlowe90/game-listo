package com.gamelist.catalogo_service.domain.gamedetail;

import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/** Tests para el Value Object Video. */
class VideoTest {

  @Test
  @DisplayName("Debe crear video con URL y videoId")
  void debeCrearVideoCompleto() {
    // Arrange & Act
    Video video = Video.of("https://youtube.com/watch?v=abc123", "abc123");

    // Assert
    assertThat(video.url()).isEqualTo("https://youtube.com/watch?v=abc123");
    assertThat(video.videoId()).isEqualTo("abc123");
    assertThat(video.hasVideoId()).isTrue();
  }

  @Test
  @DisplayName("Debe crear video solo con URL")
  void debeCrearVideoSoloURL() {
    // Arrange & Act
    Video video = Video.of("https://youtube.com/watch?v=xyz789");

    // Assert
    assertThat(video.url()).isEqualTo("https://youtube.com/watch?v=xyz789");
    assertThat(video.videoId()).isNull();
    assertThat(video.hasVideoId()).isFalse();
  }

  @Test
  @DisplayName("Debe lanzar excepción si URL es nula")
  void debeLanzarExcepcionSiURLNula() {
    // Act & Assert
    assertThatThrownBy(() -> Video.of(null, "abc123"))
        .isInstanceOf(InvalidGameDataException.class)
        .hasMessageContaining("URL del video no puede estar vacía");
  }

  @Test
  @DisplayName("Debe lanzar excepción si URL está vacía")
  void debeLanzarExcepcionSiURLVacia() {
    // Act & Assert
    assertThatThrownBy(() -> Video.of("")).isInstanceOf(InvalidGameDataException.class);
  }

  @Test
  @DisplayName("hasVideoId debe retornar false si videoId es string vacío")
  void hasVideoIdDebeFalseSiVideoIdVacio() {
    // Arrange
    Video video = Video.of("https://example.com/video", "");

    // Assert
    assertThat(video.hasVideoId()).isFalse();
  }

  @Test
  @DisplayName("Dos videos con mismos valores deben ser iguales")
  void dosVideosConMismosValoresDebenSerIguales() {
    // Arrange
    Video v1 = Video.of("https://youtube.com/watch?v=abc", "abc");
    Video v2 = Video.of("https://youtube.com/watch?v=abc", "abc");

    // Assert
    assertThat(v1).isEqualTo(v2);
    assertThat(v1.hashCode()).isEqualTo(v2.hashCode());
  }
}
