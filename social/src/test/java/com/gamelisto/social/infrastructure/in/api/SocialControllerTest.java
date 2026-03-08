package com.gamelisto.social.infrastructure.in.api;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.gamelisto.social.application.usecases.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("SocialController - Integration (Gateway headers + @PreAuthorize)")
class SocialControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private AgregarAmigoHandle agregarAmigo;
  @MockitoBean private EliminarAmigoHandle eliminarAmigo;
  @MockitoBean private ListarAmigosHandle listarAmigos;
  @MockitoBean private ListarAmigosEnComunHandle listarAmigosEnComun;

  @Test
  @DisplayName("GET /friends debe retornar 200 con lista")
  void debeRetornarListaDeAmigos() throws Exception {
    when(listarAmigos.execute("user1"))
        .thenReturn(List.of(new UserRefResult("user2", "bob", null)));

    mockMvc
        .perform(get("/v1/social/users/user1/friends").with(gatewayHeaders()))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("POST /friends debe retornar 200")
  void debeAgregarAmistad() throws Exception {
    doNothing().when(agregarAmigo).execute("user1", "user2");

    mockMvc
        .perform(post("/v1/social/users/user1/friends/user2").with(gatewayHeaders()))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("DELETE /friends debe retornar 204")
  void debeEliminarAmistad() throws Exception {
    doNothing().when(eliminarAmigo).execute("user1", "user2");

    mockMvc
        .perform(delete("/v1/social/users/user1/friends/user2").with(gatewayHeaders()))
        .andExpect(status().isNoContent());
  }

  private static RequestPostProcessor gatewayHeaders() {
    final String userId = "user1";
    return request -> {
      request.addHeader("X-User-Id", userId);
      request.addHeader("X-User-Roles", "ROLE_USER");
      return request;
    };
  }
}
