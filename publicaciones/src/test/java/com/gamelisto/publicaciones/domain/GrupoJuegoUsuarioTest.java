package com.gamelisto.publicaciones.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gamelisto.publicaciones.domain.vo.GrupoId;
import com.gamelisto.publicaciones.domain.vo.GrupoJuegoUsuarioId;
import com.gamelisto.publicaciones.domain.vo.UsuarioId;
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

    GrupoJuegoUsuario rel = GrupoJuegoUsuario.create(GrupoId.of(grupoId), UsuarioId.of(usuarioId));

    assertThat(rel.getId()).isNotNull();
    assertThat(rel.getGrupoId().value()).isEqualTo(grupoId);
    assertThat(rel.getUsuarioId().value()).isEqualTo(usuarioId);
  }

  @Test
  @DisplayName("reconstitute() debe respetar id")
  void reconstitute_debeRespetarId() {
    UUID id = UUID.randomUUID();
    UUID grupoId = UUID.randomUUID();
    UUID usuarioId = UUID.randomUUID();

    GrupoJuegoUsuario rel =
        GrupoJuegoUsuario.reconstitute(
            GrupoJuegoUsuarioId.of(id), GrupoId.of(grupoId), UsuarioId.of(usuarioId));

    assertThat(rel.getId().value()).isEqualTo(id);
  }
}
