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
 * Tests de integración de API para Publicaciones.
 *
 * <p>Están diseñados como "checklist": ve quitando @Disabled conforme implementes endpoints.
 */
@DisplayName("API - Publicaciones")
class PublicacionesApiIntegrationTest extends AbstractApiIntegrationTest {

  private static final String PUBS = API_BASE; // Recomendado: GET/POST /v1/publicaciones
  private static final String PUB_BY_ID = API_BASE + "/{idPublicacion}";
  private static final String PUBS_BY_USER = API_BASE + "/usuario/{idUsuario}";
  private static final String PUBS_BY_GAME = API_BASE + "/game/{idGame}";

  @Test
  @Disabled("Activa cuando implementes POST /v1/publicaciones y GET /v1/publicaciones/{id}")
  @DisplayName("Debe crear y recuperar una publicación por id")
  void debeCrearYRecuperarPublicacion() throws Exception {
    UUID autorId = UUID.randomUUID();

    Map<String, Object> req =
        Map.of(
            "autorId", autorId.toString(),
            "gameId", 10001,
            "titulo", "Busco grupo chill",
            "idioma", "ESP",
            "experiencia", "NOVATO",
            "estiloJuego", "DISFRUTAR_DEL_JUEGO",
            "jugadoresMaximos", 4,
            "estadoPublicacion", "PUBLICADA");

    MvcResult createResult =
        mockMvc
            .perform(
                post(PUBS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andReturn();

    UUID publicacionId =
        extractUuidFromResponse(
            createResult.getResponse().getContentAsString(),
            createResult.getResponse().getHeader("Location"));

    mockMvc
        .perform(get(PUB_BY_ID, publicacionId))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(publicacionId.toString()))
        .andExpect(jsonPath("$.autorId").value(autorId.toString()))
        .andExpect(jsonPath("$.gameId").value(10001))
        .andExpect(jsonPath("$.titulo").value("Busco grupo chill"));
  }

  @Test
  @Disabled("Activa cuando implementes GET /v1/publicaciones")
  @DisplayName("Debe listar publicaciones")
  void debeListarPublicaciones() throws Exception {
    MvcResult result = mockMvc.perform(get(PUBS)).andExpect(status().isOk()).andReturn();
    assertJsonArrayOrItemsArray(result.getResponse().getContentAsString());
  }

  @Test
  @Disabled("Activa cuando implementes PUT /v1/publicaciones/{id}")
  @DisplayName("Debe actualizar una publicación")
  void debeActualizarPublicacion() throws Exception {
    // Pre-condición: crea una publicación primero
    UUID autorId = UUID.randomUUID();

    Map<String, Object> createReq =
        Map.of(
            "autorId", autorId.toString(),
            "gameId", 222L,
            "titulo", "Titulo original",
            "idioma", "ESP",
            "experiencia", "NOOB",
            "estiloJuego", "LOGROS",
            "jugadoresMaximos", 2,
            "estadoPublicacion", "PUBLICADA");

    MvcResult createResult =
        mockMvc
            .perform(
                post(PUBS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReq)))
            .andExpect(status().isCreated())
            .andReturn();

    UUID publicacionId =
        extractUuidFromResponse(
            createResult.getResponse().getContentAsString(),
            createResult.getResponse().getHeader("Location"));

    Map<String, Object> updateReq =
        Map.of(
            "titulo", "Titulo actualizado",
            "jugadoresMaximos", 5,
            "estadoPublicacion", "COMPLETADA");

    mockMvc
        .perform(
            put(PUB_BY_ID, publicacionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateReq)))
        .andExpect(status().isOk());

    mockMvc
        .perform(get(PUB_BY_ID, publicacionId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.titulo").value("Titulo actualizado"))
        .andExpect(jsonPath("$.jugadoresMaximos").value(5))
        .andExpect(jsonPath("$.estadoPublicacion").value("COMPLETADA"));
  }

  @Test
  @Disabled("Activa cuando implementes DELETE /v1/publicaciones/{id}")
  @DisplayName("Debe eliminar una publicación")
  void debeEliminarPublicacion() throws Exception {
    UUID autorId = UUID.randomUUID();

    Map<String, Object> createReq =
        Map.of(
            "autorId", autorId.toString(),
            "gameId", 333,
            "titulo", "Para borrar",
            "idioma", "ENG",
            "experiencia", "MEDIO",
            "estiloJuego", "DISFRUTAR_DEL_JUEGO",
            "jugadoresMaximos", 4,
            "estadoPublicacion", "PUBLICADA");

    MvcResult createResult =
        mockMvc
            .perform(
                post(PUBS)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReq)))
            .andExpect(status().isCreated())
            .andReturn();

