package com.gamelist.catalogo.domain.gamedetail;

import com.gamelist.catalogo.domain.game.GameId;
import com.gamelist.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Tests para el agregado GameDetail. */
class GameDetailTest {

  @Test
  @DisplayName("Debe crear GameDetail con screenshots y videos")
  void debeCrearGameDetailConContenido() {
    // Arrange
    GameId gameId = GameId.of(100L);
    List<String> alternativeNames = List.of("Alt Name 1", "Alt Name 2");
    String coverUrl = "https://example.com/cover.jpg";
    List<String> screenshots =
        List.of("https://example.com/screenshot1.jpg", "https://example.com/screenshot2.jpg");
    List<String> videos = List.of("https://youtube.com/watch?v=abc123");

    // Act
    GameDetail gameDetail =
        GameDetail.create(gameId, alternativeNames, coverUrl, screenshots, videos);

    // Assert
    assertThat(gameDetail.getGameId()).isEqualTo(gameId);
    assertThat(gameDetail.getAlternativeNames()).hasSize(2);
    assertThat(gameDetail.getCoverUrl()).isEqualTo(coverUrl);
    assertThat(gameDetail.getScreenshots()).hasSize(2);
    assertThat(gameDetail.getVideos()).hasSize(1);
    assertThat(gameDetail.hasContent()).isTrue();
    assertThat(gameDetail.hasScreenshots()).isTrue();
    assertThat(gameDetail.hasVideos()).isTrue();
  }

  @Test
  @DisplayName("Debe crear GameDetail vacío")
  void debeCrearGameDetailVacio() {
    // Arrange
    GameId gameId = GameId.of(200L);

    // Act
    GameDetail gameDetail = GameDetail.empty(gameId);

    // Assert
    assertThat(gameDetail.getGameId()).isEqualTo(gameId);
    assertThat(gameDetail.getScreenshots()).isEmpty();
    assertThat(gameDetail.getVideos()).isEmpty();
    assertThat(gameDetail.hasContent()).isFalse();
  }

  @Test
  @DisplayName("Debe lanzar excepción si GameId es nulo")
  void debeLanzarExcepcionSiGameIdNulo() {
    // Act & Assert
    assertThatThrownBy(
            () -> GameDetail.create(null, List.of(), null, new ArrayList<>(), new ArrayList<>()))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("GameId es obligatorio");
  }

  @Test
  @DisplayName("Debe añadir screenshot")
  void debeAñadirScreenshot() {
    // Arrange
    GameDetail gameDetail = GameDetail.empty(GameId.of(100L));
    String screenshot = "https://example.com/new.jpg";

    // Act
    gameDetail.addScreenshot(screenshot);

    // Assert
    assertThat(gameDetail.getScreenshots()).hasSize(1);
    assertThat(gameDetail.hasScreenshots()).isTrue();
  }

  @Test
  @DisplayName("Debe añadir video")
  void debeAñadirVideo() {
    // Arrange
    GameDetail gameDetail = GameDetail.empty(GameId.of(100L));
    String video = "https://youtube.com/watch?v=xyz";

    // Act
    gameDetail.addVideo(video);

    // Assert
    assertThat(gameDetail.getVideos()).hasSize(1);
    assertThat(gameDetail.hasVideos()).isTrue();
  }

  @Test
  @DisplayName("Debe establecer screenshots (reemplazar existentes)")
  void debeEstablecerScreenshots() {
    // Arrange
    GameDetail gameDetail =
        GameDetail.create(
            GameId.of(100L), List.of(), null, List.of("https://old.com/1.jpg"), new ArrayList<>());
    List<String> newScreenshots = List.of("https://new.com/1.jpg", "https://new.com/2.jpg");

    // Act
    gameDetail.setScreenshots(newScreenshots);

    // Assert
    assertThat(gameDetail.getScreenshots()).hasSize(2);
    assertThat(gameDetail.getScreenshots().get(0)).contains("new.com");
  }

  @Test
  @DisplayName("Debe establecer videos (reemplazar existentes)")
  void debeEstablecerVideos() {
    // Arrange
    GameDetail gameDetail =
        GameDetail.create(
            GameId.of(100L), List.of(), null, new ArrayList<>(), List.of("https://old.com/video"));
    List<String> newVideos = List.of("https://new.com/video1", "https://new.com/video2");

    // Act
    gameDetail.setVideos(newVideos);

    // Assert
    assertThat(gameDetail.getVideos()).hasSize(2);
  }

  @Test
  @DisplayName("getScreenshots debe retornar lista inmutable")
  void getScreenshotsDebeRetornarListaInmutable() {
    // Arrange
    GameDetail gameDetail =
        GameDetail.create(
            GameId.of(100L),
            List.of(),
            null,
            List.of("https://example.com/1.jpg"),
            new ArrayList<>());

    // Act & Assert
    assertThatThrownBy(() -> gameDetail.getScreenshots().add("https://example.com/2.jpg"))
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  @DisplayName("getVideos debe retornar lista inmutable")
  void getVideosDebeRetornarListaInmutable() {
    // Arrange
    GameDetail gameDetail =
        GameDetail.create(
            GameId.of(100L),
            List.of(),
            null,
            new ArrayList<>(),
            List.of("https://example.com/video"));

    // Act & Assert
    assertThatThrownBy(() -> gameDetail.getVideos().add("https://example.com/video2"))
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  @DisplayName("Dos GameDetail con mismo GameId deben ser iguales")
  void dosGameDetailConMismoGameIdDebenSerIguales() {
    // Arrange
    GameId gameId = GameId.of(100L);
    GameDetail gd1 = GameDetail.empty(gameId);
    GameDetail gd2 = GameDetail.create(gameId, List.of(), null, List.of("url"), new ArrayList<>());

    // Assert
    assertThat(gd1).isEqualTo(gd2);
    assertThat(gd1.hashCode()).hasSameHashCodeAs(gd2.hashCode());
  }
}
