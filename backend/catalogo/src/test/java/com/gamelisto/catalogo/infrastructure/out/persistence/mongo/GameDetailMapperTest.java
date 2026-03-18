package com.gamelisto.catalogo.infrastructure.out.persistence.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import com.gamelisto.catalogo.domain.GameDetail;
import com.gamelisto.catalogo.domain.GameId;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GameDetailMapper")
class GameDetailMapperTest {

  private final GameDetailMapper mapper = new GameDetailMapper();

  @Test
  @DisplayName("Debe sanitizar y limitar el multimedia al guardar")
  void debeSanitizarYLimitarElMultimediaAlGuardar() {
    List<String> screenshots =
        IntStream.range(0, 45).mapToObj(index -> " https://img/" + index + " ").toList();
    List<String> videos = List.of("https://yt/1", "", "https://yt/1", "  ", "https://yt/2");

    GameDetail detail = GameDetail.create(GameId.of(397L), screenshots, videos);

    GameDetailDocument document = mapper.toDocument(detail);

    assertThat(document.getGameId()).isEqualTo(397L);
    assertThat(document.getScreenshots()).hasSize(40);
    assertThat(document.getScreenshots().getFirst()).isEqualTo("https://img/0");
    assertThat(document.getScreenshots().getLast()).isEqualTo("https://img/39");
    assertThat(document.getVideos()).containsExactly("https://yt/1", "https://yt/2");
  }

  @Test
  @DisplayName("Debe sanitizar y limitar el multimedia al reconstruir dominio")
  void debeSanitizarYLimitarElMultimediaAlReconstruirDominio() {
    List<String> screenshots =
        IntStream.range(0, 42).mapToObj(index -> "https://img/" + index).toList();

    GameDetailDocument document =
        new GameDetailDocument(
            "legacy-397",
            397L,
            screenshots,
            Arrays.asList("https://yt/1", null, "https://yt/1", "https://yt/2"));

    GameDetail detail = mapper.toDomain(document);

    assertThat(detail.getGameId().value()).isEqualTo(397L);
    assertThat(detail.getScreenshots()).hasSize(40);
    assertThat(detail.getScreenshots().getFirst()).isEqualTo("https://img/0");
    assertThat(detail.getScreenshots().getLast()).isEqualTo("https://img/39");
    assertThat(detail.getVideos()).containsExactly("https://yt/1", "https://yt/2");
  }
}
