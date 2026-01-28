package com.gamelisto.usuarios_service.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios_service.application.dto.SolicitarRestablecimientoCommand;
import com.gamelisto.usuarios_service.application.ports.IEmailService;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.*;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("SolicitarRestablecimientoUseCase - Tests")
class SolicitarRestablecimientoUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;

  @Mock private IEmailService emailService;

  @InjectMocks private SolicitarRestablecimientoUseCase solicitarRestablecimientoUseCase;

  // ========== CASOS DE ÉXITO ==========

  @Test
  @DisplayName("Debe generar token y enviar email cuando el usuario existe")
  void debeGenerarTokenYEnviarEmail() {
    // Arrange
    String email = "test@test.com";
    Usuario usuario = crearUsuarioActivo(email);

    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    SolicitarRestablecimientoCommand command = new SolicitarRestablecimientoCommand(email);

    // Act
    solicitarRestablecimientoUseCase.execute(command);

    // Assert
    verify(repositorioUsuarios).findByEmail(any(Email.class));
    verify(repositorioUsuarios).save(any(Usuario.class));

    // Verificar que se generó el token
    assertNotNull(usuario.getTokenRestablecimiento());
    assertFalse(usuario.getTokenRestablecimiento().isEmpty());
    assertNotNull(usuario.getTokenRestablecimientoExpiracion());
  }

  @Test
  @DisplayName("Debe generar token con expiración de 1 hora")
  void debeGenerarTokenConExpiracionDeUnaHora() {
    // Arrange
    String email = "test@test.com";
    Usuario usuario = crearUsuarioActivo(email);
    Instant antesDeGenerar = Instant.now();

    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    SolicitarRestablecimientoCommand command = new SolicitarRestablecimientoCommand(email);

    // Act
    solicitarRestablecimientoUseCase.execute(command);

    // Assert
    Instant expiracion = usuario.getTokenRestablecimientoExpiracion();
    assertNotNull(expiracion);

    // Verificar que expira aproximadamente en 1 hora (con margen de 10 segundos)
    long segundosHastaExpiracion = expiracion.getEpochSecond() - antesDeGenerar.getEpochSecond();
    assertTrue(
        segundosHastaExpiracion >= 3590 && segundosHastaExpiracion <= 3610,
        "El token debe expirar en aproximadamente 1 hora");
  }

  // ========== CASOS DE SEGURIDAD ==========

  @Test
  @DisplayName("No debe lanzar excepción cuando el email no existe (seguridad)")
  void noDebeLanzarExcepcionCuandoEmailNoExiste() {
    // Arrange
    String emailInexistente = "noexiste@test.com";

    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.empty());

    SolicitarRestablecimientoCommand command =
        new SolicitarRestablecimientoCommand(emailInexistente);

    // Act & Assert - No debe lanzar excepción
    assertDoesNotThrow(() -> solicitarRestablecimientoUseCase.execute(command));

    // Verificar que no se guardó nada
    verify(repositorioUsuarios, never()).save(any());
  }

  @Test
  @DisplayName("No debe revelar si el email está registrado o no")
  void noDebeRevelarSiEmailEstaRegistrado() {
    // Arrange
    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.empty());

    SolicitarRestablecimientoCommand command =
        new SolicitarRestablecimientoCommand("noexiste@test.com");

    // Act - Ejecutar sin excepción
    solicitarRestablecimientoUseCase.execute(command);

    // Assert - El método termina normalmente sin revelar información
    verify(repositorioUsuarios).findByEmail(any(Email.class));
  }

  // ========== CASOS DE VALIDACIÓN ==========

  @Test
  @DisplayName("Debe lanzar excepción para email con formato inválido")
  void debeLanzarExcepcionParaEmailInvalido() {
    // Arrange
    String emailInvalido = "email-invalido";
    SolicitarRestablecimientoCommand command = new SolicitarRestablecimientoCommand(emailInvalido);

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class, () -> solicitarRestablecimientoUseCase.execute(command));

    verify(repositorioUsuarios, never()).findByEmail(any());
  }

  @Test
  @DisplayName("Debe lanzar excepción para email nulo")
  void debeLanzarExcepcionParaEmailNulo() {
    // Arrange
    SolicitarRestablecimientoCommand command = new SolicitarRestablecimientoCommand(null);

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class, () -> solicitarRestablecimientoUseCase.execute(command));

    verify(repositorioUsuarios, never()).findByEmail(any());
  }

  @Test
  @DisplayName("Debe lanzar excepción para email vacío")
  void debeLanzarExcepcionParaEmailVacio() {
    // Arrange
    SolicitarRestablecimientoCommand command = new SolicitarRestablecimientoCommand("");

    // Act & Assert
    assertThrows(
        IllegalArgumentException.class, () -> solicitarRestablecimientoUseCase.execute(command));

    verify(repositorioUsuarios, never()).findByEmail(any());
  }

  // ========== CASOS DE ESTADOS DE USUARIO ==========

  @Test
  @DisplayName("Debe permitir solicitar restablecimiento para usuario suspendido")
  void debePermitirSolicitudParaUsuarioSuspendido() {
    // Arrange
    String email = "suspendido@test.com";
    Usuario usuario = crearUsuarioSuspendido(email);

    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    SolicitarRestablecimientoCommand command = new SolicitarRestablecimientoCommand(email);

    // Act
    solicitarRestablecimientoUseCase.execute(command);

    // Assert
    verify(repositorioUsuarios).save(any(Usuario.class));
    assertNotNull(usuario.getTokenRestablecimiento());
    assertFalse(usuario.getTokenRestablecimiento().isEmpty());
  }

  @Test
  @DisplayName("Debe permitir solicitar restablecimiento para usuario pendiente de verificación")
  void debePermitirSolicitudParaUsuarioPendiente() {
    // Arrange
    String email = "pendiente@test.com";
    Usuario usuario = crearUsuarioPendienteVerificacion(email);

    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuario));
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    SolicitarRestablecimientoCommand command = new SolicitarRestablecimientoCommand(email);

    // Act
    solicitarRestablecimientoUseCase.execute(command);

    // Assert
    verify(repositorioUsuarios).save(any(Usuario.class));
    assertNotNull(usuario.getTokenRestablecimiento());
    assertFalse(usuario.getTokenRestablecimiento().isEmpty());
  }

  // ========== MÉTODOS AUXILIARES ==========

  private Usuario crearUsuarioActivo(String email) {
    return Usuario.reconstitute(
        UsuarioId.generate(),
        Username.of("testuser"),
        Email.of(email),
        PasswordHash.of("$2a$10$hash"),
        Avatar.empty(),
        Instant.now(),
        Instant.now(),
        Rol.USER,
        Idioma.ESP,
        true,
        EstadoUsuario.ACTIVO,
        DiscordUserId.empty(),
        DiscordUsername.empty(),
        null,
        TokenVerificacion.empty(),
        null,
        TokenVerificacion.empty(),
        null);
  }

  private Usuario crearUsuarioSuspendido(String email) {
    return Usuario.reconstitute(
        UsuarioId.generate(),
        Username.of("suspendido"),
        Email.of(email),
        PasswordHash.of("$2a$10$hash"),
        Avatar.empty(),
        Instant.now(),
        Instant.now(),
        Rol.USER,
        Idioma.ESP,
        true,
        EstadoUsuario.SUSPENDIDO,
        DiscordUserId.empty(),
        DiscordUsername.empty(),
        null,
        TokenVerificacion.empty(),
        null,
        TokenVerificacion.empty(),
        null);
  }

  private Usuario crearUsuarioPendienteVerificacion(String email) {
    return Usuario.reconstitute(
        UsuarioId.generate(),
        Username.of("pendiente"),
        Email.of(email),
        PasswordHash.of("$2a$10$hash"),
        Avatar.empty(),
        Instant.now(),
        Instant.now(),
        Rol.USER,
        Idioma.ESP,
        true,
        EstadoUsuario.PENDIENTE_DE_VERIFICACION,
        DiscordUserId.empty(),
        DiscordUsername.empty(),
        null,
        TokenVerificacion.generate(),
        Instant.now().plusSeconds(86400),
        TokenVerificacion.empty(),
        null);
  }
}
