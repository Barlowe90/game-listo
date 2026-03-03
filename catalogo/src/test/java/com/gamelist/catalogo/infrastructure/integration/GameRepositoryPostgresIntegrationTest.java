package com.gamelist.catalogo.infrastructure.integration;

import com.gamelist.catalogo.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
@DisplayName("GameRepositoryPostgres - Tests de Integracion")
class GameRepositoryPostgresIntegrationTest {
  @Autowired private GameRepositorio gameRepository;

  private Game crearJuego(long id, String nombre) {
    return Game.create(
        GameId.of(id),
        GameName.of(nombre),
        Summary.of("Resumen de " + nombre),
        CoverUrl.of("https://img/" + id + ".jpg"));
  }

  @Test
  @DisplayName("Debe guardar un juego y recuperarlo por ID correctamente")
  void debeGuardarYRecuperarUnJuego() {
    gameRepository.save(crearJuego(1001L, "Super Mario Odyssey"));
    Optional<Game> r = gameRepository.findById(GameId.of(1001L));
    assertThat(r).isPresent();
    assertThat(r.get().getName().value()).isEqualTo("Super Mario Odyssey");
  }

  @Test
  @DisplayName("Debe devolver Optional vacio cuando el juego no existe")
  void debeDevolverEmptyOptionalSiJuegoNoExiste() {
    assertThat(gameRepository.findById(GameId.of(99999L))).isEmpty();
  }
}
