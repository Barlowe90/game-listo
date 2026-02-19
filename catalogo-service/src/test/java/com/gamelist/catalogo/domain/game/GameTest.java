package com.gamelist.catalogo_service.domain.game;

import com.gamelist.catalogo_service.domain.catalog.PlatformId;
import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

/** Tests para la entidad Game (Aggregate Root). Pruebas de dominio puro - sin Spring. */
class GameTest {

  @Test
  @DisplayName("Debe crear un juego nuevo con factory method create()")
  void debeCrearJuegoNuevo() {
    // Arrange
    GameId id = GameId.of(12345L);
    GameName name = GameName.of("The Legend of Zelda");
    Summary summary = Summary.of("Un juego de aventuras épico");
    CoverUrl coverUrl = CoverUrl.of("https://example.com/cover.jpg");

    // Act
    Game game = Game.create(id, name, summary, coverUrl);

    // Assert
    assertThat(game).isNotNull();
    assertThat(game.getId()).isEqualTo(id);
    assertThat(game.getName()).isEqualTo(name);
    assertThat(game.getSummary()).isEqualTo(summary);
    assertThat(game.getCoverUrl()).isEqualTo(coverUrl);
    assertThat(game.getPlatformIds()).isEmpty();
    assertThat(game.getCreatedAt()).isNotNull();
    assertThat(game.getUpdatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Debe crear juego con summary y cover vacíos si son null")
  void debeCrearJuegoConCamposOpcionales() {
    // Arrange
    GameId id = GameId.of(100L);
    GameName name = GameName.of("Minimal Game");

    // Act
    Game game = Game.create(id, name, null, null);

    // Assert
    assertThat(game.getSummary()).isNotNull();
    assertThat(game.getSummary().isEmpty()).isTrue();
    assertThat(game.getCoverUrl()).isNotNull();
    assertThat(game.getCoverUrl().isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Debe lanzar excepción si se crea sin ID")
  void debeLanzarExcepcionSiCreaSinId() {
    // Arrange
    GameName name = GameName.of("Test Game");

    // Act & Assert
    assertThatThrownBy(() -> Game.create(null, name, null, null))
        .isInstanceOf(InvalidGameDataException.class)
        .hasMessageContaining("ID del juego es obligatorio");
  }

  @Test
  @DisplayName("Debe lanzar excepción si se crea sin nombre")
  void debeLanzarExcepcionSiCreaSinNombre() {
    // Arrange
    GameId id = GameId.of(100L);

    // Act & Assert
    assertThatThrownBy(() -> Game.create(id, null, null, null))
        .isInstanceOf(InvalidGameDataException.class)
        .hasMessageContaining("nombre del juego es obligatorio");
  }

  @Test
  @DisplayName("Debe reconstituir un juego desde la base de datos")
  void debeReconstituirJuegoDesdeBD() {
    // Arrange
    GameId id = GameId.of(500L);
    GameName name = GameName.of("Elden Ring");
    Summary summary = Summary.of("Un RPG de acción");
    CoverUrl coverUrl = CoverUrl.of("https://example.com/elden.jpg");
    Set<PlatformId> platforms = Set.of(PlatformId.of(48L), PlatformId.of(49L), PlatformId.of(6L));
    Instant createdAt = Instant.parse("2023-01-01T00:00:00Z");
    Instant updatedAt = Instant.parse("2023-06-01T00:00:00Z");

    // Act
    Game game = Game.reconstitute(id, name, summary, coverUrl, platforms, createdAt, updatedAt);

    // Assert
    assertThat(game.getId()).isEqualTo(id);
    assertThat(game.getName()).isEqualTo(name);
    assertThat(game.getSummary()).isEqualTo(summary);
    assertThat(game.getCoverUrl()).isEqualTo(coverUrl);
    assertThat(game.getPlatformIds()).hasSize(3);
    assertThat(game.getCreatedAt()).isEqualTo(createdAt);
    assertThat(game.getUpdatedAt()).isEqualTo(updatedAt);
  }

  @Test
  @DisplayName("Debe actualizar metadatos del juego")
  void debeActualizarMetadatos() {
    // Arrange
    Game game =
        Game.create(
            GameId.of(100L),
            GameName.of("Old Name"),
            Summary.of("Old summary"),
            CoverUrl.of("https://old.com/cover.jpg"));
    Instant initialUpdatedAt = game.getUpdatedAt();

    // Esperar un poco para que el timestamp cambie
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
    }

    // Act
    GameName newName = GameName.of("New Name");
    Summary newSummary = Summary.of("New summary");
    CoverUrl newCoverUrl = CoverUrl.of("https://new.com/cover.jpg");
    game.updateMetadata(newName, newSummary, newCoverUrl);

    // Assert
    assertThat(game.getName()).isEqualTo(newName);
    assertThat(game.getSummary()).isEqualTo(newSummary);
    assertThat(game.getCoverUrl()).isEqualTo(newCoverUrl);
    assertThat(game.getUpdatedAt()).isAfter(initialUpdatedAt);
  }

  @Test
  @DisplayName("Debe añadir plataforma al juego")
  void debeAñadirPlataforma() {
    // Arrange
    Game game = Game.create(GameId.of(100L), GameName.of("Test Game"), null, null);

    // Act
    game.addPlatform(PlatformId.of(48L)); // PS5
    game.addPlatform(PlatformId.of(6L)); // PC

    // Assert
    assertThat(game.getPlatformIds())
        .containsExactlyInAnyOrder(PlatformId.of(48L), PlatformId.of(6L));
  }

  @Test
  @DisplayName("Debe lanzar excepción al añadir plataforma con ID inválido")
  void debeLanzarExcepcionAlAñadirPlataformaInvalida() {
    // Arrange
    Game game = Game.create(GameId.of(100L), GameName.of("Test Game"), null, null);

    // Act & Assert
    assertThatThrownBy(() -> game.addPlatform(null))
        .isInstanceOf(InvalidGameDataException.class)
        .hasMessageContaining("PlatformId no puede ser nulo");
  }

  @Test
  @DisplayName("Debe establecer plataformas (reemplaza existentes)")
  void debeEstablecerPlataformas() {
    // Arrange
    Game game = Game.create(GameId.of(100L), GameName.of("Test Game"), null, null);
    game.addPlatform(PlatformId.of(48L));
    game.addPlatform(PlatformId.of(49L));

    // Act
    Set<PlatformId> newPlatforms =
        new HashSet<>(Set.of(PlatformId.of(6L), PlatformId.of(130L), PlatformId.of(167L)));
    game.setPlatforms(newPlatforms);

    // Assert
    assertThat(game.getPlatformIds())
        .containsExactlyInAnyOrder(PlatformId.of(6L), PlatformId.of(130L), PlatformId.of(167L));
  }

  @Test
  @DisplayName("Debe eliminar una plataforma")
  void debeEliminarPlataforma() {
    // Arrange
    Game game = Game.create(GameId.of(100L), GameName.of("Test Game"), null, null);
    game.addPlatform(PlatformId.of(48L));
    game.addPlatform(PlatformId.of(49L));
    game.addPlatform(PlatformId.of(6L));

    // Act
    game.removePlatform(PlatformId.of(49L));

    // Assert
    assertThat(game.getPlatformIds())
        .containsExactlyInAnyOrder(PlatformId.of(48L), PlatformId.of(6L));
  }

  @Test
  @DisplayName("Debe verificar si tiene portada")
  void debeVerificarSiTienePortada() {
    // Arrange
    Game gameConPortada =
        Game.create(
            GameId.of(100L),
            GameName.of("Test Game"),
            null,
            CoverUrl.of("https://example.com/cover.jpg"));

    Game gameSinPortada = Game.create(GameId.of(200L), GameName.of("Test Game 2"), null, null);

    // Assert
    assertThat(gameConPortada.hasCover()).isTrue();
    assertThat(gameSinPortada.hasCover()).isFalse();
  }

  @Test
  @DisplayName("Debe verificar si tiene resumen")
  void debeVerificarSiTieneResumen() {
    // Arrange
    Game gameConResumen =
        Game.create(
            GameId.of(100L), GameName.of("Test Game"), Summary.of("Un resumen interesante"), null);

    Game gameSinResumen = Game.create(GameId.of(200L), GameName.of("Test Game 2"), null, null);

    // Assert
    assertThat(gameConResumen.hasSummary()).isTrue();
    assertThat(gameSinResumen.hasSummary()).isFalse();
  }

  @Test
  @DisplayName("Dos juegos con mismo ID deben ser iguales")
  void dosJuegosConMismoIdDebenSerIguales() {
    // Arrange
    GameId id = GameId.of(100L);
    Game game1 = Game.create(id, GameName.of("Game 1"), null, null);
    Game game2 = Game.create(id, GameName.of("Game 2"), null, null);

    // Assert
    assertThat(game1).isEqualTo(game2);
    assertThat(game1.hashCode()).isEqualTo(game2.hashCode());
  }

  @Test
  @DisplayName("Dos juegos con diferente ID no deben ser iguales")
  void dosJuegosConDiferenteIdNoDebenSerIguales() {
    // Arrange
    Game game1 = Game.create(GameId.of(100L), GameName.of("Game 1"), null, null);
    Game game2 = Game.create(GameId.of(200L), GameName.of("Game 1"), null, null);

    // Assert
    assertThat(game1).isNotEqualTo(game2);
  }

  @Test
  @DisplayName("Debe tener representación toString correcta")
  void debeTenerToStringCorrecto() {
    // Arrange
    Game game = Game.create(GameId.of(12345L), GameName.of("Test Game"), null, null);
    game.addPlatform(PlatformId.of(48L));
    game.addPlatform(PlatformId.of(6L));

    // Act
    String result = game.toString();

    // Assert
    assertThat(result).contains("Game");
    assertThat(result).contains("id=");
    assertThat(result).contains("name=");
    assertThat(result).contains("platformCount=2");
  }
}
