package com.gamelisto.publicaciones.infrastructure.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gamelisto.publicaciones.domain.GrupoJuegoUsuario;
import com.gamelisto.publicaciones.domain.GrupoJuegoUsuarioRepositorio;
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
@DisplayName("GrupoJuegoUsuarioRepositorioMongo - Tests de Integración")
class GrupoJuegoUsuarioRepositorioMongoIntegrationTest {

  @Autowired private GrupoJuegoUsuarioRepositorio repo;
  @Autowired private MongoTemplate mongoTemplate;

  @BeforeEach
  void clean() {
    mongoTemplate.dropCollection("grupo_juego_usuario");
  }

  @Test
  @DisplayName("Debe guardar, comprobar exists y borrar")
  void debeGuardarExistsYBorrar() {
    UUID grupoId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    repo.save(GrupoJuegoUsuario.create(grupoId, userId));

    assertThat(repo.existsByGrupoIdAndUsuarioId(grupoId, userId)).isTrue();

    repo.deleteByGrupoIdAndUsuarioId(grupoId, userId);

    assertThat(repo.existsByGrupoIdAndUsuarioId(grupoId, userId)).isFalse();
  }

  @Test
  @DisplayName("Debe aplicar unique compuesto (grupoId, usuarioId)")
  void debeAplicarUniqueCompuesto() {
    UUID grupoId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    repo.save(GrupoJuegoUsuario.create(grupoId, userId));

    assertThatThrownBy(() -> repo.save(GrupoJuegoUsuario.create(grupoId, userId)))
        .isInstanceOf(DuplicateKeyException.class);
  }
}
