package com.gamelist.catalogo_service.infrastructure.integration;
import com.gamelist.catalogo_service.domain.catalog.PlatformId;
import com.gamelist.catalogo_service.domain.game.*;
import com.gamelist.catalogo_service.domain.repositories.IGameRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
@DisplayName("GameRepositoryPostgres - Tests de Integracion")
class GameRepositoryPostgresIntegrationTest {
  @Autowired private IGameRepository gameRepository;
  private Game crearJuego(long id, String nombre) {
    return Game.create(GameId.of(id), GameName.of(nombre),
        Summary.of("Resumen de " + nombre), CoverUrl.of("https://img/" + id + ".jpg"));
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
  @DisplayName("Debe hacer upsert de juego existente sin crear duplicado")
  void debeActualizarJuegoExistente() {
    Game orig = crearJuego(2001L, "Nombre Original");
    gameRepository.save(orig);
    Game upd = Game.reconstitute(GameId.of(2001L), GameName.of("Nombre Actualizado"),
        Summary.of("Nuevo resumen"), CoverUrl.empty(), Set.of(),
        orig.getCreatedAt(), java.time.Instant.now());
    gameRepository.save(upd);
    Optional<Game> r = gameRepository.findById(GameId.of(2001L));
    assertThat(r).isPresent();
    assertThat(r.get().getName().value()).isEqualTo("Nombre Actualizado");
  }
  @Test
  @DisplayName("Debe devolver Optional vacio cuando el juego no existe")
  void debeDevolverEmptyOptionalSiJuegoNoExiste() {
    assertThat(gameRepository.findById(GameId.of(99999L))).isEmpty();
  }
  @Test
  @DisplayName("Debe eliminar un juego por ID")
  void debeEliminarJuego() {
    gameRepository.save(crearJuego(3001L, "Juego a Eliminar"));
    gameRepository.deleteById(GameId.of(3001L));
    assertThat(gameRepository.findById(GameId.of(3001L))).isEmpty();
  }
  @Test
  @DisplayName("Debe buscar juego por nombre exacto")
  void debeBuscarJuegoPorNombre() {
    gameRepository.save(crearJuego(5001L, "God of War"));
    assertThat(gameRepository.findByName("God of War")).isPresent();
  }
}