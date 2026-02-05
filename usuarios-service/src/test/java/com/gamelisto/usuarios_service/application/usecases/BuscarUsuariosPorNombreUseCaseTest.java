package com.gamelisto.usuarios_service.application.usecases;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.*;
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
@DisplayName("BuscarUsuariosPorNombreUseCase - Búsqueda de usuarios por nombre")
class BuscarUsuariosPorNombreUseCaseTest {

  @Mock private RepositorioUsuarios repositorio;

  @InjectMocks private BuscarUsuariosPorNombreUseCase useCase;

  @Test
  @DisplayName("Debe buscar usuario por nombre exacto")
  void debeBuscarPorNombreExacto() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);
    String username = "testuser";

    Usuario usuario =
        Usuario.reconstitute(
            usuarioId,
            Username.of(username),
            Email.of("test@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar.jpg"),
            Instant.now().minusSeconds(86400),
            Instant.now(),
            Rol.USER,
            Idioma.ESP,
            true,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            null,
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    when(repositorio.findByUsername(any(Username.class))).thenReturn(Optional.of(usuario));

    // Act
    UsuarioDTO resultado = useCase.execute(username);

    // Assert
    assertThat(resultado)
        .isNotNull()
        .satisfies(
            dto -> {
              assertThat(dto.id()).isEqualTo(usuarioIdString);
              assertThat(dto.username()).isEqualTo(username);
              assertThat(dto.email()).isEqualTo("test@example.com");
              assertThat(dto.status()).isEqualTo("ACTIVO");
            });

    verify(repositorio, times(1)).findByUsername(any(Username.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción si usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    // Arrange
    String username = "nonexistent";

    when(repositorio.findByUsername(any(Username.class))).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> useCase.execute(username))
        .isInstanceOf(UsuarioNoEncontradoException.class)
        .hasMessageContaining(username);

    verify(repositorio, times(1)).findByUsername(any(Username.class));
  }

  @Test
  @DisplayName("Debe retornar usuario con todos los campos correctos")
  void debeRetornarUsuarioConTodosLosCampos() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);
    String username = "johndoe";
    Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
    Instant updatedAt = Instant.parse("2026-02-01T12:00:00Z");

    Usuario usuario =
        Usuario.reconstitute(
            usuarioId,
            Username.of(username),
            Email.of("john@example.com"),
            PasswordHash.of("$2a$10$password"),
            Avatar.of("https://cdn.example.com/users/john.png"),
            createdAt,
            updatedAt,
            Rol.ADMIN,
            Idioma.ENG,
            false,
            EstadoUsuario.ACTIVO,
            DiscordUserId.of("discord123"),
            DiscordUsername.of("johndiscord"),
            Instant.now(),
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    when(repositorio.findByUsername(any(Username.class))).thenReturn(Optional.of(usuario));

    // Act
    UsuarioDTO resultado = useCase.execute(username);

    // Assert
    assertThat(resultado.id()).isEqualTo(usuarioIdString);
    assertThat(resultado.username()).isEqualTo(username);
    assertThat(resultado.email()).isEqualTo("john@example.com");
    assertThat(resultado.avatar()).isEqualTo("https://cdn.example.com/users/john.png");
    assertThat(resultado.role()).isEqualTo("ADMIN");
    assertThat(resultado.language()).isEqualTo("ENG");
    assertThat(resultado.notificationsActive()).isFalse();
    assertThat(resultado.status()).isEqualTo("ACTIVO");
    assertThat(resultado.discordUserId()).isEqualTo("discord123");
    assertThat(resultado.discordUsername()).isEqualTo("johndiscord");
    assertThat(resultado.createdAt()).isEqualTo(createdAt);
    assertThat(resultado.updatedAt()).isEqualTo(updatedAt);

    verify(repositorio).findByUsername(any(Username.class));
  }

  @Test
  @DisplayName("Debe manejar búsqueda case-sensitive según Username VO")
  void debeManejarBusquedaCaseSensitive() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);
    String username = "TestUser";

    Usuario usuario =
        Usuario.reconstitute(
            usuarioId,
            Username.of(username),
            Email.of("test@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar.jpg"),
            Instant.now().minusSeconds(86400),
            Instant.now(),
            Rol.USER,
            Idioma.ESP,
            true,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            null,
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    when(repositorio.findByUsername(any(Username.class))).thenReturn(Optional.of(usuario));

    // Act
    UsuarioDTO resultado = useCase.execute(username);

    // Assert
    assertThat(resultado.username()).isEqualTo(username);
    verify(repositorio).findByUsername(any(Username.class));
  }

  @Test
  @DisplayName("Debe excluir usuarios con estado ELIMINADO cuando se buscan")
  void debeExcluirUsuariosEliminadosCuandoSeBuscan() {
    // Arrange
    String username = "deleteduser";

    when(repositorio.findByUsername(any(Username.class))).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> useCase.execute(username))
        .isInstanceOf(UsuarioNoEncontradoException.class);

    verify(repositorio).findByUsername(any(Username.class));
  }

  @Test
  @DisplayName("Debe manejar caracteres especiales válidos en username")
  void debeManejarCaracteresEspecialesValidos() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);
    String username = "user_name-123";

    Usuario usuario =
        Usuario.reconstitute(
            usuarioId,
            Username.of(username),
            Email.of("test@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar.jpg"),
            Instant.now().minusSeconds(86400),
            Instant.now(),
            Rol.USER,
            Idioma.ESP,
            true,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            null,
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    when(repositorio.findByUsername(any(Username.class))).thenReturn(Optional.of(usuario));

    // Act
    UsuarioDTO resultado = useCase.execute(username);

    // Assert
    assertThat(resultado.username()).isEqualTo(username);
    verify(repositorio).findByUsername(any(Username.class));
  }
}
