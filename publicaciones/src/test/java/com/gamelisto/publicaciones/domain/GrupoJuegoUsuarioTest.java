package com.gamelisto.publicaciones.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("GrupoJuegoUsuario - Unit tests")
class GrupoJuegoUsuarioTest {

  @Test
  @DisplayName("create() debe generar id")
  void create_debeGenerarId() {
    UUID grupoId = UUID.randomUUID();
    UUID usuarioId = UUID.randomUUID();

    GrupoJuegoUsuario rel = GrupoJuegoUsuario.create(grupoId, usuarioId);

    assertThat(rel.getId()).isNotNull();
    assertThat(rel.getGrupoId()).isEqualTo(grupoId);
    assertThat(rel.getUsuarioId()).isEqualTo(usuarioId);
  }

  @Test
  @DisplayName("reconstitute() debe respetar id")
  void reconstitute_debeRespetarId() {
    UUID id = UUID.randomUUID();
    UUID grupoId = UUID.randomUUID();
    UUID usuarioId = UUID.randomUUID();

    GrupoJuegoUsuario rel = GrupoJuegoUsuario.reconstitute(id, grupoId, usuarioId);

    assertThat(rel.getId()).isEqualTo(id);
  }
}
