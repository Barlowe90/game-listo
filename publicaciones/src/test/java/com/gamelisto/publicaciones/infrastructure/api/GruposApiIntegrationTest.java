package com.gamelisto.publicaciones.infrastructure.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Tests de API para endpoints de grupos. */
@DisplayName("API - Grupos")
class GruposApiIntegrationTest extends AbstractApiIntegrationTest {

  private static final String PUBS = API_BASE;
  private static final String SOLICITUDES = API_BASE + "/{idPublicacion}/solicitud-union";
  private static final String SOLICITUD_BY_USER =
      API_BASE + "/{idPublicacion}/solicitud-union/{idUsuario}";
  private static final String PARTICIPANTES = API_BASE + "/{idPublicacion}/participantes";
  private static final String ABANDONAR = API_BASE + "/{idPublicacion}/abandonar-grupo";
  private static final String GRUPO_DETALLES = API_BASE + "/grupos/{idGrupo}";

  private UUID crearPublicacion(UUID autorId, long gameId) throws Exception {
    Map<String, Object> req =
        Map.of(
            "autorId",
            autorId.toString(),
            "gameId",
            gameId,
            "titulo",
            "Pub " + gameId,
            "idioma",
            "ESP",
            "experiencia",
            "NOVATO",
            "estiloJuego",
            "DISFRUTAR_DEL_JUEGO",
            "jugadoresMaximos",
            4,
            "estadoPublicacion",
            "PUBLICADA");

    MvcResult createResult =
        mockMvc
            .perform(
                post(PUBS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andReturn();

    return extractUuidFromResponse(
        createResult.getResponse().getContentAsString(),
        createResult.getResponse().getHeader("Location"));
  }

  private UUID aceptarSolicitudYObtenerGrupo(UUID publicacionId, UUID usuarioId) throws Exception {
    // crea solicitud
    mockMvc
        .perform(
            post(SOLICITUDES, publicacionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(Map.of("usuarioId", usuarioId.toString()))))
        .andExpect(status().isCreated());

    // acepta
    mockMvc
        .perform(
            patch(SOLICITUD_BY_USER, publicacionId, usuarioId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"ACEPTADA\"}"))
        .andExpect(status().isOk());

    // obtenemos el grupo desde repositorio (más robusto que depender del response)
    return grupoJuegoRepository
        .findByPublicacionId(publicacionId)
        .orElseThrow(() -> new IllegalStateException("No se creó el grupo al aceptar la solicitud"))
        .getId();
  }

  @Test
  @Disabled("Activa cuando implementes GET /v1/publicaciones/grupos/{idGrupo}")
  @DisplayName("Debe obtener detalles del grupo")
  void debeObtenerDetallesGrupo() throws Exception {
    UUID autorId = UUID.randomUUID();
    UUID publicacionId = crearPublicacion(autorId, 555);
    UUID miembro = UUID.randomUUID();
    UUID grupoId = aceptarSolicitudYObtenerGrupo(publicacionId, miembro);

    MvcResult result =
        mockMvc.perform(get(GRUPO_DETALLES, grupoId)).andExpect(status().isOk()).andReturn();

    // Flexible: valida que devuelva algún JSON con id/grupoId
    String body = result.getResponse().getContentAsString();
    JsonNode root = readJson(body);

    boolean ok =
        (root.has("id") && root.get("id").asText().equals(grupoId.toString()))
            || (root.has("grupoId") && root.get("grupoId").asText().equals(grupoId.toString()));

    assertThat(ok).isTrue();
  }

  @Test
  @Disabled("Activa cuando implementes POST /{idPublicacion}/abandonar-grupo + GET participantes")
  @DisplayName("Un usuario debe poder abandonar un grupo")
  void usuarioDebePoderAbandonarGrupo() throws Exception {
    UUID autorId = UUID.randomUUID();
    UUID publicacionId = crearPublicacion(autorId, 666);
    UUID miembro = UUID.randomUUID();
    aceptarSolicitudYObtenerGrupo(publicacionId, miembro);

    // Pre: participan autor + miembro
    MvcResult pre =
        mockMvc.perform(get(PARTICIPANTES, publicacionId)).andExpect(status().isOk()).andReturn();
    assertThat(pre.getResponse().getContentAsString()).contains(miembro.toString());

    // Abandonar: para TFG, body con usuarioId
    mockMvc
        .perform(
            post(ABANDONAR, publicacionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("usuarioId", miembro.toString()))))
        .andExpect(status().isOk());

    // Post: ya no aparece
    MvcResult post =
        mockMvc.perform(get(PARTICIPANTES, publicacionId)).andExpect(status().isOk()).andReturn();
    assertThat(post.getResponse().getContentAsString()).doesNotContain(miembro.toString());
  }
}
