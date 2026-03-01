package com.gamelisto.publicaciones.infrastructure.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.publicaciones.AbstractIntegrationTest;
import com.gamelisto.publicaciones.infraestructure.out.persistence.GrupoJuegoRepository;
import com.gamelisto.publicaciones.infraestructure.out.persistence.GrupoJuegoUsuarioRepository;
import com.gamelisto.publicaciones.infraestructure.out.persistence.PeticionUnionRepository;
import com.gamelisto.publicaciones.infraestructure.out.persistence.PublicacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base para tests de API (MockMvc) que necesitan Mongo.
 *
 * <p>Incluye limpieza de colecciones y helpers para extraer IDs de respuestas.
 */
public abstract class AbstractApiIntegrationTest extends AbstractIntegrationTest {

  protected static final String API_BASE = "/v1/publicaciones";

  @Autowired protected MockMvc mockMvc;
  @Autowired protected ObjectMapper objectMapper;

  @Autowired protected PublicacionRepository publicacionRepository;
  @Autowired protected PeticionUnionRepository peticionUnionRepository;
  @Autowired protected GrupoJuegoRepository grupoJuegoRepository;
  @Autowired protected GrupoJuegoUsuarioRepository grupoJuegoUsuarioRepository;

  @BeforeEach
  void cleanDb() {
    // Orden importa por dependencias lógicas, pero en Mongo no hay FK: solo por claridad.
    grupoJuegoUsuarioRepository.deleteAll();
    peticionUnionRepository.deleteAll();
    grupoJuegoRepository.deleteAll();
    publicacionRepository.deleteAll();
  }

  /**
   * Intenta extraer un UUID de:
   *  - body JSON {"id": "..."} o {"publicacionId": "..."} o {"grupoId": "..."}
   *  - header Location: .../{uuid}
   */
  protected UUID extractUuidFromResponse(String responseBody, String locationHeader) {
    // 1) Location
    if (locationHeader != null && !locationHeader.isBlank()) {
      int lastSlash = locationHeader.lastIndexOf('/');
      if (lastSlash >= 0 && lastSlash + 1 < locationHeader.length()) {
        String maybe = locationHeader.substring(lastSlash + 1);
        try { return UUID.fromString(maybe); } catch (Exception ignored) {}
      }
    }

    // 2) JSON body
    if (responseBody != null && !responseBody.isBlank()) {
      try {
        JsonNode root = objectMapper.readTree(responseBody);
        for (String key : new String[]{"id", "publicacionId", "grupoId"}) {
          JsonNode v = root.get(key);
          if (v != null && v.isTextual()) {
            return UUID.fromString(v.asText());
          }
        }
      } catch (Exception ignored) {}
    }

    throw new IllegalStateException(
        "No se pudo extraer UUID de la respuesta. Body=" + responseBody + " Location=" + locationHeader);
  }

  protected void assertJsonArrayOrItemsArray(String body) throws Exception {
    JsonNode root = objectMapper.readTree(body);
    if (root.isArray()) return;
    JsonNode items = root.get("items");
    assertThat(items).as("Esperaba un array o un objeto con campo 'items' array").isNotNull();
    assertThat(items.isArray()).isTrue();
  }

  protected JsonNode readJson(String body) throws Exception {
    return objectMapper.readTree(body);
  }

  protected Optional<JsonNode> readItemsArrayIfWrapped(JsonNode root) {
    if (root.isArray()) return Optional.of(root);
    JsonNode items = root.get("items");
    if (items != null && items.isArray()) return Optional.of(items);
    return Optional.empty();
  }
}
