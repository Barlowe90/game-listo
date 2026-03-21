package com.gamelisto.catalogo.infrastructure.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.catalogo.AbstractIntegrationTest;
import com.gamelisto.catalogo.application.usecases.GameCardResult;
import com.gamelisto.catalogo.application.usecases.GameResult;
import com.gamelisto.catalogo.application.usecases.ObtenerTodosLosJuegosCommand;
import com.gamelisto.catalogo.application.usecases.PlatformResult;
import com.gamelisto.catalogo.application.usecases.SyncResultResult;
import com.gamelisto.catalogo.application.usecases.*;
import com.gamelisto.catalogo.domain.PageResult;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.gamelisto.catalogo.domain.exceptions.DomainException;
import org.mockito.ArgumentCaptor;
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
  @MockitoBean private ResolverJuegosPorSteamAppIdsUseCase resolverJuegosPorSteamAppIdsUseCase;

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
  @DisplayName("GET /v1/catalogo/games debe retornar resultados paginados con headers")
  void debeRetornarJuegosPaginadosConHeaders() throws Exception {
    GameCardResult gameResult =
        new GameCardResult(
            1L,
            "Zelda",
            "https://img/z.jpg",
            List.of("PC"),
            List.of("Cooperative", "Online multiplayer"));
    PageResult<GameCardResult> pageResult = new PageResult<>(List.of(gameResult), 2, 5, 11, 3);

    when(obtenerTodosLosJuegosUseCase.execute(any())).thenReturn(pageResult);

    mockMvc
        .perform(get("/v1/catalogo/games").param("page", "2").param("size", "5"))
        .andExpect(status().isOk())
        .andExpect(header().string("X-Current-Page", "2"))
        .andExpect(header().string("X-Page-Size", "5"))
        .andExpect(header().string("X-Total-Count", "11"))
        .andExpect(header().string("X-Total-Pages", "3"))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("Zelda"))
        .andExpect(jsonPath("$[0].platforms[0]").value("PC"))
        .andExpect(jsonPath("$[0].gameModes[0]").value("Cooperative"))
        .andExpect(jsonPath("$[0].summary").doesNotExist());
  }

  @Test
  @DisplayName("GET /v1/catalogo/games debe aceptar filtros opcionales de plataforma")
  void debeAceptarFiltrosOpcionalesDePlataforma() throws Exception {
    PageResult<GameCardResult> pageResult = new PageResult<>(List.of(), 0, 12, 0, 0);
    ArgumentCaptor<ObtenerTodosLosJuegosCommand> commandCaptor =
        ArgumentCaptor.forClass(ObtenerTodosLosJuegosCommand.class);

    when(obtenerTodosLosJuegosUseCase.execute(any())).thenReturn(pageResult);

    mockMvc
        .perform(
            get("/v1/catalogo/games")
                .param("page", "0")
                .param("size", "12")
                .param("platform", "PS4")
                .param("platform", "PlayStation 4"))
        .andExpect(status().isOk());

    verify(obtenerTodosLosJuegosUseCase).execute(commandCaptor.capture());
    ObtenerTodosLosJuegosCommand command = commandCaptor.getValue();
    org.assertj.core.api.Assertions.assertThat(command.platforms())
        .containsExactlyInAnyOrder("ps4", "playstation 4");
  }

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

  @Test
  @DisplayName(
      "POST /v1/catalogo/games/steam/resolve debe retornar los juegos resueltos sin autenticacion")
  void debeResolverJuegosSteamSinAutenticacion() throws Exception {
    when(resolverJuegosPorSteamAppIdsUseCase.execute(List.of(620L, 730L)))
        .thenReturn(Map.of(620L, 10L, 730L, 20L));

    String requestBody =
        objectMapper.writeValueAsString(Map.of("steamAppIds", List.of(620L, 730L)));

    mockMvc
        .perform(
            post("/v1/catalogo/games/steam/resolve")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(2))
        .andExpect(
            jsonPath("$.items[?(@.steamAppId == 620)].gameId")
                .value(org.hamcrest.Matchers.contains(10)))
        .andExpect(
            jsonPath("$.items[?(@.steamAppId == 730)].gameId")
                .value(org.hamcrest.Matchers.contains(20)));
  }

  private RequestPostProcessor asGatewayUser(String roles) {
    return req -> {
      req.addHeader("X-User-Id", UUID.fromString("11111111-1111-1111-1111-111111111111"));
      req.addHeader("X-User-Roles", roles); // ej: "USER" o "USER,ADMIN"
      return req;
    };
  }
}
