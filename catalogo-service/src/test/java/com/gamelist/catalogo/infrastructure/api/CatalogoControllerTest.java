package com.gamelist.catalogo.infrastructure.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelist.catalogo.AbstractIntegrationTest;
import com.gamelist.catalogo.application.dto.out.GameDTO;
import com.gamelist.catalogo.application.dto.out.PlatformDTO;
import com.gamelist.catalogo.application.dto.out.SyncResultDTO;
import com.gamelist.catalogo.application.usecases.*;
import com.gamelist.catalogo.domain.exceptions.EntityNotFoundException;
import java.util.List;
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
  @MockitoBean private SyncGamesFromIGDBUseCase syncGamesUseCase;
  @MockitoBean private SyncPlatformsFromIGDBUseCase syncPlatformsUseCase;
  @MockitoBean private BuscarGameDetailPorIdUseCase getGameDetailUseCase;
  @MockitoBean private BuscarGamePorIdUseCase getGameByIdUseCase;
  @MockitoBean private ObtenerTodasLasPlatformasUseCase obtenerTodasLasPlatformasUseCase;
  @MockitoBean private ObtenerTodosLosJuegosUseCase obtenerTodosLosJuegosUseCase;

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
    when(syncPlatformsUseCase.execute()).thenReturn(new SyncResultDTO(42, null));
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
    GameDTO gameDTO =
        new GameDTO(
            1L,
            "Zelda",
            "Un juego",
            "https://img/z.jpg",
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
    when(getGameByIdUseCase.execute(any())).thenReturn(gameDTO);

    mockMvc
        .perform(get("/v1/catalogo/games/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Zelda"))
        .andExpect(jsonPath("$.platforms").isArray());
  }

  @Test
  @DisplayName("GET /v1/catalogo/games/{id} debe retornar 404 cuando el juego no existe")
  void debeRetornar404SiJuegoNoExiste() throws Exception {
    when(getGameByIdUseCase.execute(any()))
        .thenThrow(new EntityNotFoundException("Juego no encontrado con ID: 999"));
    mockMvc.perform(get("/v1/catalogo/games/999")).andExpect(status().isNotFound());
  }

  // ─── Platforms ────────────────────────────────────────────────────────────
  @Test
  @DisplayName("GET /v1/catalogo/platforms debe retornar 200 con lista de plataformas")
  void debeListarPlataformas() throws Exception {
    PlatformDTO ps4DTO = new PlatformDTO(48L, "PlayStation 4", "PS4");
    when(obtenerTodasLasPlatformasUseCase.execute()).thenReturn(List.of(ps4DTO));

    mockMvc
        .perform(get("/v1/catalogo/platforms"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(48))
        .andExpect(jsonPath("$[0].name").value("PlayStation 4"))
        .andExpect(jsonPath("$[0].abbreviation").value("PS4"));
  }
}
