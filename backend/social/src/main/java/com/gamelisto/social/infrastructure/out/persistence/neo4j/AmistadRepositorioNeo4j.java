package com.gamelisto.social.infrastructure.out.persistence.neo4j;

import com.gamelisto.social.dominio.AmistadRepositorio;
import com.gamelisto.social.dominio.GrafoUsuarioRepositorio;
import com.gamelisto.social.dominio.UserRef;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class AmistadRepositorioNeo4j implements AmistadRepositorio, GrafoUsuarioRepositorio {

  public static final String USER_ID = "userId";
  public static final String FRIEND_ID = "friendId";
  private final Neo4jClient neo4jClient;

  @Override
  @Transactional
  public void upsertUser(UserRef user) {
    Map<String, Object> params = new HashMap<>();
    params.put("id", user.id().toString());
    params.put("username", user.username() != null ? user.username() : "");
    params.put("avatar", user.avatar() != null ? user.avatar() : "");
    params.put("discordUserId", user.discordUserId());

    neo4jClient
        .query(
            """
            MERGE (u:User {id: $id})
            SET u.username = $username,
                u.avatar = $avatar,
                u.discordUserId = $discordUserId
            """)
        .bindAll(params)
        .run();
  }

  @Override
  @Transactional
  public void deleteUser(UUID userId) {
    neo4jClient
        .query("MATCH (u:User {id: $id}) DETACH DELETE u")
        .bind(userId.toString())
        .to("id")
        .run();
  }

  @Override
  @Transactional
  public void addFriendship(UUID userId, UUID friendId) {
    neo4jClient
        .query(
            "MERGE (a:User {id: $userId}) "
                + "MERGE (b:User {id: $friendId}) "
                + "MERGE (a)-[:FRIEND]-(b)")
        .bindAll(
            Map.of(
                USER_ID, userId.toString(),
                FRIEND_ID, friendId.toString()))
        .run();
  }

  @Override
  @Transactional
  public void removeFriendship(UUID userId, UUID friendId) {
    neo4jClient
        .query("MATCH (a:User {id: $userId})-[r:FRIEND]-(b:User {id: $friendId}) DELETE r")
        .bindAll(
            Map.of(
                USER_ID, userId.toString(),
                FRIEND_ID, friendId.toString()))
        .run();
  }

  @Override
  public List<UserRef> getFriends(UUID userId) {
    return neo4jClient
        .query(
            "MATCH (a:User {id: $userId})-[:FRIEND]-(b:User) RETURN b.id AS id, b.username AS username, b.avatar AS avatar")
        .bind(userId.toString())
        .to(USER_ID)
        .fetchAs(UserRef.class)
        .mappedBy(
            (typeSystem, res) ->
                new UserRef(
                    UUID.fromString(res.get("id").asString()),
                    res.get("username").asString(""),
                    res.get("avatar").asString("")))
        .all()
        .stream()
        .toList();
  }
}
