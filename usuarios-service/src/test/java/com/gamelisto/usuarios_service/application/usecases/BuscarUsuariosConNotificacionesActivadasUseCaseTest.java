package com.gamelisto.usuarios_service.application.usecases;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.*;
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
@DisplayName(
    "BuscarUsuariosConNotificacionesActivadasUseCase - Búsqueda de usuarios con notificaciones"
        + " activas")
class BuscarUsuariosConNotificacionesActivadasUseCaseTest {

  @Mock private RepositorioUsuarios repositorio;

  @InjectMocks private BuscarUsuariosConNotificacionesActivadasUseCase useCase;

  @Test
  @DisplayName("Debe retornar usuarios con notificaciones activadas")
  void debeRetornarUsuariosConNotificacionesActivadas() {
    // Arrange
    Usuario usuario1 =
        Usuario.reconstitute(
            UsuarioId.fromString(UUID.randomUUID().toString()),
            Username.of("user1"),
            Email.of("user1@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar1.jpg"),
            Instant.now().minusSeconds(86400),
            Instant.now(),
            Rol.USER,
            Idioma.ESP,
            true, // notificaciones activas
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            null,
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
            Instant.now().minusSeconds(86400),
            Instant.now(),
            Rol.USER,
            Idioma.ENG,
            true, // notificaciones activas
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            null,
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    when(repositorio.findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true))
        .thenReturn(Arrays.asList(usuario1, usuario2));

    // Act
    List<UsuarioDTO> resultado = useCase.execute();

    // Assert
    assertThat(resultado)
        .hasSize(2)
        .allMatch(dto -> dto.notificationsActive())
        .allMatch(dto -> dto.status().equals("ACTIVO"));

    verify(repositorio, times(1)).findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true);
  }

  @Test
  @DisplayName("Debe excluir usuarios con notificaciones desactivadas")
  void debeExcluirUsuariosConNotificacionesDesactivadas() {
    // Arrange
    Usuario usuarioConNotificaciones =
        Usuario.reconstitute(
            UsuarioId.fromString(UUID.randomUUID().toString()),
            Username.of("userWithNotifs"),
            Email.of("with@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar.jpg"),
            Instant.now().minusSeconds(86400),
            Instant.now(),
            Rol.USER,
            Idioma.ESP,
            true, // notificaciones activas
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            null,
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    // Solo retorna usuarios con notificaciones activas
    when(repositorio.findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true))
        .thenReturn(Collections.singletonList(usuarioConNotificaciones));

    // Act
    List<UsuarioDTO> resultado = useCase.execute();

    // Assert
    assertThat(resultado)
        .hasSize(1)
        .allMatch(dto -> dto.notificationsActive())
        .noneMatch(dto -> !dto.notificationsActive());

    verify(repositorio).findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true);
  }

  @Test
  @DisplayName("Debe excluir usuarios eliminados")
  void debeExcluirUsuariosEliminados() {
    // Arrange
    Usuario usuarioActivo =
        Usuario.reconstitute(
            UsuarioId.fromString(UUID.randomUUID().toString()),
            Username.of("activeuser"),
            Email.of("active@example.com"),
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

    // El repositorio solo retorna usuarios con estado ACTIVO
    when(repositorio.findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true))
        .thenReturn(Collections.singletonList(usuarioActivo));

    // Act
    List<UsuarioDTO> resultado = useCase.execute();

    // Assert
    assertThat(resultado)
        .hasSize(1)
        .allMatch(dto -> dto.status().equals("ACTIVO"))
        .noneMatch(dto -> dto.status().equals("ELIMINADO"))
        .noneMatch(dto -> dto.status().equals("SUSPENDIDO"));

    verify(repositorio).findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true);
  }

  @Test
  @DisplayName("Debe retornar lista vacía si ninguno tiene notificaciones activas")
  void debeRetornarListaVaciaSiNingunoTieneNotificacionesActivas() {
    // Arrange
    when(repositorio.findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true))
        .thenReturn(Collections.emptyList());

    // Act
    List<UsuarioDTO> resultado = useCase.execute();

    // Assert
    assertThat(resultado).isEmpty();

    verify(repositorio).findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true);
  }

  @Test
  @DisplayName("Debe retornar DTOs con todos los campos correctos")
  void debeRetornarDtosConTodosLosCamposCorrectos() {
    // Arrange
    String usuarioIdString = UUID.randomUUID().toString();
    UsuarioId usuarioId = UsuarioId.fromString(usuarioIdString);
    Instant createdAt = Instant.parse("2026-01-01T00:00:00Z");
    Instant updatedAt = Instant.parse("2026-02-01T12:00:00Z");

    Usuario usuario =
        Usuario.reconstitute(
            usuarioId,
            Username.of("testuser"),
            Email.of("test@example.com"),
            PasswordHash.of("$2a$10$password"),
            Avatar.of("https://cdn.example.com/users/test.png"),
            createdAt,
            updatedAt,
            Rol.USER,
            Idioma.ESP,
            true, // notificaciones activas
            EstadoUsuario.ACTIVO,
            DiscordUserId.of("discord123"),
            DiscordUsername.of("testdiscord"),
            Instant.now(),
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    when(repositorio.findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true))
        .thenReturn(Collections.singletonList(usuario));

    // Act
    List<UsuarioDTO> resultado = useCase.execute();

    // Assert
    assertThat(resultado).hasSize(1);
    UsuarioDTO dto = resultado.get(0);

    assertThat(dto.id()).isEqualTo(usuarioIdString);
    assertThat(dto.username()).isEqualTo("testuser");
    assertThat(dto.email()).isEqualTo("test@example.com");
    assertThat(dto.avatar()).isEqualTo("https://cdn.example.com/users/test.png");
    assertThat(dto.role()).isEqualTo("USER");
    assertThat(dto.language()).isEqualTo("ESP");
    assertThat(dto.notificationsActive()).isTrue();
    assertThat(dto.status()).isEqualTo("ACTIVO");
    assertThat(dto.discordUserId()).isEqualTo("discord123");
    assertThat(dto.discordUsername()).isEqualTo("testdiscord");
    assertThat(dto.createdAt()).isEqualTo(createdAt);
    assertThat(dto.updatedAt()).isEqualTo(updatedAt);

    verify(repositorio).findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true);
  }

  @Test
  @DisplayName("Debe incluir usuarios de diferentes idiomas con notificaciones activas")
  void debeIncluirUsuariosDeDiferentesIdiomasConNotificacionesActivas() {
    // Arrange
    Usuario usuarioEspanol =
        Usuario.reconstitute(
            UsuarioId.fromString(UUID.randomUUID().toString()),
            Username.of("usuarioEsp"),
            Email.of("esp@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar1.jpg"),
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

    Usuario usuarioIngles =
        Usuario.reconstitute(
            UsuarioId.fromString(UUID.randomUUID().toString()),
            Username.of("userEng"),
            Email.of("eng@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar2.jpg"),
            Instant.now().minusSeconds(86400),
            Instant.now(),
            Rol.USER,
            Idioma.ENG,
            true,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            DiscordUsername.empty(),
            null,
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    when(repositorio.findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true))
        .thenReturn(Arrays.asList(usuarioEspanol, usuarioIngles));

    // Act
    List<UsuarioDTO> resultado = useCase.execute();

    // Assert
    assertThat(resultado)
        .hasSize(2)
        .anyMatch(dto -> dto.language().equals("ESP"))
        .anyMatch(dto -> dto.language().equals("ENG"))
        .allMatch(dto -> dto.notificationsActive());

    verify(repositorio).findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true);
  }

  @Test
  @DisplayName("Debe incluir usuarios con y sin Discord vinculado")
  void debeIncluirUsuariosConYSinDiscordVinculado() {
    // Arrange
    Usuario usuarioSinDiscord =
        Usuario.reconstitute(
            UsuarioId.fromString(UUID.randomUUID().toString()),
            Username.of("noDiscord"),
            Email.of("nodiscord@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar1.jpg"),
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

    Usuario usuarioConDiscord =
        Usuario.reconstitute(
            UsuarioId.fromString(UUID.randomUUID().toString()),
            Username.of("withDiscord"),
            Email.of("withdiscord@example.com"),
            PasswordHash.of("$2a$10$hashedPassword"),
            Avatar.of("https://example.com/avatar2.jpg"),
            Instant.now().minusSeconds(86400),
            Instant.now(),
            Rol.USER,
            Idioma.ENG,
            true,
            EstadoUsuario.ACTIVO,
            DiscordUserId.of("discord999"),
            DiscordUsername.of("discordUser"),
            Instant.now(),
            TokenVerificacion.empty(),
            Instant.now(),
            TokenVerificacion.empty(),
            null);

    when(repositorio.findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true))
        .thenReturn(Arrays.asList(usuarioSinDiscord, usuarioConDiscord));

    // Act
    List<UsuarioDTO> resultado = useCase.execute();

    // Assert
    assertThat(resultado)
        .hasSize(2)
        .anyMatch(dto -> dto.discordUserId() == null || dto.discordUserId().isEmpty())
        .anyMatch(dto -> dto.discordUserId() != null && !dto.discordUserId().isEmpty());

    verify(repositorio).findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true);
  }

  @Test
  @DisplayName("Debe filtrar solo usuarios ACTIVOS, no SUSPENDIDOS ni PENDIENTES")
  void debeFilterarSoloUsuariosActivosNoSuspendidosNiPendientes() {
    // Arrange
    Usuario usuarioActivo =
        Usuario.reconstitute(
            UsuarioId.fromString(UUID.randomUUID().toString()),
            Username.of("active"),
            Email.of("active@example.com"),
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

    // El repositorio ya filtra por EstadoUsuario.ACTIVO
    when(repositorio.findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true))
        .thenReturn(Collections.singletonList(usuarioActivo));

    // Act
    List<UsuarioDTO> resultado = useCase.execute();

    // Assert
    assertThat(resultado)
        .hasSize(1)
        .allMatch(dto -> dto.status().equals("ACTIVO"))
        .noneMatch(dto -> dto.status().equals("SUSPENDIDO"))
        .noneMatch(dto -> dto.status().equals("PENDIENTE_DE_VERIFICACION"));

    verify(repositorio).findByStatusAndNotificationsActive(EstadoUsuario.ACTIVO, true);
  }
}
