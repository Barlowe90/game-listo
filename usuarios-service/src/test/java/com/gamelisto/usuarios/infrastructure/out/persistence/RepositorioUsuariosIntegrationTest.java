package com.gamelisto.usuarios.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.gamelisto.usuarios.config.TestMessagingConfig;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestMessagingConfig.class)
@Transactional
@DisplayName("Tests de Integración - RepositorioUsuarios con PostgreSQL")
class RepositorioUsuariosIntegrationTest {

  @Autowired private RepositorioUsuarios repositorioUsuarios;

  private Usuario usuarioTest;

  @BeforeEach
  void setUp() {
    // Crear usuario de prueba
    usuarioTest =
        Usuario.create(
            Username.of("testuser"),
            Email.of("test@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"));
  }

  @Test
  @DisplayName("Debe guardar y recuperar un usuario por ID")
  void debeGuardarYRecuperarPorId() {
    // Act
    Usuario guardado = repositorioUsuarios.save(usuarioTest);
    Optional<Usuario> recuperado = repositorioUsuarios.findById(guardado.getId());

    // Assert
    assertThat(recuperado).isPresent();
    assertThat(recuperado.get().getUsername().value()).isEqualTo(usuarioTest.getUsername().value());
    assertThat(recuperado.get().getEmail().value()).isEqualTo(usuarioTest.getEmail().value());
  }

  @Test
  @DisplayName("Debe encontrar usuario por email")
  void debeEncontrarPorEmail() {
    // Arrange
    repositorioUsuarios.save(usuarioTest);

    // Act
    Optional<Usuario> encontrado = repositorioUsuarios.findByEmail(Email.of("test@example.com"));

    // Assert
    assertThat(encontrado).isPresent();
    assertThat(encontrado.get().getUsername().value()).isEqualTo("testuser");
  }

  @Test
  @DisplayName("Debe verificar si existe un username")
  void debeVerificarExistenciaUsername() {
    // Arrange
    repositorioUsuarios.save(usuarioTest);

    // Act
    boolean existe = repositorioUsuarios.existsByUsername(Username.of("testuser"));
    boolean noExiste = repositorioUsuarios.existsByUsername(Username.of("otrouser"));

    // Assert
    assertThat(existe).isTrue();
    assertThat(noExiste).isFalse();
  }

  @Test
  @DisplayName("Debe verificar si existe un email")
  void debeVerificarExistenciaEmail() {
    // Arrange
    repositorioUsuarios.save(usuarioTest);

    // Act
    boolean existe = repositorioUsuarios.existsByEmail(Email.of("test@example.com"));
    boolean noExiste = repositorioUsuarios.existsByEmail(Email.of("otro@example.com"));

    // Assert
    assertThat(existe).isTrue();
    assertThat(noExiste).isFalse();
  }

  @Test
  @DisplayName("Debe encontrar usuario por token de verificación")
  void debeEncontrarPorTokenVerificacion() {
    // Arrange
    Usuario guardado = repositorioUsuarios.save(usuarioTest);
    TokenVerificacion token = guardado.getTokenVerificacion();

    // Act
    Optional<Usuario> encontrado = repositorioUsuarios.findByTokenVerificacion(token);

    // Assert
    assertThat(encontrado).isPresent();
    assertThat(encontrado.get().getId()).isEqualTo(guardado.getId());
  }

  @Test
  @DisplayName("Debe actualizar datos del usuario")
  void debeActualizarUsuario() {
    // Arrange
    Usuario guardado = repositorioUsuarios.save(usuarioTest);

    // Act - Actualizar usuario (sin cambiar username ya que no se puede cambiar)
    Usuario actualizado = repositorioUsuarios.save(guardado);

    // Assert
    Optional<Usuario> recuperado = repositorioUsuarios.findById(actualizado.getId());
    assertThat(recuperado).isPresent();
    assertThat(recuperado.get().getUsername().value()).isEqualTo("testuser");
  }

  @Test
  @DisplayName("Debe listar todos los usuarios")
  void debeListarTodosLosUsuarios() {
    // Arrange
    Usuario usuario1 =
        Usuario.create(
            Username.of("user1"), Email.of("user1@example.com"), PasswordHash.of("$2a$10$hash1"));
    Usuario usuario2 =
        Usuario.create(
            Username.of("user2"), Email.of("user2@example.com"), PasswordHash.of("$2a$10$hash2"));

    repositorioUsuarios.save(usuario1);
    repositorioUsuarios.save(usuario2);

    // Act
    List<Usuario> usuarios = repositorioUsuarios.findAll();

    // Assert
    assertThat(usuarios).hasSizeGreaterThanOrEqualTo(2);
  }

  @Test
  @DisplayName("Debe buscar usuarios por estado")
  void debeBuscarPorEstado() {
    // Arrange
    Usuario usuarioActivo =
        Usuario.create(
            Username.of("activo"), Email.of("activo@example.com"), PasswordHash.of("$2a$10$hash"));
    usuarioActivo.verificarEmail(usuarioActivo.getTokenVerificacion()); // Lo marca como ACTIVO
    repositorioUsuarios.save(usuarioActivo);

    // Usuario pendiente (sin verificar)
    Usuario usuarioPendiente =
        Usuario.create(
            Username.of("pendiente"),
            Email.of("pendiente@example.com"),
            PasswordHash.of("$2a$10$hash"));
    repositorioUsuarios.save(usuarioPendiente);

    // Act
    List<Usuario> activos = repositorioUsuarios.findByStatus(EstadoUsuario.ACTIVO);
    List<Usuario> pendientes =
        repositorioUsuarios.findByStatus(EstadoUsuario.PENDIENTE_DE_VERIFICACION);

    // Assert
    assertThat(activos).isNotEmpty();
    assertThat(pendientes).isNotEmpty();
    assertThat(activos).anyMatch(u -> u.getUsername().value().equals("activo"));
    assertThat(pendientes).anyMatch(u -> u.getUsername().value().equals("pendiente"));
  }

  @Test
  @DisplayName("Debe vincular Discord y buscar por Discord User ID")
  void debeVincularDiscordYBuscar() {
    // Arrange
    Usuario usuario = repositorioUsuarios.save(usuarioTest);
    DiscordUserId discordId = DiscordUserId.of("123456789");
    DiscordUsername discordUsername = DiscordUsername.of("DiscordUser#1234");

    usuario.linkDiscord(discordId, discordUsername);
    repositorioUsuarios.save(usuario);

    // Act
    Optional<Usuario> encontrado = repositorioUsuarios.findByDiscordUserId(discordId);

    // Assert
    assertThat(encontrado).isPresent();
    assertThat(encontrado.get().hasDiscordLinked()).isTrue();
    assertThat(encontrado.get().getDiscordUsername().value()).isEqualTo(discordUsername.value());
  }

  @Test
  @DisplayName("Debe retornar vacío si no existe usuario con email")
  void debeRetornarVacioSiNoExisteEmail() {
    // Act
    Optional<Usuario> noEncontrado =
        repositorioUsuarios.findByEmail(Email.of("noexiste@example.com"));

    // Assert
    assertThat(noEncontrado).isEmpty();
  }

  @Test
  @DisplayName("Debe retornar vacío si no existe usuario con token")
  void debeRetornarVacioSiNoExisteToken() {
    // Arrange
    TokenVerificacion tokenInexistente = TokenVerificacion.generate();

    // Act
    Optional<Usuario> noEncontrado = repositorioUsuarios.findByTokenVerificacion(tokenInexistente);

    // Assert
    assertThat(noEncontrado).isEmpty();
  }
}
