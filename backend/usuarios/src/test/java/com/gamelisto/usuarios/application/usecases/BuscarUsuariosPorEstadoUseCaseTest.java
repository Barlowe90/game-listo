package com.gamelisto.usuarios.application.usecases;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.usecases.admin.BuscarUsuariosPorEstadoUseCase;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.*;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("BuscarUsuariosPorEstadoUseCase - Búsqueda de usuarios por estado")
class BuscarUsuariosPorEstadoUseCaseTest {

  @Mock private RepositorioUsuarios repositorio;

  @InjectMocks private BuscarUsuariosPorEstadoUseCase useCase;

  @Test
  @DisplayName("Debe buscar usuarios por estado ACTIVO")
  void debeBuscarUsuariosPorEstadoActivo() {
    // Arrange
    Usuario usuario1 =
        Usuario.reconstitute(
            UsuarioId.fromString(UUID.randomUUID().toString()),
            Username.of("user1"),
            Email.of("user1@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar1.jpg"),
            Rol.USER,
            Idioma.ESP,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    Usuario usuario2 =
        Usuario.reconstitute(
            UsuarioId.fromString(UUID.randomUUID().toString()),
            Username.of("user2"),
            Email.of("user2@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar2.jpg"),
            Rol.USER,
            Idioma.ENG,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    when(repositorio.findByStatus(EstadoUsuario.ACTIVO))
        .thenReturn(Arrays.asList(usuario1, usuario2));

    // Act
    List<UsuarioResult> resultado = useCase.execute(EstadoUsuario.ACTIVO);

    // Assert
    assertThat(resultado).hasSize(2).allMatch(dto -> dto.status().equals("ACTIVO"));

    verify(repositorio, times(1)).findByStatus(EstadoUsuario.ACTIVO);
  }

  @Test
  @DisplayName("Debe buscar usuarios por estado SUSPENDIDO")
  void debeBuscarUsuariosPorEstadoSuspendido() {
    // Arrange
    Usuario usuario1 =
        Usuario.reconstitute(
            UsuarioId.fromString(UUID.randomUUID().toString()),
            Username.of("suspendeduser"),
            Email.of("suspended@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar.jpg"),
            Rol.USER,
            Idioma.ESP,
            EstadoUsuario.SUSPENDIDO,
            DiscordUserId.empty(),
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null);

    when(repositorio.findByStatus(EstadoUsuario.SUSPENDIDO))
        .thenReturn(Collections.singletonList(usuario1));

    // Act
    List<UsuarioResult> resultado = useCase.execute(EstadoUsuario.SUSPENDIDO);

    // Assert
    assertThat(resultado)
        .hasSize(1)
        .first()
        .satisfies(
            dto -> {
              assertThat(dto.status()).isEqualTo("SUSPENDIDO");
              assertThat(dto.username()).isEqualTo("suspendeduser");
            });

    verify(repositorio).findByStatus(EstadoUsuario.SUSPENDIDO);
  }

  @Test
  @DisplayName("Debe buscar usuarios por estado PENDIENTE_DE_VERIFICACION")
  void debeBuscarUsuariosPorEstadoPendienteDeVerificacion() {
    // Arrange
    Usuario usuario1 =
        Usuario.reconstitute(
            UsuarioId.fromString(UUID.randomUUID().toString()),
            Username.of("pendinguser"),
            Email.of("pending@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar.jpg"),
            Rol.USER,
            Idioma.ESP,
            EstadoUsuario.PENDIENTE_DE_VERIFICACION,
            DiscordUserId.empty(),
            TokenVerificacion.of("token123"),
            null,
            TokenVerificacion.empty(),
            null);

    when(repositorio.findByStatus(EstadoUsuario.PENDIENTE_DE_VERIFICACION))
        .thenReturn(Collections.singletonList(usuario1));

    // Act
    List<UsuarioResult> resultado = useCase.execute(EstadoUsuario.PENDIENTE_DE_VERIFICACION);

    // Assert
    assertThat(resultado)
        .hasSize(1)
        .first()
        .satisfies(
            dto -> {
              assertThat(dto.status()).isEqualTo("PENDIENTE_DE_VERIFICACION");
              assertThat(dto.username()).isEqualTo("pendinguser");
            });

    verify(repositorio).findByStatus(EstadoUsuario.PENDIENTE_DE_VERIFICACION);
  }

  @Test
  @DisplayName("Debe retornar lista vacía si no hay usuarios con ese estado")
  void debeRetornarListaVaciaSiNoHayUsuariosConEseEstado() {
    // Arrange
    when(repositorio.findByStatus(any(EstadoUsuario.class))).thenReturn(Collections.emptyList());

    // Act
    List<UsuarioResult> resultado = useCase.execute(EstadoUsuario.SUSPENDIDO);

    // Assert
    assertThat(resultado).isEmpty();

    verify(repositorio).findByStatus(EstadoUsuario.SUSPENDIDO);
  }

  @Test
  @DisplayName("Debe excluir usuarios eliminados")
  void debeExcluirUsuariosEliminados() {
    // Arrange
    Usuario usuario1 =
        Usuario.reconstitute(
            UsuarioId.fromString(UUID.randomUUID().toString()),
            Username.of("activeuser"),
            Email.of("active@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar1.jpg"),
            Rol.USER,
            Idioma.ESP,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    // Solo usuarios ACTIVOS (los ELIMINADOS no se devuelven)
    when(repositorio.findByStatus(EstadoUsuario.ACTIVO))
        .thenReturn(Collections.singletonList(usuario1));

    // Act
    List<UsuarioResult> resultado = useCase.execute(EstadoUsuario.ACTIVO);

    // Assert
    assertThat(resultado).hasSize(1).noneMatch(dto -> dto.status().equals("ELIMINADO"));

    verify(repositorio).findByStatus(EstadoUsuario.ACTIVO);
  }

  @Test
  @DisplayName("Debe retornar DTOs con todos los campos correctos")
  void debeRetornarDtosConTodosLosCamposCorrectos() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);

    Usuario usuario =
        Usuario.reconstitute(
            usuarioId,
            Username.of("testuser"),
            Email.of("test@example.com"),
            PasswordHash.of("$2a$10$password"),
            Avatar.of("https://cdn.example.com/users/test.png"),
            Rol.ADMIN,
            Idioma.ENG,
            EstadoUsuario.ACTIVO,
            DiscordUserId.of("discord456"),
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    when(repositorio.findByStatus(EstadoUsuario.ACTIVO))
        .thenReturn(Collections.singletonList(usuario));

    // Act
    List<UsuarioResult> resultado = useCase.execute(EstadoUsuario.ACTIVO);

    // Assert
    assertThat(resultado).hasSize(1);
    UsuarioResult dto = resultado.get(0);

    assertThat(dto.id()).isEqualTo(usuarioIdString);
    assertThat(dto.username()).isEqualTo("testuser");
    assertThat(dto.email()).isEqualTo("test@example.com");
    assertThat(dto.avatar()).isEqualTo("https://cdn.example.com/users/test.png");
    assertThat(dto.role()).isEqualTo("ADMIN");
    assertThat(dto.language()).isEqualTo("ENG");
    assertThat(dto.status()).isEqualTo("ACTIVO");
    assertThat(dto.discordUserId()).isEqualTo("discord456");

    verify(repositorio).findByStatus(EstadoUsuario.ACTIVO);
  }

  @Test
  @DisplayName("Debe manejar múltiples usuarios con diferentes configuraciones")
  void debeManjejarMultiplesUsuariosConDiferentesConfiguraciones() {
    // Arrange
    Usuario usuario1 =
        Usuario.reconstitute(
            UsuarioId.fromString(UUID.randomUUID().toString()),
            Username.of("user1"),
            Email.of("user1@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar1.jpg"),
            Rol.USER,
            Idioma.ESP,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    Usuario usuario2 =
        Usuario.reconstitute(
            UsuarioId.fromString(UUID.randomUUID().toString()),
            Username.of("admin1"),
            Email.of("admin1@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar2.jpg"),
            Rol.ADMIN,
            Idioma.ENG,
            EstadoUsuario.ACTIVO,
            DiscordUserId.of("discord789"),
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null);

    when(repositorio.findByStatus(EstadoUsuario.ACTIVO))
        .thenReturn(Arrays.asList(usuario1, usuario2));

    // Act
    List<UsuarioResult> resultado = useCase.execute(EstadoUsuario.ACTIVO);

    // Assert
    assertThat(resultado)
        .hasSize(2)
        .anyMatch(dto -> dto.username().equals("user1") && dto.role().equals("USER"))
        .anyMatch(dto -> dto.username().equals("admin1") && dto.role().equals("ADMIN"));

    verify(repositorio).findByStatus(EstadoUsuario.ACTIVO);
  }
}
