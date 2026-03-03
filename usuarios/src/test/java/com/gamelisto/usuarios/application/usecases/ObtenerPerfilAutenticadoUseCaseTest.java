package com.gamelisto.usuarios.application.usecases;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.UsuarioDTO;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.*;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ObtenerPerfilAutenticadoUseCase - Obtener perfil del usuario logueado")
class ObtenerPerfilAutenticadoUseCaseTest {

  @Mock private RepositorioUsuarios repositorio;

  @InjectMocks private ObtenerPerfilAutenticadoUseCase useCase;

  @Test
  @DisplayName("Debe obtener perfil de usuario autenticado por ID")
  void debeObtenerPerfilDeUsuarioAutenticado() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);

    Usuario usuario =
        Usuario.reconstitute(
            usuarioId,
            Username.of("testuser"),
            Email.of("test@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar.jpg"),
            Rol.USER,
            Idioma.ESP,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null);

    when(repositorio.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));

    // Act
    UsuarioDTO resultado = useCase.execute(usuarioIdString);

    // Assert
    assertThat(resultado)
        .isNotNull()
        .satisfies(
            dto -> {
              assertThat(dto.id()).isEqualTo(usuarioIdString);
              assertThat(dto.username()).isEqualTo("testuser");
              assertThat(dto.email()).isEqualTo("test@example.com");
              assertThat(dto.avatar()).isEqualTo("https://example.com/avatar.jpg");
              assertThat(dto.role()).isEqualTo("USER");
              assertThat(dto.language()).isEqualTo("ESP");
              assertThat(dto.status()).isEqualTo("ACTIVO");
            });

    verify(repositorio, times(1)).findById(any(UsuarioId.class));
  }

  @Test
  @DisplayName("Debe retornar DTO con datos correctos")
  void debeRetornarDtoConDatosCorrectos() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);

    Usuario usuario =
        Usuario.reconstitute(
            usuarioId,
            Username.of("johndoe"),
            Email.of("john@example.com"),
            PasswordHash.of("$2a$10$password"),
            Avatar.of("https://cdn.example.com/users/john.png"),
            Rol.ADMIN,
            Idioma.ENG,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    when(repositorio.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));

    // Act
    UsuarioDTO resultado = useCase.execute(usuarioIdString);

    // Assert
    assertThat(resultado.id()).isEqualTo(usuarioIdString);
    assertThat(resultado.username()).isEqualTo("johndoe");
    assertThat(resultado.email()).isEqualTo("john@example.com");
    assertThat(resultado.avatar()).isEqualTo("https://cdn.example.com/users/john.png");
    assertThat(resultado.role()).isEqualTo("ADMIN");
    assertThat(resultado.language()).isEqualTo("ENG");
    assertThat(resultado.status()).isEqualTo("ACTIVO");

    verify(repositorio).findById(usuarioId);
  }

  @Test
  @DisplayName("Debe lanzar excepción si usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();

    when(repositorio.findById(any(UsuarioId.class))).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> useCase.execute(usuarioIdString))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("Usuario no encontrado")
        .hasMessageContaining(usuarioIdString);

    verify(repositorio, times(1)).findById(any(UsuarioId.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción si usuario está eliminado")
  void debeLanzarExcepcionSiUsuarioEstaEliminado() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);

    Usuario usuarioEliminado =
        Usuario.reconstitute(
            usuarioId,
            Username.of("deleteduser"),
            Email.of("deleted@example.com"),
            PasswordHash.of("$2a$10$hash"),
            Avatar.empty(),
            Rol.USER,
            Idioma.ESP,
            EstadoUsuario.ELIMINADO,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null);

    when(repositorio.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuarioEliminado));

    // Act
    UsuarioDTO resultado = useCase.execute(usuarioIdString);

    // Assert
    assertThat(resultado).isNotNull();
    assertThat(resultado.status()).isEqualTo("ELIMINADO");

    verify(repositorio).findById(any(UsuarioId.class));
  }

  @Test
  @DisplayName("Debe incluir información de Discord si está vinculado")
  void debeIncluirInformacionDeDiscordSiEstaVinculado() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);

    Usuario usuario =
        Usuario.reconstitute(
            usuarioId,
            Username.of("gamer123"),
            Email.of("gamer@example.com"),
            PasswordHash.of("$2a$10$hash"),
            Avatar.of("https://cdn.example.com/avatars/gamer.jpg"),
            Rol.USER,
            Idioma.ESP,
            EstadoUsuario.ACTIVO,
            DiscordUserId.of("987654321"),
            DiscordUsername.of("GamerPro#1234"),
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    when(repositorio.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));

    // Act
    UsuarioDTO resultado = useCase.execute(usuarioIdString);

    // Assert
    assertThat(resultado)
        .isNotNull()
        .satisfies(
            dto -> {
              assertThat(dto.discordUserId()).isEqualTo("987654321");
              assertThat(dto.discordUsername()).isEqualTo("GamerPro#1234");
            });

    verify(repositorio).findById(usuarioId);
  }

  @Test
  @DisplayName("Debe manejar usuario sin Discord vinculado")
  void debeManejarUsuarioSinDiscordVinculado() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);

    Usuario usuario =
        Usuario.reconstitute(
            usuarioId,
            Username.of("normaluser"),
            Email.of("normal@example.com"),
            PasswordHash.of("$2a$10$hash"),
            Avatar.empty(),
            Rol.USER,
            Idioma.ESP,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null);

    when(repositorio.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuario));

    // Act
    UsuarioDTO resultado = useCase.execute(usuarioIdString);

    // Assert
    assertThat(resultado.discordUserId()).isNull();
    assertThat(resultado.discordUsername()).isNull();

    verify(repositorio).findById(usuarioId);
  }

  @Test
  @DisplayName("Debe lanzar excepción con formato de UUID inválido")
  void debeLanzarExcepcionConFormatoUuidInvalido() {
    // Arrange
    String uuidInvalido = "not-a-valid-uuid";

    // Act & Assert
    assertThatThrownBy(() -> useCase.execute(uuidInvalido))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid UUID string");

    verify(repositorio, never()).findById(any(UsuarioId.class));
  }

  @Test
  @DisplayName("Debe manejar diferentes roles de usuario")
  void debeManejarDiferentesRolesDeUsuario() {
    // Arrange - Usuario ADMIN
    String adminIdString = UUID.randomUUID().toString();
    UsuarioId adminId = UsuarioId.fromString(adminIdString);

    Usuario admin =
        Usuario.reconstitute(
            adminId,
            Username.of("admin"),
            Email.of("admin@example.com"),
            PasswordHash.of("$2a$10$hash"),
            Avatar.empty(),
            Rol.ADMIN,
            Idioma.ESP,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null);

    when(repositorio.findById(any(UsuarioId.class))).thenReturn(Optional.of(admin));

    // Act
    UsuarioDTO resultado = useCase.execute(adminIdString);

    // Assert
    assertThat(resultado.role()).isEqualTo("ADMIN");
    verify(repositorio).findById(adminId);
  }

  @Test
  @DisplayName("Debe manejar diferentes estados de usuario")
  void debeManejarDiferentesEstadosDeUsuario() {
    // Arrange - Usuario SUSPENDIDO
    String usuarioIdString = UUID.randomUUID().toString();
    UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);

    Usuario usuarioSuspendido =
        Usuario.reconstitute(
            usuarioId,
            Username.of("suspended"),
            Email.of("suspended@example.com"),
            PasswordHash.of("$2a$10$hash"),
            Avatar.empty(),
            Rol.USER,
            Idioma.ESP,
            EstadoUsuario.SUSPENDIDO,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null);

    when(repositorio.findById(any(UsuarioId.class))).thenReturn(Optional.of(usuarioSuspendido));

    // Act
    UsuarioDTO resultado = useCase.execute(usuarioIdString);

    // Assert
    assertThat(resultado.status()).isEqualTo("SUSPENDIDO");
    verify(repositorio).findById(usuarioId);
  }
}