    UUID publicacionId =
        extractUuidFromResponse(
            createResult.getResponse().getContentAsString(),
            createResult.getResponse().getHeader("Location"));

    mockMvc.perform(delete(PUB_BY_ID, publicacionId)).andExpect(status().isNoContent());

    // Puedes decidir si devuelves 404 o 410; para TFG 404 está bien.
    mockMvc.perform(get(PUB_BY_ID, publicacionId)).andExpect(status().isNotFound());
  }

  @Test
  @Disabled("Activa cuando implementes GET /v1/publicaciones/usuario/{idUsuario}")
  @DisplayName("Debe listar publicaciones creadas por un usuario")
  void debeListarPorUsuario() throws Exception {
    UUID autorId = UUID.randomUUID();
    UUID otroAutor = UUID.randomUUID();

    // Crea dos publicaciones del mismo autor y una de otro autor
    for (int i = 0; i < 2; i++) {
      Map<String, Object> req =
          Map.of(
              "autorId", autorId.toString(),
              "gameId", 400 + i,
              "titulo", "P" + i,
              "idioma", "ESP",
              "experiencia", "NOVATO",
              "estiloJuego", "LOGROS",
              "jugadoresMaximos", 4,
              "estadoPublicacion", "PUBLICADA");

      mockMvc
          .perform(
              post(PUBS)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(req)))
          .andExpect(status().isCreated());
    }

    Map<String, Object> otherReq =
        Map.of(
            "autorId", otroAutor.toString(),
            "gameId", 999,
            "titulo", "Otra",
            "idioma", "ENG",
            "experiencia", "PRO",
            "estiloJuego", "DISFRUTAR_DEL_JUEGO",
            "jugadoresMaximos", 3,
            "estadoPublicacion", "PUBLICADA");

    mockMvc
        .perform(
            post(PUBS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otherReq)))
        .andExpect(status().isCreated());

    MvcResult result =
        mockMvc.perform(get(PUBS_BY_USER, autorId)).andExpect(status().isOk()).andReturn();

    JsonNode root = readJson(result.getResponse().getContentAsString());
    JsonNode items = readItemsArrayIfWrapped(root).orElseThrow();
    assertThat(items).hasSize(2);
  }

  @Test
  @Disabled("Activa cuando implementes GET /v1/publicaciones/game/{idGame}")
  @DisplayName("Debe listar publicaciones por gameId")
  void debeListarPorGame() throws Exception {
    UUID autorId = UUID.randomUUID();

    // Dos publicaciones del mismo juego
    for (int i = 0; i < 2; i++) {
      Map<String, Object> req =
          Map.of(
              "autorId", autorId.toString(),
              "gameId", 12345,
              "titulo", "SameGame-" + i,
              "idioma", "ESP",
              "experiencia", "NOVATO",
              "estiloJuego", "LOGROS",
              "jugadoresMaximos", 4,
              "estadoPublicacion", "PUBLICADA");

      mockMvc
          .perform(
              post(PUBS)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(req)))
          .andExpect(status().isCreated());
    }

    // Otra publicación de otro juego
    Map<String, Object> otherGame =
        Map.of(
            "autorId", autorId.toString(),
            "gameId", 777,
            "titulo", "OtherGame",
            "idioma", "ESP",
            "experiencia", "NOVATO",
            "estiloJuego", "LOGROS",
            "jugadoresMaximos", 4,
            "estadoPublicacion", "PUBLICADA");

    mockMvc
        .perform(
            post(PUBS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(otherGame)))
        .andExpect(status().isCreated());

    MvcResult result =
        mockMvc.perform(get(PUBS_BY_GAME, 12345)).andExpect(status().isOk()).andReturn();

    JsonNode root = readJson(result.getResponse().getContentAsString());
    JsonNode items = readItemsArrayIfWrapped(root).orElseThrow();
    assertThat(items).hasSize(2);
  }
}
