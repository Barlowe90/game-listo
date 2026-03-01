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

/**
 * Tests de API para las solicitudes de unión.
 *
 * <p>Flow típico: crear publicación -> crear solicitud -> aceptar/rechazar -> comprobar
 * participantes.
 */
@DisplayName("API - Solicitudes de unión")
class SolicitudesUnionApiIntegrationTest extends AbstractApiIntegrationTest {

  private static final String PUBS = API_BASE;
  private static final String SOLICITUDES = API_BASE + "/{idPublicacion}/solicitud-union";
  private static final String SOLICITUD_BY_USER =
      API_BASE + "/{idPublicacion}/solicitud-union/{idUsuario}";
  private static final String PARTICIPANTES = API_BASE + "/{idPublicacion}/participantes";

  private UUID crearPublicacion(UUID autorId, long gameId, String titulo) throws Exception {
    Map<String, Object> req =
        Map.of(
            "autorId",
            autorId.toString(),
            "gameId",
            gameId,
            "titulo",
            titulo,
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

  @Test
  @Disabled("Activa cuando implementes GET/POST /{idPublicacion}/solicitud-union")
  @DisplayName("Debe crear una solicitud y listarla por publicación")
  void debeCrearSolicitudYListarla() throws Exception {
    UUID autorId = UUID.randomUUID();
    UUID publicacionId = crearPublicacion(autorId, 111, "Publicación para solicitudes");

    UUID usuarioId = UUID.randomUUID();
    Map<String, Object> createReq = Map.of("usuarioId", usuarioId.toString());

    mockMvc
        .perform(
            post(SOLICITUDES, publicacionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createReq)))
        .andExpect(status().isCreated());

    MvcResult list =
        mockMvc.perform(get(SOLICITUDES, publicacionId)).andExpect(status().isOk()).andReturn();

    JsonNode root = readJson(list.getResponse().getContentAsString());
    JsonNode items = readItemsArrayIfWrapped(root).orElseThrow();
    assertThat(items).hasSize(1);
  }

  @Test
  @Disabled(
      "Activa cuando implementes PATCH /{idPublicacion}/solicitud-union/{idUsuario} + GET participantes")
  @DisplayName("Aceptar solicitud debe añadir participante al grupo")
  void aceptarSolicitudDebeAnadirParticipante() throws Exception {
    UUID autorId = UUID.randomUUID();
    UUID publicacionId = crearPublicacion(autorId, 222, "Publicación accept");

    UUID usuarioId = UUID.randomUUID();
    mockMvc
        .perform(
            post(SOLICITUDES, publicacionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(Map.of("usuarioId", usuarioId.toString()))))
        .andExpect(status().isCreated());

    // Aceptar: body con el estado (KISS)
    mockMvc
        .perform(
            patch(SOLICITUD_BY_USER, publicacionId, usuarioId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"ACEPTADA\"}"))
        .andExpect(status().isOk());

    // Verificar participantes: por simplicidad esperamos que incluya autor y el usuario aceptado
    MvcResult participantes =
        mockMvc.perform(get(PARTICIPANTES, publicacionId)).andExpect(status().isOk()).andReturn();

    JsonNode root = readJson(participantes.getResponse().getContentAsString());
    JsonNode items = readItemsArrayIfWrapped(root).orElseThrow();
    assertThat(items.toString()).contains(autorId.toString());
    assertThat(items.toString()).contains(usuarioId.toString());
  }

  @Test
  @Disabled("Activa cuando implementes PATCH reject")
  @DisplayName("Rechazar solicitud no debe añadir participante")
  void rechazarSolicitudNoDebeAnadirParticipante() throws Exception {
    UUID autorId = UUID.randomUUID();
    UUID publicacionId = crearPublicacion(autorId, 333, "Publicación reject");

    UUID usuarioId = UUID.randomUUID();
    mockMvc
        .perform(
            post(SOLICITUDES, publicacionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(Map.of("usuarioId", usuarioId.toString()))))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            patch(SOLICITUD_BY_USER, publicacionId, usuarioId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"estado\":\"RECHAZADA\"}"))
        .andExpect(status().isOk());

    MvcResult participantes =
        mockMvc.perform(get(PARTICIPANTES, publicacionId)).andExpect(status().isOk()).andReturn();

    JsonNode root = readJson(participantes.getResponse().getContentAsString());
    JsonNode items = readItemsArrayIfWrapped(root).orElseThrow();
    assertThat(items.toString()).contains(autorId.toString());
    assertThat(items.toString()).doesNotContain(usuarioId.toString());
  }

  @Test
  @Disabled("Opcional: Activa si decides devolver 409 en duplicados por (publicacionId, usuarioId)")
  @DisplayName("Crear solicitud duplicada debe dar 409 Conflict")
  void crearSolicitudDuplicadaDebeDarConflict() throws Exception {
    UUID autorId = UUID.randomUUID();
    UUID publicacionId = crearPublicacion(autorId, 444, "Publicación dup");

    UUID usuarioId = UUID.randomUUID();
    Map<String, Object> body = Map.of("usuarioId", usuarioId.toString());

    mockMvc
        .perform(
            post(SOLICITUDES, publicacionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isCreated());

    // Repetida
    mockMvc
        .perform(
            post(SOLICITUDES, publicacionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
        .andExpect(status().isConflict());
  }
}
