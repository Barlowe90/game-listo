package com.gamelisto.publicaciones.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.gamelisto.publicaciones.domain.DiaSemana;
import com.gamelisto.publicaciones.domain.FranjaHoraria;
import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.vo.DisponibilidadSemanal;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@DisplayName("PublicacionMapper - Unit tests")
class PublicacionMapperTest {

  private final PublicacionMapper mapper = new PublicacionMapper();

  @Test
  @DisplayName("toDocument y toDomain deben mapear disponibilidad correctamente")
  void mapearDisponibilidadIdaYVuelta() {
    DisponibilidadSemanal ds =
        DisponibilidadSemanal.of(
            Map.of(
                DiaSemana.LUNES,
                Set.of(FranjaHoraria.DIA),
                DiaSemana.VIERNES,
                Set.of(FranjaHoraria.NOCHE)));

    Publicacion p =
        Publicacion.create(
            UUID.randomUUID(),
            1L,
            "T",
            com.gamelisto.publicaciones.domain.Idioma.ESP,
            com.gamelisto.publicaciones.domain.Experiencia.NOVATO,
            com.gamelisto.publicaciones.domain.EstiloJuego.LOGROS,
            4,
            ds);

    PublicacionDocument doc = mapper.toDocument(p);

    assertThat(doc.getDisponibilidad()).isNotNull();
    assertThat(doc.getDisponibilidad().get("LUNES")).contains("DIA");
    assertThat(doc.getDisponibilidad().get("VIERNES")).contains("NOCHE");

    Publicacion restored = mapper.toDomain(doc);

    assertThat(restored.getDisponibilidadSemanal()).isNotNull();
    assertThat(
            restored.getDisponibilidadSemanal().estaDisponible(DiaSemana.LUNES, FranjaHoraria.DIA))
        .isTrue();
    assertThat(
            restored
                .getDisponibilidadSemanal()
                .estaDisponible(DiaSemana.VIERNES, FranjaHoraria.NOCHE))
        .isTrue();
  }
}
