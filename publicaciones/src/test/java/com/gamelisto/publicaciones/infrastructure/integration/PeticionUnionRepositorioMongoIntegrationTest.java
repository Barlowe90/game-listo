package com.gamelisto.publicaciones.infrastructure.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gamelisto.publicaciones.domain.EstadoPeticion;
import com.gamelisto.publicaciones.domain.PeticionUnion;
import com.gamelisto.publicaciones.domain.PeticionUnionRepositorio;
import java.util.List;
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
@DisplayName("PeticionUnionRepositorioMongo - Tests de Integración")
class PeticionUnionRepositorioMongoIntegrationTest {

  @Autowired private PeticionUnionRepositorio peticionUnionRepositorio;
  @Autowired private MongoTemplate mongoTemplate;

  @BeforeEach
  void clean() {
    mongoTemplate.dropCollection("peticion_union");
  }

  @Test
  @DisplayName("Debe guardar y listar peticiones por publicacionId")
  void debeGuardarYListarPorPublicacionId() {
    UUID pubId = UUID.randomUUID();

    PeticionUnion p1 = PeticionUnion.create(pubId, UUID.randomUUID(), EstadoPeticion.SOLICITADA);
    PeticionUnion p2 = PeticionUnion.create(pubId, UUID.randomUUID(), EstadoPeticion.ACEPTADA);

    peticionUnionRepositorio.save(p1);
    peticionUnionRepositorio.save(p2);

    List<PeticionUnion> list = peticionUnionRepositorio.findByPublicacionId(pubId);
    assertThat(list).hasSize(2);
  }

  @Test
  @DisplayName("Debe aplicar unique compuesto (publicacionId, usuarioId)")
  void debeAplicarUniqueCompuesto() {
    UUID pubId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    peticionUnionRepositorio.save(PeticionUnion.create(pubId, userId, EstadoPeticion.SOLICITADA));

    assertThatThrownBy(
            () ->
                peticionUnionRepositorio.save(
                    PeticionUnion.create(pubId, userId, EstadoPeticion.SOLICITADA)))
        .isInstanceOf(DuplicateKeyException.class);
  }
}
