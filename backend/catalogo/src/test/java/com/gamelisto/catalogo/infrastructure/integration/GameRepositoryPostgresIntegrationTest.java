package com.gamelisto.catalogo.infrastructure.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.gamelisto.catalogo.domain.CoverUrl;
import com.gamelisto.catalogo.domain.Game;
import com.gamelisto.catalogo.domain.GameCardSummary;
import com.gamelisto.catalogo.domain.GameId;
import com.gamelisto.catalogo.domain.GameName;
import com.gamelisto.catalogo.domain.GameRepositorio;
import com.gamelisto.catalogo.domain.PageResult;
import com.gamelisto.catalogo.domain.Summary;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

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

  private Game crearJuego(long id, String nombre, List<String> plataformas) {
    return crearJuego(id, nombre, plataformas, List.of());
  }

  private Game crearJuego(
      long id, String nombre, List<String> plataformas, List<String> gameModes) {
    return Game.reconstitute(
        GameId.of(id),
        GameName.of(nombre),
        Summary.of("Resumen de " + nombre),
        CoverUrl.of("https://img/" + id + ".jpg"),
        plataformas,
        null,
        null,
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        List.of(),
        gameModes,
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

  @Test
  @DisplayName("Debe filtrar resumenes paginados por plataforma ignorando mayusculas")
  void debeFiltrarResumenesPaginadosPorPlataformaIgnorandoMayusculas() {
    gameRepository.save(crearJuego(2001L, "Astro Bot", List.of("PlayStation 5"), List.of("Single player")));
    gameRepository.save(
        crearJuego(
            2002L,
            "Forza Horizon 5",
            List.of("PC", "Xbox Series X|S"),
            List.of("Co-operative", "Online multiplayer")));
    gameRepository.save(crearJuego(2003L, "Zelda", List.of("Nintendo Switch")));

    PageResult<GameCardSummary> result = gameRepository.findSummaries(0, 10, List.of("pc"));

    assertThat(result.totalElements()).isEqualTo(1);
    assertThat(result.content()).extracting(GameCardSummary::name).containsExactly("Forza Horizon 5");
    assertThat(result.content().getFirst().gameModes())
        .containsExactly("Co-operative", "Online multiplayer");
  }

  @Test
  @DisplayName("No debe duplicar resumenes cuando varias plataformas coinciden con el filtro")
  void noDebeDuplicarResumenesCuandoVariasPlataformasCoincidenConElFiltro() {
    gameRepository.save(crearJuego(2101L, "Halo Infinite", List.of("PC", "Xbox Series X|S")));

    PageResult<GameCardSummary> result =
        gameRepository.findSummaries(0, 10, List.of("pc", "xbox series x|s"));

    assertThat(result.totalElements()).isEqualTo(1);
    assertThat(result.content()).hasSize(1);
    assertThat(result.content().getFirst().name()).isEqualTo("Halo Infinite");
  }

  @Test
  @DisplayName("Debe devolver solo los campos ligeros necesarios para la card del catalogo")
  void debeDevolverResumenLigeroParaCards() {
    gameRepository.save(
        crearJuego(
            2201L,
            "Sea of Thieves",
            List.of("PC", "Xbox Series X|S"),
            List.of("Co-operative", "Online multiplayer")));

    PageResult<GameCardSummary> result = gameRepository.findSummaries(0, 10, List.of());

    assertThat(result.content()).isNotEmpty();
    GameCardSummary summary =
        result.content().stream().filter(game -> game.id().equals(2201L)).findFirst().orElseThrow();
    assertThat(summary.coverUrl()).isEqualTo("https://img/2201.jpg");
    assertThat(summary.platforms()).containsExactly("PC", "Xbox Series X|S");
    assertThat(summary.gameModes()).containsExactly("Co-operative", "Online multiplayer");
  }
}
