package com.gamelisto.usuarios_service.infrastructure.api;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.usuarios_service.config.TestMessagingConfig;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.*;
import com.gamelisto.usuarios_service.infrastructure.api.dto.*;
import com.gamelisto.usuarios_service.test.config.RedisTestContainerExtension;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Tests de integración para autorización por roles en endpoints protegidos.
 *
 * <p>Simula el comportamiento del API Gateway enviando headers HTTP: - X-User-Id: ID del usuario
 * autenticado - X-User-Username: Nombre de usuario - X-User-Roles: Roles del usuario (ej: "USER",
 * "ADMIN")
 *
 * <p>Valida que @PreAuthorize funcione correctamente en UsuariosController.
 */
@SpringBootTest
@AutoConfigureMockMvc
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
    // Crear usuario ADMIN usando reconstitute para establecer el rol
    UsuarioId adminId = UsuarioId.generate();
    Instant now = Instant.now();
    adminUser =
        Usuario.reconstitute(
            adminId,
            Username.of("admin"),
            Email.of("admin@test.com"),
            PasswordHash.of(passwordEncoder.encode("Admin123!")),
            Avatar.empty(),
            now,
            now,
            Rol.ADMIN, // Rol de administrador
            Idioma.ESP,
            true,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            null,
            TokenVerificacion.generate(),
            now.plusSeconds(24 * 60 * 60),
            TokenVerificacion.empty(),
            null);
    adminUser = repositorioUsuarios.save(adminUser);

    // Crear usuario USER regular
    regularUser =
        Usuario.create(
            Username.of("user"),
            Email.of("user@test.com"),
            PasswordHash.of(passwordEncoder.encode("User123!")));
    regularUser = repositorioUsuarios.save(regularUser);

    // Crear otro usuario USER para tests de propiedad
    otherUser =
        Usuario.create(
            Username.of("otheruser"),
            Email.of("other@test.com"),
            PasswordHash.of(passwordEncoder.encode("Other123!")));
    otherUser = repositorioUsuarios.save(otherUser);
  }

  // ============================================================================
  // ENDPOINTS SOLO ADMIN
  // ============================================================================

  @Nested
  @DisplayName("GET /v1/usuarios/users - Listar todos los usuarios (Solo ADMIN)")
  class ListarTodosLosUsuariosTests {

    @Test
    @DisplayName("ADMIN puede listar todos los usuarios")
    void adminPuedeListarUsuarios() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/users")
                  .header("X-User-Id", adminUser.getId().value())
                  .header("X-User-Username", adminUser.getUsername().value())
                  .header("X-User-Roles", "ADMIN")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isArray())
          .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))));
    }

    @Test
    @DisplayName("USER no puede listar todos los usuarios (403 Forbidden)")
    void userNoPuedeListarUsuarios() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/users")
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Sin autenticación no puede listar usuarios (403 Forbidden)")
    void sinAutenticacionNoPuedeListarUsuarios() throws Exception {
      mockMvc
          .perform(get("/v1/usuarios/users").contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("GET /v1/usuarios/{id} - Obtener usuario por ID (Solo ADMIN)")
  class ObtenerUsuarioPorIdTests {

    @Test
    @DisplayName("ADMIN puede obtener cualquier usuario por ID")
    void adminPuedeObtenerUsuarioPorId() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/{id}", regularUser.getId().value())
                  .header("X-User-Id", adminUser.getId().value())
                  .header("X-User-Username", adminUser.getUsername().value())
                  .header("X-User-Roles", "ADMIN")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.id").exists())
          .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    @DisplayName("USER no puede obtener usuario por ID (403 Forbidden)")
    void userNoPuedeObtenerUsuarioPorId() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/{id}", otherUser.getId().value())
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("DELETE /v1/usuarios/{id} - Eliminar usuario (Solo ADMIN)")
  class EliminarUsuarioTests {

    @Test
    @DisplayName("ADMIN puede eliminar cualquier usuario")
    void adminPuedeEliminarUsuario() throws Exception {
      mockMvc
          .perform(
              delete("/v1/usuarios/{id}", otherUser.getId().value())
                  .header("X-User-Id", adminUser.getId().value())
                  .header("X-User-Username", adminUser.getUsername().value())
                  .header("X-User-Roles", "ADMIN")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("USER no puede eliminar usuarios (403 Forbidden)")
    void userNoPuedeEliminarUsuario() throws Exception {
      mockMvc
          .perform(
              delete("/v1/usuarios/{id}", otherUser.getId().value())
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("PATCH /v1/usuarios/{id}/estado - Cambiar estado (Solo ADMIN)")
  class CambiarEstadoUsuarioTests {

    @Test
    @DisplayName("ADMIN puede cambiar el estado de cualquier usuario")
    void adminPuedeCambiarEstadoUsuario() throws Exception {
      Map<String, String> request = new HashMap<>();
      request.put("estadoUsuario", "SUSPENDIDO");

      mockMvc
          .perform(
              patch("/v1/usuarios/{id}/estado", regularUser.getId().value())
                  .header("X-User-Id", adminUser.getId().value())
                  .header("X-User-Username", adminUser.getUsername().value())
                  .header("X-User-Roles", "ADMIN")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value("SUSPENDIDO"));
    }

    @Test
    @DisplayName("USER no puede cambiar el estado de usuarios (403 Forbidden)")
    void userNoPuedeCambiarEstadoUsuario() throws Exception {
      Map<String, String> request = new HashMap<>();
      request.put("estadoUsuario", "SUSPENDIDO");

      mockMvc
          .perform(
              patch("/v1/usuarios/{id}/estado", otherUser.getId().value())
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("GET /v1/usuarios/users/estado - Buscar por estado (Solo ADMIN)")
  class BuscarPorEstadoTests {

    @Test
    @DisplayName("ADMIN puede buscar usuarios por estado")
    void adminPuedeBuscarPorEstado() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/users/estado")
                  .param("estado", "ACTIVO")
                  .header("X-User-Id", adminUser.getId().value())
                  .header("X-User-Username", adminUser.getUsername().value())
                  .header("X-User-Roles", "ADMIN")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("USER no puede buscar usuarios por estado (403 Forbidden)")
    void userNoPuedeBuscarPorEstado() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/users/estado")
                  .param("estado", "ACTIVO")
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName(
      "GET /v1/usuarios/users/notifications-enabled - Usuarios con notificaciones (Solo ADMIN)")
  class BuscarConNotificacionesTests {

    @Test
    @DisplayName("ADMIN puede buscar usuarios con notificaciones activadas")
    void adminPuedeBuscarConNotificaciones() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/users/notifications-enabled")
                  .header("X-User-Id", adminUser.getId().value())
                  .header("X-User-Username", adminUser.getUsername().value())
                  .header("X-User-Roles", "ADMIN")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("USER no puede buscar usuarios con notificaciones (403 Forbidden)")
    void userNoPuedeBuscarConNotificaciones() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/users/notifications-enabled")
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("GET /v1/usuarios/health - Health check (Solo ADMIN)")
  class HealthCheckTests {

    @Test
    @DisplayName("ADMIN puede acceder al health check")
    void adminPuedeAccederHealthCheck() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/health")
                  .header("X-User-Id", adminUser.getId().value())
                  .header("X-User-Username", adminUser.getUsername().value())
                  .header("X-User-Roles", "ADMIN")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("USER no puede acceder al health check (403 Forbidden)")
    void userNoPuedeAccederHealthCheck() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/health")
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }

  // ============================================================================
  // ENDPOINTS ADMIN O PROPIO USUARIO
  // ============================================================================

  @Nested
  @DisplayName("PATCH /v1/usuarios/{id} - Editar perfil (ADMIN o propio usuario)")
  class EditarPerfilTests {

    @Test
    @DisplayName("ADMIN puede editar cualquier perfil")
    void adminPuedeEditarCualquierPerfil() throws Exception {
      EditarPerfilUsuarioRequest request =
          new EditarPerfilUsuarioRequest("https://i.imgur.com/admin-edit.png", "ENG", true);

      mockMvc
          .perform(
              patch("/v1/usuarios/{id}", regularUser.getId().value())
                  .header("X-User-Id", adminUser.getId().value())
                  .header("X-User-Username", adminUser.getUsername().value())
                  .header("X-User-Roles", "ADMIN")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.avatar").value("https://i.imgur.com/admin-edit.png"));
    }

    @Test
    @DisplayName("USER puede editar su propio perfil")
    void userPuedeEditarSuPropioPerfil() throws Exception {
      EditarPerfilUsuarioRequest request =
          new EditarPerfilUsuarioRequest("https://i.imgur.com/my-avatar.png", "ESP", false);

      mockMvc
          .perform(
              patch("/v1/usuarios/{id}", regularUser.getId().value())
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.avatar").value("https://i.imgur.com/my-avatar.png"));
    }

    @Test
    @DisplayName("USER no puede editar perfil de otro usuario (403 Forbidden)")
    void userNoPuedeEditarPerfilAjeno() throws Exception {
      EditarPerfilUsuarioRequest request =
          new EditarPerfilUsuarioRequest("https://i.imgur.com/hacked.png", "ENG", true);

      mockMvc
          .perform(
              patch("/v1/usuarios/{id}", otherUser.getId().value())
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("PUT /v1/usuarios/{id}/password - Cambiar contraseña (ADMIN o propio usuario)")
  class CambiarContrasenaTests {

    @Test
    @DisplayName("ADMIN puede cambiar contraseña de cualquier usuario")
    void adminPuedeCambiarCualquierContrasena() throws Exception {
      CambiarContrasenaRequest request =
          new CambiarContrasenaRequest("User123!", "NewPassword456!");

      mockMvc
          .perform(
              put("/v1/usuarios/{id}/password", regularUser.getId().value())
                  .header("X-User-Id", adminUser.getId().value())
                  .header("X-User-Username", adminUser.getUsername().value())
                  .header("X-User-Roles", "ADMIN")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("USER puede cambiar su propia contraseña")
    void userPuedeCambiarSuPropiaContrasena() throws Exception {
      CambiarContrasenaRequest request =
          new CambiarContrasenaRequest("User123!", "NewPassword789!");

      mockMvc
          .perform(
              put("/v1/usuarios/{id}/password", regularUser.getId().value())
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("USER no puede cambiar contraseña de otro usuario (403 Forbidden)")
    void userNoPuedeCambiarContrasenaAjena() throws Exception {
      CambiarContrasenaRequest request =
          new CambiarContrasenaRequest("Other123!", "HackedPassword!");

      mockMvc
          .perform(
              put("/v1/usuarios/{id}/password", otherUser.getId().value())
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("PUT /v1/usuarios/{id}/email - Cambiar email (ADMIN o propio usuario)")
  class CambiarEmailTests {

    @Test
    @DisplayName("ADMIN puede cambiar email de cualquier usuario")
    void adminPuedeCambiarCualquierEmail() throws Exception {
      CambiarCorreoRequest request = new CambiarCorreoRequest("newemail@admin.com");

      mockMvc
          .perform(
              put("/v1/usuarios/{id}/email", regularUser.getId().value())
                  .header("X-User-Id", adminUser.getId().value())
                  .header("X-User-Username", adminUser.getUsername().value())
                  .header("X-User-Roles", "ADMIN")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("USER puede cambiar su propio email")
    void userPuedeCambiarSuPropioEmail() throws Exception {
      CambiarCorreoRequest request = new CambiarCorreoRequest("mynewemail@user.com");

      mockMvc
          .perform(
              put("/v1/usuarios/{id}/email", regularUser.getId().value())
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("USER no puede cambiar email de otro usuario (403 Forbidden)")
    void userNoPuedeCambiarEmailAjeno() throws Exception {
      CambiarCorreoRequest request = new CambiarCorreoRequest("hacked@email.com");

      mockMvc
          .perform(
              put("/v1/usuarios/{id}/email", otherUser.getId().value())
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("PUT /v1/usuarios/{id}/discord - Vincular Discord (ADMIN o propio usuario)")
  class VincularDiscordTests {

    @Test
    @DisplayName("ADMIN puede vincular Discord a cualquier usuario")
    void adminPuedeVincularDiscordACualquierUsuario() throws Exception {
      VincularDiscordRequest request = new VincularDiscordRequest("123456789", "AdminDiscord#1234");

      mockMvc
          .perform(
              put("/v1/usuarios/{id}/discord", regularUser.getId().value())
                  .header("X-User-Id", adminUser.getId().value())
                  .header("X-User-Username", adminUser.getUsername().value())
                  .header("X-User-Roles", "ADMIN")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.discordUsername").value("AdminDiscord#1234"));
    }

    @Test
    @DisplayName("USER puede vincular Discord a su propia cuenta")
    void userPuedeVincularDiscordASuCuenta() throws Exception {
      VincularDiscordRequest request = new VincularDiscordRequest("987654321", "UserDiscord#5678");

      mockMvc
          .perform(
              put("/v1/usuarios/{id}/discord", regularUser.getId().value())
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.discordUsername").value("UserDiscord#5678"));
    }

    @Test
    @DisplayName("USER no puede vincular Discord a otra cuenta (403 Forbidden)")
    void userNoPuedeVincularDiscordACuentaAjena() throws Exception {
      VincularDiscordRequest request =
          new VincularDiscordRequest("111111111", "HackedDiscord#9999");

      mockMvc
          .perform(
              put("/v1/usuarios/{id}/discord", otherUser.getId().value())
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isForbidden());
    }
  }

  @Nested
  @DisplayName("DELETE /v1/usuarios/{id}/discord - Desvincular Discord (ADMIN o propio usuario)")
  class DesvincularDiscordTests {

    @Test
    @DisplayName("ADMIN puede desvincular Discord de cualquier usuario")
    void adminPuedeDesvincularDiscordDeCualquierUsuario() throws Exception {
      // Primero vincular Discord
      regularUser.linkDiscord(
          DiscordUserId.of("123456789"), DiscordUsername.of("TestDiscord#1234"));
      repositorioUsuarios.save(regularUser);

      mockMvc
          .perform(
              delete("/v1/usuarios/{id}/discord", regularUser.getId().value())
                  .header("X-User-Id", adminUser.getId().value())
                  .header("X-User-Username", adminUser.getUsername().value())
                  .header("X-User-Roles", "ADMIN")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.discordUsername").doesNotExist());
    }

    @Test
    @DisplayName("USER puede desvincular Discord de su propia cuenta")
    void userPuedeDesvincularDiscordDeSuCuenta() throws Exception {
      // Primero vincular Discord
      regularUser.linkDiscord(DiscordUserId.of("987654321"), DiscordUsername.of("MyDiscord#5678"));
      repositorioUsuarios.save(regularUser);

      mockMvc
          .perform(
              delete("/v1/usuarios/{id}/discord", regularUser.getId().value())
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.discordUsername").doesNotExist());
    }

    @Test
    @DisplayName("USER no puede desvincular Discord de otra cuenta (403 Forbidden)")
    void userNoPuedeDesvincularDiscordDeCuentaAjena() throws Exception {
      // Primero vincular Discord al otro usuario
      otherUser.linkDiscord(DiscordUserId.of("111111111"), DiscordUsername.of("OtherDiscord#9999"));
      repositorioUsuarios.save(otherUser);

      mockMvc
          .perform(
              delete("/v1/usuarios/{id}/discord", otherUser.getId().value())
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }

  // ============================================================================
  // ENDPOINTS AUTENTICADOS (cualquier rol)
  // ============================================================================

  @Nested
  @DisplayName("GET /v1/usuarios/users/search - Buscar por username (Autenticado)")
  class BuscarPorUsernameTests {

    @Test
    @DisplayName("ADMIN puede buscar usuarios por username")
    void adminPuedeBuscarPorUsername() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/users/search")
                  .param("username", "user")
                  .header("X-User-Id", adminUser.getId().value())
                  .header("X-User-Username", adminUser.getUsername().value())
                  .header("X-User-Roles", "ADMIN")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.username").value("user"));
    }

    @Test
    @DisplayName("USER puede buscar usuarios por username")
    void userPuedeBuscarPorUsername() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/users/search")
                  .param("username", "admin")
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "USER")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    @DisplayName("Sin autenticación no puede buscar usuarios (403 Forbidden)")
    void sinAutenticacionNoPuedeBuscarPorUsername() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/users/search")
                  .param("username", "admin")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }
  }

  // ============================================================================
  // ENDPOINTS PÚBLICOS (sin autenticación requerida)
  // ============================================================================

  @Nested
  @DisplayName("POST /v1/usuarios/auth/register - Registro (Público)")
  class RegistroPublicoTests {

    @Test
    @DisplayName("Cualquiera puede registrarse sin autenticación")
    void cualquieraPuedeRegistrarse() throws Exception {
      CrearUsuarioRequest request =
          new CrearUsuarioRequest("newpublic", "newpublic@test.com", "Password123!");

      mockMvc
          .perform(
              post("/v1/usuarios/auth/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.username").value("newpublic"));
    }
  }

  @Nested
  @DisplayName("POST /v1/usuarios/auth/login - Login (Público)")
  class LoginPublicoTests {

    @Test
    @DisplayName("Cualquiera puede hacer login sin autenticación previa")
    void cualquieraPuedeHacerLogin() throws Exception {
      // Primero activar el usuario con su token de verificación
      regularUser.verificarEmail(regularUser.getTokenVerificacion());
      repositorioUsuarios.save(regularUser);

      Map<String, String> request = new HashMap<>();
      request.put("email", "user@test.com");
      request.put("password", "User123!");

      mockMvc
          .perform(
              post("/v1/usuarios/auth/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.accessToken").exists())
          .andExpect(jsonPath("$.refreshToken").exists());
    }
  }

  // ============================================================================
  // EDGE CASES Y VALIDACIONES
  // ============================================================================

  @Nested
  @DisplayName("Edge Cases - Validación de Headers")
  class EdgeCasesTests {

    @Test
    @DisplayName("Headers incompletos deben denegar acceso a endpoint protegido")
    void headersIncompletosDeneganAcceso() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/users")
                  .header("X-User-Id", adminUser.getId().value())
                  // Falta X-User-Roles
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Rol inválido debe denegar acceso a endpoint ADMIN")
    void rolInvalidoDenegaAcceso() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/users")
                  .header("X-User-Id", regularUser.getId().value())
                  .header("X-User-Username", regularUser.getUsername().value())
                  .header("X-User-Roles", "INVALID_ROLE")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Usuario con múltiples roles debe tener acceso ADMIN")
    void usuarioConMultiplesRolesTieneAccesoAdmin() throws Exception {
      mockMvc
          .perform(
              get("/v1/usuarios/users")
                  .header("X-User-Id", adminUser.getId().value())
                  .header("X-User-Username", adminUser.getUsername().value())
                  .header("X-User-Roles", "USER,ADMIN,MODERATOR")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
    }
  }
}
