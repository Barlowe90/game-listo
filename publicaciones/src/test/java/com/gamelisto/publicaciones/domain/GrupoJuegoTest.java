package com.gamelisto.publicaciones.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gamelisto.publicaciones.domain.vo.GrupoId;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GrupoJuego - Unit tests")
class GrupoJuegoTest {

  @Test
  @DisplayName("create() debe generar id y fechaCreacion")
  void create_debeGenerarIdYFecha() {
    UUID publicacionId = UUID.randomUUID();

    GrupoJuego g = GrupoJuego.create(PublicacionId.of(publicacionId));

    assertThat(g.getId()).isNotNull();
    assertThat(g.getPublicacionId().value()).isEqualTo(publicacionId);
    assertThat(g.getFechaCreacion()).isNotNull();
  }

  @Test
  @DisplayName("reconstitute() debe respetar id")
  void reconstitute_debeRespetarId() {
    UUID id = UUID.randomUUID();
    UUID publicacionId = UUID.randomUUID();
    Instant fecha = Instant.parse("2026-01-01T10:00:00Z");

    GrupoJuego g = GrupoJuego.reconstitute(GrupoId.of(id), PublicacionId.of(publicacionId), fecha);

    assertThat(g.getId().value()).isEqualTo(id);
    assertThat(g.getFechaCreacion()).isEqualTo(fecha);
  }
}
