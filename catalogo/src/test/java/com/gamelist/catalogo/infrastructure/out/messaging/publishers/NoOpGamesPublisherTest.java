package com.gamelist.catalogo.infrastructure.out.messaging.publishers;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.gamelist.catalogo.domain.events.GameCreado;
import java.util.List;

import com.gamelist.catalogo.infrastructure.out.messaging.NoOpGamesPublisherRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("NoOpGamesPublisherRepositorio - Tests")
class NoOpGamesPublisherTest {

  private NoOpGamesPublisherRepositorio publisher;

  @BeforeEach
  void setUp() {
    publisher = new NoOpGamesPublisherRepositorio();
  }

  @Test
  @DisplayName("Debe ejecutar publicarGameCreado sin lanzar excepciones")
  void debeEjecutarPublicarGameCreadoSinLanzarExcepciones() {
    // Given
    GameCreado event =
        GameCreado.of(
            "game-1",
            "name",
            "summary",
            "cover",
            List.of("PC"), // platforms
            "FULL", // gameType
            "RELEASED", // gameStatus
            List.of(), // alternativeNames
            List.of(), // dlcs
            List.of(), // expandedGames
            List.of(), // expansionIds
            List.of(), // externalGames
            List.of(), // franchises
            List.of(), // gameModes
            List.of(), // genres
            List.of(), // involvedCompanies
            List.of(), // keywords
            List.of(), // multiplayerModeIds
            null, // parentGameId
            List.of(), // playerPerspectives
            List.of(), // remakeIds
            List.of(), // remasterIds
            List.of(), // similarGames
            List.of() // themes
            );

    // When & Then - No debe lanzar excepción
    assertThatCode(() -> publisher.publicarGameCreado(event)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe manejar eventos null sin lanzar excepciones")
  void debeManejarEventosNullSinLanzarExcepciones() {
    assertThatCode(() -> publisher.publicarGameCreado(null)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe publicar múltiples eventos consecutivamente")
  void debePublicarMultiplesEventosConsecutivamente() {
    GameCreado e1 =
        GameCreado.of(
            "g1",
            "n1",
            "s1",
            "c1",
            List.of("PC"),
            "FULL",
            "RELEASED",
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of());
    GameCreado e2 =
        GameCreado.of(
            "g2",
            "n2",
            "s2",
            "c2",
            List.of("PC"),
            "FULL",
            "RELEASED",
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            null,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of());

    assertThatCode(
            () -> {
              publisher.publicarGameCreado(e1);
              publisher.publicarGameCreado(e2);
            })
        .doesNotThrowAnyException();
  }
}
