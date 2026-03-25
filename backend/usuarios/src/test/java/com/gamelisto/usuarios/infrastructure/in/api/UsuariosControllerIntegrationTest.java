package com.gamelisto.usuarios.infrastructure.in.api;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.usuarios.config.TestMessagingConfig;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.*;
import com.gamelisto.usuarios.infrastructure.in.api.dto.CambiarContrasenaRequest;
import com.gamelisto.usuarios.infrastructure.in.api.dto.CrearUsuarioRequest;
import com.gamelisto.usuarios.infrastructure.in.api.dto.EditarPerfilUsuarioRequest;
import com.gamelisto.usuarios.infrastructure.in.api.dto.VerificarEmailRequest;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
@DisplayName("Tests de Integración - REST API de Usuarios")
@SuppressWarnings("null")
class UsuariosControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private RepositorioUsuarios repositorioUsuarios;

  @Autowired private PasswordEncoder passwordEncoder;

  private Usuario usuarioExistente;
  private Usuario adminUser;

  @BeforeEach
  void setUp() {
    // Crear usuario existente en la base de datos
    usuarioExistente =
        Usuario.create(
            Username.of("existinguser"),
            Email.of("existing@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"));
    usuarioExistente = repositorioUsuarios.save(usuarioExistente);

    // Crear usuario ADMIN para los tests que requieren privilegios
    Instant now = Instant.now();
    adminUser =
        Usuario.reconstitute(
            UsuarioId.generate(),
            Username.of("admintest"),
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
  }

  @Test
  @DisplayName("POST /v1/usuarios/auth/register - Debe crear un nuevo usuario")
  void debeCrearNuevoUsuario() throws Exception {
    // Arrange
    CrearUsuarioRequest request =
        new CrearUsuarioRequest("newuser", "newuser@example.com", "Password123!");

    // Act & Assert
    mockMvc
        .perform(
            post("/v1/usuarios/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.username").value("newuser"))
        .andExpect(jsonPath("$.email").value("newuser@example.com"))
        .andExpect(jsonPath("$.status").value("PENDIENTE_DE_VERIFICACION"));
  }

  @Test
  @DisplayName("POST /v1/usuarios/auth/register - Debe fallar si el username ya existe (422)")
  void debeFallarSiUsernameYaExiste() throws Exception {
    // Arrange
    CrearUsuarioRequest request =
        new CrearUsuarioRequest(
            "existinguser", // Ya existe
            "another@example.com",
            "Password123!");

    // Act & Assert
    mockMvc
        .perform(
            post("/v1/usuarios/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().is(422))
        .andExpect(jsonPath("$.error").value(containsString("existinguser")));
  }

  @Test
  @DisplayName("POST /v1/usuarios/auth/register - Debe fallar si el email ya está registrado (422)")
  void debeFallarSiEmailYaExiste() throws Exception {
    // Arrange
    CrearUsuarioRequest request =
        new CrearUsuarioRequest(
            "uniqueuser",
            "existing@example.com", // Ya existe
            "Password123!");

    // Act & Assert
    mockMvc
        .perform(
            post("/v1/usuarios/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().is(422))
        .andExpect(jsonPath("$.error").value(containsString("existing@example.com")));
  }

  @Test
  @DisplayName("POST /v1/usuarios - Debe validar formato de email")
  void debeValidarFormatoEmail() throws Exception {
    // Arrange
    CrearUsuarioRequest request =
        new CrearUsuarioRequest(
            "newuser",
            "invalid-email", // Email inválido
            "Password123!");

    // Act & Assert
    mockMvc
        .perform(
            post("/v1/usuarios/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("GET /v1/usuarios/{id} - Debe obtener un usuario por ID")
  void debeObtenerUsuarioPorId() throws Exception {
    // Act & Assert
    mockMvc
        .perform(
            get("/v1/usuarios/{id}", usuarioExistente.getId().value())
                .header("X-User-Id", adminUser.getId().value())
                .header("X-User-Roles", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.username").value("existinguser"))
        .andExpect(jsonPath("$.email").value("existing@example.com"));
  }

  @Test
  @DisplayName("GET /v1/usuarios/{id} - Debe retornar 422 si el usuario no existe")
  void debeRetornar404SiUsuarioNoExiste() throws Exception {
    // Act & Assert - usar UUID válido pero inexistente
    mockMvc
        .perform(
            get("/v1/usuarios/{id}", "00000000-0000-0000-0000-000000000000")
                .header("X-User-Id", adminUser.getId().value())
                .header("X-User-Roles", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is(422));
  }

  @Test
  @DisplayName("GET /v1/usuarios/admin/users - Debe listar todos los usuarios")
  void debeListarTodosLosUsuarios() throws Exception {
    // Act & Assert
    mockMvc
        .perform(
            get("/v1/usuarios/admin/users")
                .header("X-User-Id", adminUser.getId().value())
                .header("X-User-Roles", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
        .andExpect(jsonPath("$[*].username", hasItem("existinguser")));
  }

  @Test
  @DisplayName("PATCH /v1/usuarios - Debe editar el perfil de un usuario")
  void debeEditarPerfilUsuario() throws Exception {
    // Arrange
    EditarPerfilUsuarioRequest request =
        new EditarPerfilUsuarioRequest("https://i.imgur.com/newavatar.png", "ENG");

    // Act & Assert - Usuario edita su propio perfil
    mockMvc
        .perform(
            patch("/v1/usuarios", usuarioExistente.getId().value())
                .header("X-User-Id", usuarioExistente.getId().value())
                .header("X-User-Roles", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.avatar").value("https://i.imgur.com/newavatar.png"))
        .andExpect(jsonPath("$.language").value("ENG"));
  }

  @Test
  @DisplayName("DELETE /v1/usuarios/{id} - Debe eliminar un usuario")
  void debeEliminarUsuario() throws Exception {
    // Act & Assert - ADMIN elimina usuario
    mockMvc
        .perform(
            delete("/v1/usuarios/admin/{id}", usuarioExistente.getId().value())
                .header("X-User-Id", adminUser.getId().value())
                .header("X-User-Roles", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    // Verificar que el usuario ahora tiene estado ELIMINADO
    mockMvc
        .perform(
            get("/v1/usuarios/{id}", usuarioExistente.getId().value())
                .header("X-User-Id", adminUser.getId().value())
                .header("X-User-Roles", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("ELIMINADO"));
  }

  @Test
  @DisplayName("PATCH /v1/usuarios/{id}/estado - Debe cambiar el estado del usuario")
  void debeCambiarEstadoUsuario() throws Exception {
    // Arrange - Usar el nombre de campo correcto: estadoUsuario
    Map<String, String> request = new HashMap<>();
    request.put("estadoUsuario", "SUSPENDIDO");

    // Act & Assert - ADMIN cambia estado
    mockMvc
        .perform(
            patch("/v1/usuarios/admin/{id}/estado", usuarioExistente.getId().value())
                .header("X-User-Id", adminUser.getId().value())
                .header("X-User-Roles", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("SUSPENDIDO"));
  }

  @Test
  @DisplayName("PUT /v1/usuarios/password - Debe cambiar la contraseña")
  void debeCambiarContrasena() throws Exception {
    // Arrange - Crear usuario con contraseña conocida
    Usuario usuario =
        Usuario.create(
            Username.of("userpasstest"),
            Email.of("passtest@example.com"),
            PasswordHash.of(passwordEncoder.encode("OldPassword123!")));
    usuario = repositorioUsuarios.save(usuario);

    CambiarContrasenaRequest request =
        new CambiarContrasenaRequest("OldPassword123!", "NewPassword456!");

    // Act & Assert - Usuario cambia su propia contraseña
    mockMvc
        .perform(
            put("/v1/usuarios/password", usuario.getId().value())
                .header("X-User-Id", usuario.getId().value())
                .header("X-User-Roles", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("POST /v1/usuarios/auth/verify-email - Debe verificar email con token válido")
  void debeVerificarEmailConTokenValido() throws Exception {
    // Arrange
    String token = usuarioExistente.getTokenVerificacion().value();
    VerificarEmailRequest request = new VerificarEmailRequest(token);

    // Act & Assert
    mockMvc
        .perform(
            post("/v1/usuarios/auth/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("POST /v1/usuarios/auth/verify-email - Debe fallar con token inválido (422)")
  void debeFallarConTokenInvalido() throws Exception {
    // Arrange
    VerificarEmailRequest request = new VerificarEmailRequest("token-invalido-12345");

    // Act & Assert
    mockMvc
        .perform(
            post("/v1/usuarios/auth/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().is(422));
  }

  @Test
  @DisplayName("GET /v1/usuarios/admin/users?estado=... - Debe filtrar usuarios por estado")
  void debeFiltrarPorEstado() throws Exception {
    // Act & Assert - ADMIN puede filtrar
    mockMvc
        .perform(
            get("/v1/usuarios/admin/users")
                .param("estado", "PENDIENTE_DE_VERIFICACION")
                .header("X-User-Id", adminUser.getId().value())
                .header("X-User-Roles", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }
}
