package com.gamelisto.publicaciones.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("PeticionUnion - Unit tests")
class PeticionUnionTest {

  @Test
  @DisplayName("create() debe generar id y asignar campos")
  void create_debeGenerarId() {
    UUID pubId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    PeticionUnion p = PeticionUnion.create(pubId, userId, EstadoSolicitud.SOLICITADA);

    assertThat(p.getId()).isNotNull();
    assertThat(p.getPublicacionId()).isEqualTo(pubId);
    assertThat(p.getUsuarioId()).isEqualTo(userId);
    assertThat(p.getEstadoSolicitud()).isEqualTo(EstadoSolicitud.SOLICITADA);
  }

  @Test
  @DisplayName("reconstitute() debe respetar id")
  void reconstitute_debeRespetarId() {
    UUID id = UUID.randomUUID();
    UUID pubId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    PeticionUnion p = PeticionUnion.reconstitute(id, pubId, userId, EstadoSolicitud.ACEPTADA);

    assertThat(p.getId()).isEqualTo(id);
    assertThat(p.getEstadoSolicitud()).isEqualTo(EstadoSolicitud.ACEPTADA);
  }
}
