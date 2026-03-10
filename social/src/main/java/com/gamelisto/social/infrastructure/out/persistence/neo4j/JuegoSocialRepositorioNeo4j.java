package com.gamelisto.social.infrastructure.out.persistence.neo4j;

import com.gamelisto.social.dominio.JuegoSocialRepositorio;
import com.gamelisto.social.dominio.UserRef;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JuegoSocialRepositorioNeo4j implements JuegoSocialRepositorio {

  private static final String ESTADO_DESEADO = "DESEADO";
  private static final String ESTADO_JUGANDO = "JUGANDO";
  private static final int PREVIEW_LIMIT = 3;
  private final Neo4jClient neo4jClient;

  @Override
  @Transactional
  public void syncGameState(UUID userId, Long gameId, String estado) {
    String estadoNormalizado = estado != null ? estado.trim().toUpperCase() : "";

    Map<String, Object> params = Map.of("userId", userId.toString(), "gameId", gameId);

    if (ESTADO_DESEADO.equals(estadoNormalizado)) {
      neo4jClient
          .query(
              """
                          MERGE (u:User {id: $userId})
                          MERGE (g:Game {id: $gameId})
                          OPTIONAL MATCH (u)-[r1:WISHLISTED]->(g)
                          DELETE r1
                          WITH u, g
                          OPTIONAL MATCH (u)-[r2:PLAYING]->(g)
                          DELETE r2
                          WITH u, g
                          MERGE (u)-[:WISHLISTED]->(g)
                          """)
          .bindAll(params)
          .run();
      return;
    }

    if (ESTADO_JUGANDO.equals(estadoNormalizado)) {
      neo4jClient
          .query(
              """
                          MERGE (u:User {id: $userId})
                          MERGE (g:Game {id: $gameId})
                          OPTIONAL MATCH (u)-[r1:WISHLISTED]->(g)
                          DELETE r1
                          WITH u, g
                          OPTIONAL MATCH (u)-[r2:PLAYING]->(g)
                          DELETE r2
                          WITH u, g
                          MERGE (u)-[:PLAYING]->(g)
                          """)
          .bindAll(params)
          .run();
      return;
    }

    // Para cualquier otro estado, eliminamos la proyección social de ese juego
    neo4jClient
        .query(
            """
                      MERGE (u:User {id: $userId})
                      MERGE (g:Game {id: $gameId})
                      OPTIONAL MATCH (u)-[r1:WISHLISTED]->(g)
                      DELETE r1
                      WITH u, g
                      OPTIONAL MATCH (u)-[r2:PLAYING]->(g)
                      DELETE r2
                      """)
        .bindAll(params)
        .run();
  }

  @Override
  public long countFriendsWithGameInWishlist(UUID userId, Long gameId) {
    return neo4jClient
        .query(
            """
                      MATCH (u:User {id: $userId})-[:FRIEND]-(f:User)-[:WISHLISTED]->(g:Game {id: $gameId})
                      RETURN count(DISTINCT f) AS total
                      """)
        .bindAll(Map.of("userId", userId.toString(), "gameId", gameId))
        .fetch()
        .one()
        .map(record -> record.get("total"))
        .map(value -> ((Number) value).longValue())
        .orElse(0L);
  }

  @Override
  public long countFriendsPlayingGame(UUID userId, Long gameId) {

    return neo4jClient
        .query(
            """
                      MATCH (u:User {id: $userId})-[:FRIEND]-(f:User)-[:PLAYING]->(g:Game {id: $gameId})
                      RETURN count(DISTINCT f) AS total
                      """)
        .bindAll(Map.of("userId", userId.toString(), "gameId", gameId))
        .fetch()
        .one()
        .map(record -> record.get("total"))
        .map(value -> ((Number) value).longValue())
        .orElse(0L);
  }

  @Override
  public List<UserRef> findFriendsWithGameInWishlist(UUID userId, Long gameId) {
    return neo4jClient
        .query(
            """
                      MATCH (u:User {id: $userId})-[:FRIEND]-(f:User)-[:WISHLISTED]->(g:Game {id: $gameId})
                      RETURN DISTINCT f.id AS id, f.username AS username, f.avatar AS avatar
                      ORDER BY f.username
                      LIMIT $limit
                      """)
        .bindAll(
            Map.of(
                "userId", userId.toString(),
                "gameId", gameId,
                "limit", PREVIEW_LIMIT))
        .fetchAs(UserRef.class)
        .mappedBy(
            (typeSystem, record) ->
                UserRef.of(
                    UUID.fromString(record.get("id").asString()),
                    record.get("username").asString(""),
                    record.get("avatar").asString("")))
        .all()
        .stream()
        .toList();
  }

  @Override
  public List<UserRef> findFriendsPlayingGame(UUID userId, Long gameId) {
    return neo4jClient
        .query(
            """
                      MATCH (u:User {id: $userId})-[:FRIEND]-(f:User)-[:PLAYING]->(g:Game {id: $gameId})
                      RETURN DISTINCT f.id AS id, f.username AS username, f.avatar AS avatar
                      ORDER BY f.username
                      LIMIT $limit
                      """)
        .bindAll(
            Map.of(
                "userId", userId.toString(),
                "gameId", gameId,
                "limit", PREVIEW_LIMIT))
        .fetchAs(UserRef.class)
        .mappedBy(
            (typeSystem, record) ->
                UserRef.of(
                    UUID.fromString(record.get("id").asString()),
                    record.get("username").asString(""),
                    record.get("avatar").asString("")))
        .all()
        .stream()
        .toList();
  }
}
