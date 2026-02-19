package com.gamelist.catalogo.infrastructure.integration;

import com.gamelist.catalogo.domain.game.GameId;
import com.gamelist.catalogo.domain.gamedetail.GameDetail;
import com.gamelist.catalogo.domain.gamedetail.Screenshot;
import com.gamelist.catalogo.domain.gamedetail.Video;
import com.gamelist.catalogo.domain.repositories.IGameDetailRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@DisplayName("GameDetailRepositoryMongo - Tests de Integracion")
class GameDetailRepositoryMongoIntegrationTest {
  @Autowired private IGameDetailRepository gameDetailRepository;

  @Test
  @DisplayName("Debe guardar y recuperar un GameDetail por gameId")
  void debeGuardarYRecuperarGameDetail() {
    GameId gameId = GameId.of(10001L);
    GameDetail d =
        GameDetail.create(
            gameId,
            List.of(Screenshot.of("https://img/ss.jpg", 1920, 1080)),
            List.of(Video.of("https://yt/abc", "abc")));
    gameDetailRepository.save(d);
    Optional<GameDetail> r = gameDetailRepository.findByGameId(gameId);
    assertThat(r).isPresent();
    assertThat(r.get().getScreenshots()).hasSize(1);
    assertThat(r.get().getVideos()).hasSize(1);
  }

  @Test
  @DisplayName("Debe devolver Optional vacio si no existe detalle")
  void debeDevolverEmptyOptionalSiNoExisteDetalle() {
    assertThat(gameDetailRepository.findByGameId(GameId.of(99999L))).isEmpty();
  }

  @Test
  @DisplayName("Debe guardar GameDetail vacio")
  void debeGuardarGameDetailVacio() {
    GameId gameId = GameId.of(10003L);
    gameDetailRepository.save(GameDetail.empty(gameId));
    Optional<GameDetail> r = gameDetailRepository.findByGameId(gameId);
    assertThat(r).isPresent();
    assertThat(r.get().getScreenshots()).isEmpty();
  }
}
