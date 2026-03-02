package com.gamelisto.publicaciones.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gamelisto.publicaciones.domain.vo.PeticionId;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
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

    PeticionUnion p =
        PeticionUnion.create(
            PublicacionId.of(pubId), UsuarioId.of(userId), EstadoSolicitud.SOLICITADA);

    assertThat(p.getId()).isNotNull();
    assertThat(p.getPublicacionId().value()).isEqualTo(pubId);
    assertThat(p.getUsuarioId().value()).isEqualTo(userId);
    assertThat(p.getEstadoSolicitud()).isEqualTo(EstadoSolicitud.SOLICITADA);
  }

  @Test
  @DisplayName("reconstitute() debe respetar id")
  void reconstitute_debeRespetarId() {
    UUID id = UUID.randomUUID();
    UUID pubId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    PeticionUnion p =
        PeticionUnion.reconstitute(
            PeticionId.of(id),
            PublicacionId.of(pubId),
            UsuarioId.of(userId),
            EstadoSolicitud.ACEPTADA);

    assertThat(p.getId().value()).isEqualTo(id);
    assertThat(p.getEstadoSolicitud()).isEqualTo(EstadoSolicitud.ACEPTADA);
  }
}
