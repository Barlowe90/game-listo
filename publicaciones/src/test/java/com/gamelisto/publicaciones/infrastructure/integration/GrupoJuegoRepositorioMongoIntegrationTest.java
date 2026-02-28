package com.gamelisto.publicaciones.infrastructure.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gamelisto.publicaciones.domain.GrupoJuego;
import com.gamelisto.publicaciones.domain.GrupoJuegoRepositorio;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@DisplayName("GrupoJuegoRepositorioMongo - Tests de Integración")
class GrupoJuegoRepositorioMongoIntegrationTest {

  @Autowired private GrupoJuegoRepositorio repo;
  @Autowired private MongoTemplate mongoTemplate;

  @BeforeEach
  void clean() {
    mongoTemplate.dropCollection("grupo_juego");
  }

  @Test
  @DisplayName("Debe guardar y recuperar por publicacionId")
  void debeGuardarYRecuperarPorPublicacionId() {
    UUID publicacionId = UUID.randomUUID();

    GrupoJuego g = repo.save(GrupoJuego.create(publicacionId));

    Optional<GrupoJuego> r = repo.findByPublicacionId(publicacionId);
    assertThat(r).isPresent();
    assertThat(r.get().getId()).isEqualTo(g.getId());
  }

  @Test
  @DisplayName("Debe aplicar unique en publicacionId (1 grupo por publicación)")
  void debeAplicarUniquePublicacionId() {
    UUID publicacionId = UUID.randomUUID();

    repo.save(GrupoJuego.create(publicacionId));

    assertThatThrownBy(() -> repo.save(GrupoJuego.create(publicacionId)))
        .isInstanceOf(DuplicateKeyException.class);
  }
}
