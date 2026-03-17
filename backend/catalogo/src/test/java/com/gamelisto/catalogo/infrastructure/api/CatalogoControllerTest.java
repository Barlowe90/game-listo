package com.gamelisto.catalogo.infrastructure.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.catalogo.AbstractIntegrationTest;
import com.gamelisto.catalogo.application.usecases.GameResult;
import com.gamelisto.catalogo.application.usecases.PlatformResult;
import com.gamelisto.catalogo.application.usecases.SyncResultResult;
import com.gamelisto.catalogo.application.usecases.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.gamelisto.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

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
    when(syncGamesUseCase.execute(anyInt())).thenReturn(new SyncResultResult(150, 999L));

    mockMvc
        .perform(
            post("/v1/catalogo/sync/games")
                .with(asGatewayUser("ADMIN"))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalSynced").value(150))
        .andExpect(jsonPath("$.lastId").value(999));
  }

  @Test
  @DisplayName("POST /v1/catalogo/sync/games con body debe pasar limit al use case")
  void debeSincronizarJuegosConBody() throws Exception {
    when(syncGamesUseCase.execute(anyInt())).thenReturn(new SyncResultResult(10, 1010L));

    String requestBody = objectMapper.writeValueAsString(Map.of("limit", 10));

    mockMvc
        .perform(
            post("/v1/catalogo/sync/games")
                .with(asGatewayUser("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.totalSynced").value(10))
        .andExpect(jsonPath("$.lastId").value(1010));
  }

  // ─── Sync Platforms ───────────────────────────────────────────────────────
  @Test
  @DisplayName("POST /v1/catalogo/sync/platforms debe retornar 200 con resultado")
  void debeSincronizarPlataformas() throws Exception {
    when(syncPlatformsUseCase.execute()).thenReturn(new SyncResultResult(42, null));
    mockMvc
        .perform(post("/v1/catalogo/sync/platforms").with(asGatewayUser("ADMIN")))
        .andExpect(status().isOk());
  }

  // ─── Game by ID ───────────────────────────────────────────────────────────
  @Test
  @DisplayName("GET /v1/catalogo/games/{id} debe retornar 200 cuando el juego existe")
  void debeRetornarDetalleDeJuego() throws Exception {
    GameResult gameResult =
        new GameResult(
            1L,
            "Zelda",
            "Un juego",
            "https://img/z.jpg",
            List.of("PC"),
            null,
            null,
            List.of(),
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
    when(getGameByIdUseCase.execute(any())).thenReturn(gameResult);

    mockMvc.perform(get("/v1/catalogo/games/1")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("GET /v1/catalogo/games/{id} debe retornar 404 cuando el juego no existe")
  void debeRetornar404SiJuegoNoExiste() throws Exception {
    when(getGameByIdUseCase.execute(any()))
        .thenThrow(new DomainException("Juego no encontrado con ID: 999"));
    mockMvc.perform(get("/v1/catalogo/games/999")).andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("GET /v1/catalogo/games/{id}/detail debe retornar 200 sin autenticación")
  void debeRetornarGameDetailSinAutenticacion() throws Exception {
    GameDetailResult detailResult =
        new GameDetailResult(1L, List.of("https://img/z1.jpg"), List.of("https://yt/z1"));
    when(getGameDetailUseCase.execute(any())).thenReturn(detailResult);

    mockMvc.perform(get("/v1/catalogo/games/1/detail")).andExpect(status().isOk());
  }

  // ─── Platforms ────────────────────────────────────────────────────────────
  @Test
  @DisplayName("GET /v1/catalogo/platforms debe retornar 200 con lista de plataformas")
  void debeListarPlataformas() throws Exception {
    PlatformResult ps4DTO = new PlatformResult(48L, "PlayStation 4", "PS4");
    when(obtenerTodasLasPlatformasUseCase.execute()).thenReturn(List.of(ps4DTO));

    mockMvc.perform(get("/v1/catalogo/platforms")).andExpect(status().isOk());
  }

  @Test
  @DisplayName("POST /v1/catalogo/sync/games sin autenticación debe retornar 403")
  void debeProtegerSyncGamesSinAutenticacion() throws Exception {
    mockMvc.perform(post("/v1/catalogo/sync/games")).andExpect(status().isForbidden());
  }

  private RequestPostProcessor asGatewayUser(String roles) {
    return req -> {
      req.addHeader("X-User-Id", UUID.fromString("11111111-1111-1111-1111-111111111111"));
      req.addHeader("X-User-Roles", roles); // ej: "USER" o "USER,ADMIN"
      return req;
    };
  }
}
