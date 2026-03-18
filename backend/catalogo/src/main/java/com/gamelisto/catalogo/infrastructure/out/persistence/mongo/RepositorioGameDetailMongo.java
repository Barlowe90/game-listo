package com.gamelisto.catalogo.infrastructure.out.persistence.mongo;

import com.gamelisto.catalogo.domain.GameId;
import com.gamelisto.catalogo.domain.GameDetail;
import com.gamelisto.catalogo.domain.GameDetailRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RepositorioGameDetailMongo implements GameDetailRepositorio {

  private static final int MAX_MEDIA_ITEMS = 40;
  private static final String DOCUMENT_ID_PREFIX = "game-detail-";

  private final MongoTemplate mongoTemplate;
  private final GameDetailMapper mapper;

  @Override
  public GameDetail save(GameDetail gameDetail) {
    GameDetailDocument document = mapper.toDocument(gameDetail);
    document.setId(buildDocumentId(gameDetail.getGameId()));
    GameDetailDocument saved = mongoTemplate.save(document);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<GameDetail> findByGameId(GameId gameId) {
    return findCanonicalDocument(gameId)
        .or(() -> findLegacyDocument(gameId))
        .map(mapper::toDomain);
  }

  private Optional<GameDetailDocument> findCanonicalDocument(GameId gameId) {
    Query query = baseMediaQuery();
    query.addCriteria(Criteria.where("_id").is(buildDocumentId(gameId)));
    return findFirst(query);
  }

  private Optional<GameDetailDocument> findLegacyDocument(GameId gameId) {
    Query query = baseMediaQuery();
    query.addCriteria(Criteria.where("gameId").is(gameId.value()));
    query.with(Sort.by(Sort.Direction.DESC, "_id"));
    return findFirst(query);
  }

  private Optional<GameDetailDocument> findFirst(Query query) {
    return mongoTemplate.find(query.limit(1), GameDetailDocument.class).stream().findFirst();
  }

  private Query baseMediaQuery() {
    Query query = new Query();
    query.fields().include("id").include("gameId");
    query.fields().slice("screenshots", MAX_MEDIA_ITEMS);
    query.fields().slice("videos", MAX_MEDIA_ITEMS);
    return query;
  }

  private String buildDocumentId(GameId gameId) {
    return DOCUMENT_ID_PREFIX + gameId.value();
  }
}
