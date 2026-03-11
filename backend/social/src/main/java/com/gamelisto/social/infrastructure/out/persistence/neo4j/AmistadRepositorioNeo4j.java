package com.gamelisto.social.infrastructure.out.persistence.neo4j;

import com.gamelisto.social.dominio.AmistadRepositorio;
import com.gamelisto.social.dominio.GrafoUsuarioRepositorio;
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
public class AmistadRepositorioNeo4j implements AmistadRepositorio, GrafoUsuarioRepositorio {

  private final Neo4jClient neo4jClient;

  @Override
  @Transactional
  public void upsertUser(UserRef user) {
    neo4jClient
        .query("MERGE (u:User {id: $id}) SET u.username = $username, u.avatar = $avatar")
        .bindAll(
            Map.of(
                "id", user.id().toString(),
                "username", user.username() != null ? user.username() : "",
                "avatar", user.avatar() != null ? user.avatar() : ""))
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
                "userId", userId.toString(),
                "friendId", friendId.toString()))
        .run();
  }

  @Override
  @Transactional
  public void removeFriendship(UUID userId, UUID friendId) {
    neo4jClient
        .query("MATCH (a:User {id: $userId})-[r:FRIEND]-(b:User {id: $friendId}) DELETE r")
        .bindAll(
            Map.of(
                "userId", userId.toString(),
                "friendId", friendId.toString()))
        .run();
  }

  @Override
  public List<UserRef> getFriends(UUID userId) {
    return neo4jClient
        .query(
            "MATCH (a:User {id: $userId})-[:FRIEND]-(b:User) RETURN b.id AS id, b.username AS username, b.avatar AS avatar")
        .bind(userId.toString())
        .to("userId")
        .fetchAs(UserRef.class)
        .mappedBy(
            (typeSystem, record) ->
                new UserRef(
                    UUID.fromString(record.get("id").asString()),
                    record.get("username").asString(""),
                    record.get("avatar").asString("")))
        .all()
        .stream()
        .toList();
  }
}
