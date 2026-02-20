package com.gamelist.catalogo.infrastructure.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelist.catalogo.AbstractIntegrationTest;
import com.gamelist.catalogo.application.dto.results.*;
import com.gamelist.catalogo.application.usecases.*;
import com.gamelist.catalogo.domain.exceptions.EntityNotFoundException;
import com.gamelist.catalogo.domain.game.*;
import com.gamelist.catalogo.domain.repositories.IGameRepository;
import com.gamelist.catalogo.domain.repositories.IPlatformRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@DisplayName("CatalogoController - Tests REST")
class CatalogoControllerTest extends AbstractIntegrationTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private SyncIgdbGamesUseCase syncGamesUseCase;
  @MockitoBean private SyncPlatformsFromIgdbUseCase syncPlatformsUseCase;
  @MockitoBean private GetGameDetailUseCase getGameDetailUseCase;
  @MockitoBean private IPlatformRepository platformRepository;
  @MockitoBean private IGameRepository gameRepository;

  // ─── Sync Games ───────────────────────────────────────────────────────────
  @Test
  @DisplayName("POST /v1/catalogo/sync/games sin body debe retornar 200 con resultado de sync")
  void debeSincronizarJuegosSinBody() throws Exception {
    when(syncGamesUseCase.execute(anyInt())).thenReturn(new SyncResultDTO(150, 999L));
    mockMvc
        .perform(post("/v1/catalogo/sync/games").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalSynced").value(150))
        .andExpect(jsonPath("$.lastId").value(999))
        .andExpect(jsonPath("$.message").value("Sincronización de juegos completada"));
  }

  @Test
  @DisplayName("POST /v1/catalogo/sync/games con body debe pasar limit al use case")
  void debeSincronizarJuegosConBody() throws Exception {
    when(syncGamesUseCase.execute(anyInt())).thenReturn(new SyncResultDTO(10, 1010L));
    String requestBody =
        """
        {"limit": 10}
        """;
    mockMvc
        .perform(
            post("/v1/catalogo/sync/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalSynced").value(10))
        .andExpect(jsonPath("$.lastId").value(1010));
  }

  // ─── Sync Platforms ───────────────────────────────────────────────────────
  @Test
  @DisplayName("POST /v1/catalogo/sync/platforms debe retornar 200 con resultado")
  void debeSincronizarPlataformas() throws Exception {
    when(syncPlatformsUseCase.execute(any())).thenReturn(new SyncResultDTO(42, null));
    mockMvc
        .perform(post("/v1/catalogo/sync/platforms"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalSynced").value(42))
        .andExpect(jsonPath("$.message").value("Sincronización de plataformas completada"));
  }

  // ─── Game by ID ───────────────────────────────────────────────────────────
  @Test
  @DisplayName("GET /v1/catalogo/games/{id} debe retornar 200 cuando el juego existe")
  void debeRetornarDetalleDeJuego() throws Exception {
    Game game =
        Game.reconstitute(
            GameId.of(1L),
            GameName.of("Zelda"),
            Summary.of("Un juego"),
            CoverUrl.of("https://img/z.jpg"),
            List.of("PC"),
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null);
    when(gameRepository.findById(any())).thenReturn(Optional.of(game));

    mockMvc
        .perform(get("/v1/catalogo/games/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Zelda"))
        .andExpect(jsonPath("$.platforms").isArray());
  }

  @Test
  @DisplayName("GET /v1/catalogo/games/{id} debe retornar 404 cuando el juego no existe")
  void debeRetornar404SiJuegoNoExiste() throws Exception {
    when(gameRepository.findById(any()))
        .thenThrow(new EntityNotFoundException("Juego no encontrado con ID: 999"));
    mockMvc.perform(get("/v1/catalogo/games/999")).andExpect(status().isNotFound());
  }

  // ─── Platforms ────────────────────────────────────────────────────────────
  @Test
  @DisplayName("GET /v1/catalogo/platforms debe retornar 200 con lista de plataformas")
  void debeListarPlataformas() throws Exception {
    var ps4 =
        com.gamelist.catalogo.domain.catalog.Platform.create(
            com.gamelist.catalogo.domain.catalog.PlatformId.of(48L),
            com.gamelist.catalogo.domain.catalog.PlatformName.of("PlayStation 4"),
            com.gamelist.catalogo.domain.catalog.PlatformAbbreviation.of("PS4"));
    when(platformRepository.findAll()).thenReturn(List.of(ps4));
    mockMvc
        .perform(get("/v1/catalogo/platforms"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(48))
        .andExpect(jsonPath("$[0].name").value("PlayStation 4"))
        .andExpect(jsonPath("$[0].abbreviation").value("PS4"));
  }

  @Test
  @DisplayName("GET /v1/catalogo/platforms/{id} debe retornar 404 cuando la plataforma no existe")
  void debeRetornar404SiPlataformaNoExiste() throws Exception {
    when(platformRepository.findById(any())).thenReturn(Optional.empty());
    mockMvc.perform(get("/v1/catalogo/platforms/99999")).andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /v1/catalogo/platforms/{id} debe retornar 200 cuando la plataforma existe")
  void debeRetornarPlataformaPorId() throws Exception {
    var ps5 =
        com.gamelist.catalogo.domain.catalog.Platform.create(
            com.gamelist.catalogo.domain.catalog.PlatformId.of(167L),
            com.gamelist.catalogo.domain.catalog.PlatformName.of("PlayStation 5"),
            com.gamelist.catalogo.domain.catalog.PlatformAbbreviation.of("PS5"));
    when(platformRepository.findById(any())).thenReturn(Optional.of(ps5));
    mockMvc
        .perform(get("/v1/catalogo/platforms/167"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(167))
        .andExpect(jsonPath("$.abbreviation").value("PS5"));
  }
}
