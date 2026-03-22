package com.gamelisto.social.infrastructure.out.persistence.neo4j;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.gamelisto.social.dominio.UserRef;
import java.util.UUID;
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

@DataNeo4jTest
@Testcontainers
@Import({JuegoSocialRepositorioNeo4j.class, AmistadRepositorioNeo4j.class})
@DisplayName("JuegoSocialRepositorioNeo4j - Integracion con Neo4j")
class JuegoSocialRepositorioNeo4jIntegrationTest {

  @Container
  static Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5").withoutAuthentication();

  @DynamicPropertySource
  static void neo4jProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.neo4j.uri", neo4j::getBoltUrl);
    registry.add("spring.neo4j.authentication.username", () -> "neo4j");
    registry.add("spring.neo4j.authentication.password", () -> "");
  }

  @Autowired private JuegoSocialRepositorioNeo4j juegoSocialRepositorio;
  @Autowired private AmistadRepositorioNeo4j amistadRepositorio;

  @Test
  @DisplayName("debe sincronizar deseado y jugando para un amigo")
  void debeSincronizarDeseadoYJugandoParaUnAmigo() {
    UUID usuarioPrincipal = UUID.randomUUID();
    UUID amigo = UUID.randomUUID();
    Long gameId = 451324L;

    amistadRepositorio.upsertUser(UserRef.of(usuarioPrincipal, "alice", null));
    amistadRepositorio.upsertUser(UserRef.of(amigo, "bob", null));
    amistadRepositorio.addFriendship(usuarioPrincipal, amigo);

    juegoSocialRepositorio.syncGameState(amigo, gameId, "DESEADO");

    assertEquals(1L, juegoSocialRepositorio.countFriendsWithGameInWishlist(usuarioPrincipal, gameId));
    assertEquals(0L, juegoSocialRepositorio.countFriendsPlayingGame(usuarioPrincipal, gameId));
    assertEquals(1, juegoSocialRepositorio.findFriendsWithGameInWishlist(usuarioPrincipal, gameId).size());

    juegoSocialRepositorio.syncGameState(amigo, gameId, "JUGANDO");

    assertEquals(0L, juegoSocialRepositorio.countFriendsWithGameInWishlist(usuarioPrincipal, gameId));
    assertEquals(1L, juegoSocialRepositorio.countFriendsPlayingGame(usuarioPrincipal, gameId));
    assertEquals(1, juegoSocialRepositorio.findFriendsPlayingGame(usuarioPrincipal, gameId).size());
  }
}



