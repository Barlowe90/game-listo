package com.gamelisto.usuarios.infrastructure.in.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.usuarios.config.TestMessagingConfig;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.*;
import com.gamelisto.usuarios.infrastructure.in.api.dto.*;
import com.gamelisto.usuarios.test.config.RedisTestContainerExtension;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestMessagingConfig.class)
@Transactional
@DisplayName("Tests de Integración - Autorización por Roles")
@ExtendWith(RedisTestContainerExtension.class)
@SuppressWarnings("null")
class AuthorizationIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private RepositorioUsuarios repositorioUsuarios;

  @Autowired private PasswordEncoder passwordEncoder;

  private Usuario adminUser;
  private Usuario regularUser;
  private Usuario otherUser;

  @BeforeEach
  void setUp() {
    UsuarioId adminId = UsuarioId.generate();
    Instant now = Instant.now();
    String adminUsername = "admin-" + UsuarioId.generate().value().toString().substring(0, 8);
    String regularUsername = "user-" + UsuarioId.generate().value().toString().substring(0, 8);
    String otherUsername = "otheruser-" + UsuarioId.generate().value().toString().substring(0, 8);

    adminUser =
        Usuario.reconstitute(
            adminId,
            Username.of(adminUsername),
            Email.of("admin@test.com"),
            PasswordHash.of(passwordEncoder.encode("Admin123!")),
            Avatar.empty(),
            Rol.ADMIN,
            Idioma.ESP,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            TokenVerificacion.generate(),
            now.plusSeconds(24 * 60 * 60),
            TokenVerificacion.empty(),
            null);
    adminUser = repositorioUsuarios.save(adminUser);

    regularUser =
        Usuario.create(
            Username.of(regularUsername),
            Email.of("user@test.com"),
            PasswordHash.of(passwordEncoder.encode("User123!")));
    regularUser = repositorioUsuarios.save(regularUser);

    otherUser =
        Usuario.create(
            Username.of(otherUsername),
            Email.of("other@test.com"),
            PasswordHash.of(passwordEncoder.encode("Other123!")));
    otherUser = repositorioUsuarios.save(otherUser);
  }

  // Mantener solo los tests para endpoints que existen en el controller actual

  @Nested
  @DisplayName("GET /v1/usuarios/{id} - Obtener usuario por ID (ahora accesible por cualquier rol)")
  class ObtenerUsuarioPorIdUseCaseTests {

    @Test
    @DisplayName("ADMIN puede obtener cualquier usuario por ID")
    void adminPuedeObtenerUsuarioPorId() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/{id}", regularUser.getId().value())
                  .header("X-User-Id", adminUser.getId().value())
                  .header("X-User-Roles", "ADMIN")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").exists())
          .andExpect(jsonPath("$.username").value(regularUser.getUsername().value()));
    }

    @Test
    @DisplayName("USER puede obtener usuario por ID (controller no restringe)")
    void userPuedeObtenerUsuarioPorId() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/{id}", otherUser.getId().value())
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").exists())
          .andExpect(jsonPath("$.username").value(otherUser.getUsername().value()));
    }
  }

  @Nested
  @DisplayName("PATCH /v1/usuarios - Editar perfil (propio usuario)")
  class EditarPerfilTests {

    @Test
    @DisplayName("USER puede editar su propio perfil")
    void userPuedeEditarSuPropioPerfil() throws Exception {
      EditarPerfilUsuarioRequest request =
          new EditarPerfilUsuarioRequest("https://i.imgur.com/my-avatar.png", "ESP");

      mockMvc
          .perform(
              patch("/v1/usuarios")
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.avatar").value("https://i.imgur.com/my-avatar.png"));
    }
  }

  @Nested
  @DisplayName("PUT /v1/usuarios/password - Cambiar contraseña (propio usuario)")
  class CambiarContrasenaTests {

    @Test
    @DisplayName("USER puede cambiar su propia contraseña")
    void userPuedeCambiarSuPropiaContrasena() throws Exception {
      CambiarContrasenaRequest request =
          new CambiarContrasenaRequest("User123!", "NewPassword789!");

      mockMvc
          .perform(
              put("/v1/usuarios/password")
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("PUT /v1/usuarios/email - Cambiar email (propio usuario)")
  class CambiarEmailTests {

    @Test
    @DisplayName("USER puede cambiar su propio email")
    void userPuedeCambiarSuPropioEmail() throws Exception {
      CambiarCorreoRequest request = new CambiarCorreoRequest("mynewemail@user.com");

      mockMvc
          .perform(
              put("/v1/usuarios/email")
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk());
    }
  }
}
