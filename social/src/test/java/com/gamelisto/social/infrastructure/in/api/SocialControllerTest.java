package com.gamelisto.social.infrastructure.in.api;

import com.gamelisto.social.application.usecases.UserRefResult;
import com.gamelisto.social.application.usecases.AgregarAmigoHandle;
import com.gamelisto.social.application.usecases.EliminarAmigoHandle;
import com.gamelisto.social.application.usecases.ListarAmigosEnComunHandle;
import com.gamelisto.social.application.usecases.ListarAmigosHandle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SocialController.class)
@ActiveProfiles("test")
@DisplayName("SocialController - REST endpoints")
class SocialControllerTest {

  @Autowired private MockMvc mockMvc;
  @MockitoBean private AgregarAmigoHandle agregarAmigo;
  @MockitoBean private EliminarAmigoHandle eliminarAmigo;
  @MockitoBean private ListarAmigosHandle listarAmigos;
  @MockitoBean private ListarAmigosEnComunHandle listarAmigosEnComun;

  @Test
  @WithMockUser
  @DisplayName("GET /friends debe retornar 200 con lista")
  void debeRetornarListaDeAmigos() throws Exception {
    when(listarAmigos.execute("user1"))
        .thenReturn(List.of(new UserRefResult("user2", "bob", null)));
    mockMvc
        .perform(get("/v1/social/users/user1/friends"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("user2"));
  }

  @Test
  @WithMockUser
  @DisplayName("POST /friends debe retornar 200")
  void debeAgregarAmistad() throws Exception {
    mockMvc.perform(post("/v1/social/users/user1/friends/user2")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  @DisplayName("DELETE /friends debe retornar 204")
  void debeEliminarAmistad() throws Exception {
    mockMvc
        .perform(delete("/v1/social/users/user1/friends/user2"))
        .andExpect(status().isNoContent());
  }
}
