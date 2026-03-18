package com.gamelisto.catalogo.infrastructure.out.persistence.mongo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gamelisto.catalogo.domain.GameDetail;
import com.gamelisto.catalogo.domain.GameId;
import java.util.List;
import java.util.Optional;
import org.bson.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@ExtendWith(MockitoExtension.class)
@DisplayName("RepositorioGameDetailMongo")
class RepositorioGameDetailMongoTest {

  @Mock private MongoTemplate mongoTemplate;
  @Mock private GameDetailMapper mapper;

  @InjectMocks private RepositorioGameDetailMongo repository;

  @Captor private ArgumentCaptor<GameDetailDocument> documentCaptor;
  @Captor private ArgumentCaptor<Query> queryCaptor;

  @Test
  @DisplayName("Debe guardar con ID determinista por gameId")
  void debeGuardarConIdDeterministaPorGameId() {
    GameDetail detail =
        GameDetail.create(GameId.of(397L), List.of("https://img/1"), List.of("https://yt/1"));
    GameDetailDocument mapped =
        new GameDetailDocument(null, 397L, List.of("https://img/1"), List.of("https://yt/1"));

    when(mapper.toDocument(detail)).thenReturn(mapped);
    when(mongoTemplate.save(any(GameDetailDocument.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    when(mapper.toDomain(any(GameDetailDocument.class))).thenReturn(detail);

    GameDetail saved = repository.save(detail);

    verify(mongoTemplate).save(documentCaptor.capture());
    assertThat(documentCaptor.getValue().getId()).isEqualTo("game-detail-397");
    assertThat(saved).isSameAs(detail);
  }

  @Test
  @DisplayName("Debe priorizar el documento canonico al buscar por gameId")
  void debePriorizarElDocumentoCanonicoAlBuscarPorGameId() {
    GameId gameId = GameId.of(397L);
    GameDetailDocument canonical =
        new GameDetailDocument(
            "game-detail-397", 397L, List.of("https://img/1"), List.of("https://yt/1"));
    GameDetail expected =
        GameDetail.create(gameId, List.of("https://img/1"), List.of("https://yt/1"));

    when(mongoTemplate.find(any(Query.class), eq(GameDetailDocument.class)))
        .thenReturn(List.of(canonical));
    when(mapper.toDomain(canonical)).thenReturn(expected);

    Optional<GameDetail> result = repository.findByGameId(gameId);

    verify(mongoTemplate).find(queryCaptor.capture(), eq(GameDetailDocument.class));
    Query executedQuery = queryCaptor.getValue();

    assertThat(result).containsSame(expected);
    assertThat(executedQuery.getQueryObject().getString("_id")).isEqualTo("game-detail-397");
    assertThat(executedQuery.getLimit()).isEqualTo(1);
    assertThat(executedQuery.getFieldsObject().get("screenshots"))
        .isEqualTo(new Document("$slice", 40));
    assertThat(executedQuery.getFieldsObject().get("videos")).isEqualTo(new Document("$slice", 40));
  }

  @Test
  @DisplayName("Debe usar fallback legacy cuando no existe documento canonico")
  void debeUsarFallbackLegacyCuandoNoExisteDocumentoCanonico() {
    GameId gameId = GameId.of(397L);
    GameDetailDocument legacy =
        new GameDetailDocument("legacy-1", 397L, List.of("https://img/9"), List.of());
    GameDetail expected = GameDetail.create(gameId, List.of("https://img/9"), List.of());

    when(mongoTemplate.find(any(Query.class), eq(GameDetailDocument.class)))
        .thenReturn(List.of())
        .thenReturn(List.of(legacy));
    when(mapper.toDomain(legacy)).thenReturn(expected);

    Optional<GameDetail> result = repository.findByGameId(gameId);

    verify(mongoTemplate, times(2)).find(queryCaptor.capture(), eq(GameDetailDocument.class));

    List<Query> executedQueries = queryCaptor.getAllValues();
    Query canonicalQuery = executedQueries.getFirst();
    Query legacyQuery = executedQueries.getLast();

    assertThat(result).containsSame(expected);
    assertThat(canonicalQuery.getQueryObject().getString("_id")).isEqualTo("game-detail-397");
    assertThat(legacyQuery.getQueryObject().getLong("gameId")).isEqualTo(397L);
    assertThat(legacyQuery.getSortObject().getInteger("_id")).isEqualTo(-1);
    assertThat(legacyQuery.getLimit()).isEqualTo(1);
  }
}
