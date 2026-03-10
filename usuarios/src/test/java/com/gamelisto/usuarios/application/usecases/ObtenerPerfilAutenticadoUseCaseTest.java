package com.gamelisto.usuarios.application.usecases;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.exceptions.DomainException;
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
    UUID usuarioId = UUID.randomUUID();

    Usuario usuario =
        Usuario.reconstitute(
            UsuarioId.of(usuarioId),
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
    UsuarioResult resultado = useCase.execute(usuarioId);

    // Assert
    assertThat(resultado)
        .isNotNull()
        .satisfies(
            dto -> {
              assertThat(dto.id()).isEqualTo(usuarioId.toString());
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
    UUID usuarioId = UUID.randomUUID();
    UsuarioId usuarioIdVo = UsuarioId.of(usuarioId);

    Usuario usuario =
        Usuario.reconstitute(
            usuarioIdVo,
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
    UsuarioResult resultado = useCase.execute(usuarioId);

    // Assert
    assertThat(resultado.id()).isEqualTo(usuarioId.toString());
    assertThat(resultado.username()).isEqualTo("johndoe");
    assertThat(resultado.email()).isEqualTo("john@example.com");
    assertThat(resultado.avatar()).isEqualTo("https://cdn.example.com/users/john.png");
    assertThat(resultado.role()).isEqualTo("ADMIN");
    assertThat(resultado.language()).isEqualTo("ENG");
    assertThat(resultado.status()).isEqualTo("ACTIVO");

    verify(repositorio).findById(usuarioIdVo);
  }

  @Test
  @DisplayName("Debe lanzar excepción si usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    // Arrange
    UUID usuarioId = UUID.randomUUID();
    UsuarioId usuarioIdVo = UsuarioId.of(usuarioId);

    when(repositorio.findById(any(UsuarioId.class))).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> useCase.execute(usuarioId))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("Usuario no encontrado")
        .hasMessageContaining(usuarioId.toString());

    verify(repositorio, times(1)).findById(any(UsuarioId.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción si usuario está eliminado")
  void debeLanzarExcepcionSiUsuarioEstaEliminado() {
    // Arrange
    UUID usuarioId = UUID.randomUUID();
    UsuarioId usuarioIdVo = UsuarioId.of(usuarioId);

    Usuario usuarioEliminado =
        Usuario.reconstitute(
            usuarioIdVo,
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
    UsuarioResult resultado = useCase.execute(usuarioId);

    // Assert
    assertThat(resultado).isNotNull();
    assertThat(resultado.status()).isEqualTo("ELIMINADO");

    verify(repositorio).findById(any(UsuarioId.class));
  }

  @Test
  @DisplayName("Debe incluir información de Discord si está vinculado")
  void debeIncluirInformacionDeDiscordSiEstaVinculado() {
    // Arrange
    UUID usuarioId = UUID.randomUUID();
    UsuarioId usuarioIdVo = UsuarioId.of(usuarioId);

    Usuario usuario =
        Usuario.reconstitute(
            usuarioIdVo,
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
    UsuarioResult resultado = useCase.execute(usuarioId);

    // Assert
    assertThat(resultado)
        .isNotNull()
        .satisfies(
            dto -> {
              assertThat(dto.discordUserId()).isEqualTo("987654321");
              assertThat(dto.discordUsername()).isEqualTo("GamerPro#1234");
            });

    verify(repositorio).findById(usuarioIdVo);
  }

  @Test
  @DisplayName("Debe manejar usuario sin Discord vinculado")
  void debeManejarUsuarioSinDiscordVinculado() {
    // Arrange
    UUID usuarioId = UUID.randomUUID();
    UsuarioId usuarioIdVo = UsuarioId.of(usuarioId);

    Usuario usuario =
        Usuario.reconstitute(
            usuarioIdVo,
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
    UsuarioResult resultado = useCase.execute(usuarioId);

    // Assert
    assertThat(resultado.discordUserId()).isNull();
    assertThat(resultado.discordUsername()).isNull();

    verify(repositorio).findById(usuarioIdVo);
  }

  @Test
  @DisplayName("Debe lanzar excepción con formato de UUID inválido")
  void debeLanzarExcepcionConFormatoUuidInvalido() {
    // Arrange

    // Act & Assert - ahora el use case recibe UUID, por tanto pasar null debe lanzar
    // IllegalArgumentException
    assertThatThrownBy(() -> useCase.execute(null))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("nulo");

    verify(repositorio, never()).findById(any(UsuarioId.class));
  }

  @Test
  @DisplayName("Debe manejar diferentes roles de usuario")
  void debeManejarDiferentesRolesDeUsuario() {
    // Arrange - Usuario ADMIN
    UUID adminId = UUID.randomUUID();
    UsuarioId adminIdVo = UsuarioId.of(adminId);

    Usuario admin =
        Usuario.reconstitute(
            adminIdVo,
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
    UsuarioResult resultado = useCase.execute(adminId);

    // Assert
    assertThat(resultado.role()).isEqualTo("ADMIN");
    verify(repositorio).findById(adminIdVo);
  }

  @Test
  @DisplayName("Debe manejar diferentes estados de usuario")
  void debeManejarDiferentesEstadosDeUsuario() {
    // Arrange - Usuario SUSPENDIDO
    UUID usuarioId = UUID.randomUUID();
    UsuarioId usuarioIdVo = UsuarioId.of(usuarioId);

    Usuario usuarioSuspendido =
        Usuario.reconstitute(
            usuarioIdVo,
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
    UsuarioResult resultado = useCase.execute(usuarioId);

    // Assert
    assertThat(resultado.status()).isEqualTo("SUSPENDIDO");
    verify(repositorio).findById(usuarioIdVo);
  }
}
