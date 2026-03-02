package com.gamelisto.publicaciones.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Publicacion - Unit tests")
class PublicacionTest {

  @Test
  @DisplayName("create() debe generar un id y conservar el resto de campos")
  void create_debeGenerarId() {
    UUID autorId = UUID.randomUUID();

    Publicacion p =
        Publicacion.create(
            autorId,
            10001L,
            "Busco compas para jugar",
            Idioma.ESP,
            Experiencia.NOVATO,
            EstiloJuego.DISFRUTAR_DEL_JUEGO,
            4);

    assertThat(p.getId()).isNotNull();
    assertThat(p.getAutorId()).isEqualTo(autorId);
    assertThat(p.getGameId()).isEqualTo(10001L);
    assertThat(p.getJugadoresMaximos()).isEqualTo(4);
  }

  @Test
  @DisplayName("reconstitute() debe respetar el id proporcionado")
  void reconstitute_debeRespetarId() {
    UUID id = UUID.randomUUID();
    UUID autorId = UUID.randomUUID();

    Publicacion p =
        Publicacion.reconstitute(
            id, autorId, 10002L, "Titulo", Idioma.ENG, Experiencia.PRO, EstiloJuego.LOGROS, 8);

    assertThat(p.getId().value()).isEqualTo(id);
    assertThat(p.getAutorId()).isEqualTo(autorId);
  }
}
