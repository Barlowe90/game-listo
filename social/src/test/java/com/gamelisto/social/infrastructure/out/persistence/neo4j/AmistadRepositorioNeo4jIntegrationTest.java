package com.gamelisto.social.infrastructure.out.persistence.neo4j;

import com.gamelisto.social.dominio.UserRef;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.neo4j.test.autoconfigure.DataNeo4jTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DataNeo4jTest
@Testcontainers
@Import(AmistadRepositorioNeo4j.class)
@DisplayName("AmistadRepositorioNeo4j - Integracion con Neo4j")
class AmistadRepositorioNeo4jIntegrationTest {

  @Container
  static Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5").withoutAuthentication();

  @DynamicPropertySource
  static void neo4jProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.neo4j.uri", neo4j::getBoltUrl);
    registry.add("spring.neo4j.authentication.username", () -> "neo4j");
    registry.add("spring.neo4j.authentication.password", () -> "");
  }

  @Autowired private AmistadRepositorioNeo4j repositorio;

  @Test
  @DisplayName("debe agregar y recuperar una amistad")
  void debeAgregarYRecuperarAmistad() {
    repositorio.upsertUser(UserRef.of("u1", "alice", null));
    repositorio.upsertUser(UserRef.of("u2", "bob", null));
    repositorio.addFriendship("u1", "u2");
    List<UserRef> amigos = repositorio.getFriends("u1");
    assertEquals(1, amigos.size());
    assertEquals("u2", amigos.get(0).id());
  }

  @Test
  @DisplayName("addFriendship debe ser idempotente")
  void debeSerIdempotente() {
    repositorio.upsertUser(UserRef.of("u3", "charlie", null));
    repositorio.upsertUser(UserRef.of("u4", "diana", null));
    repositorio.addFriendship("u3", "u4");
    repositorio.addFriendship("u3", "u4");
    assertEquals(1, repositorio.getFriends("u3").size());
  }

  @Test
  @DisplayName("debe eliminar una amistad")
  void debeEliminarAmistad() {
    repositorio.upsertUser(UserRef.of("u5", "eve", null));
    repositorio.upsertUser(UserRef.of("u6", "frank", null));
    repositorio.addFriendship("u5", "u6");
    repositorio.removeFriendship("u5", "u6");
    assertTrue(repositorio.getFriends("u5").isEmpty());
  }
}
