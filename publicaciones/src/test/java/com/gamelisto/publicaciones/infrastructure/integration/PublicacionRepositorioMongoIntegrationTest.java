package com.gamelisto.publicaciones.infrastructure.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.gamelisto.publicaciones.domain.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@DisplayName("PublicacionRepositorioMongo - Tests de Integración")
class PublicacionRepositorioMongoIntegrationTest {

  @Autowired private PublicacionRepositorio publicacionRepositorio;
  @Autowired private MongoTemplate mongoTemplate;

  @BeforeEach
  void clean() {
    // Limpieza KISS para evitar interferencias entre tests
    mongoTemplate.dropCollection("publicacion");
  }

  @Test
  @DisplayName("Debe guardar y recuperar una Publicacion por id")
  void debeGuardarYRecuperarPorId() {
    UUID autorId = UUID.randomUUID();

    Publicacion p =
        Publicacion.create(
            autorId,
            12345L,
            "Titulo",
            Idioma.ESP,
            Experiencia.MEDIO,
            EstiloJuego.LOGROS,
            5,
            EstadoPublicacion.PUBLICADA);

    publicacionRepositorio.save(p);

    Optional<Publicacion> r = publicacionRepositorio.findById(p.getId());
    assertThat(r).isPresent();
    assertThat(r.get().getAutorId()).isEqualTo(autorId);
    assertThat(r.get().getGameId()).isEqualTo(12345L);
  }

  @Test
  @DisplayName("Debe filtrar por autorId y por gameId")
  void debeFiltrarPorAutorYGame() {
    UUID autor1 = UUID.randomUUID();
    UUID autor2 = UUID.randomUUID();

    publicacionRepositorio.save(
        Publicacion.create(
            autor1,
            100L,
            "P1",
            Idioma.ESP,
            Experiencia.NOVATO,
            EstiloJuego.DISFRUTAR_DEL_JUEGO,
            4,
            EstadoPublicacion.PUBLICADA));

    publicacionRepositorio.save(
        Publicacion.create(
            autor1,
            200L,
            "P2",
            Idioma.ENG,
            Experiencia.PRO,
            EstiloJuego.LOGROS,
            2,
            EstadoPublicacion.PUBLICADA));

    publicacionRepositorio.save(
        Publicacion.create(
            autor2,
            100L,
            "P3",
            Idioma.ESP,
            Experiencia.NOOB,
            EstiloJuego.DISFRUTAR_DEL_JUEGO,
            3,
            EstadoPublicacion.PUBLICADA));

    List<Publicacion> autor1Pubs = publicacionRepositorio.findByAutorId(autor1);
    assertThat(autor1Pubs).hasSize(2);

    List<Publicacion> game100 = publicacionRepositorio.findByGameId(100L);
    assertThat(game100).hasSize(2);
  }
}
