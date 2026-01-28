package com.gamelisto.usuarios_service.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios_service.application.dto.CrearUsuarioCommand;
import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.application.ports.IEmailService;
import com.gamelisto.usuarios_service.application.ports.IUsuarioPublisher;
import com.gamelisto.usuarios_service.domain.exceptions.*;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class CrearUsuarioUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private IUsuarioPublisher eventosPublisher;

  @Mock private IEmailService emailService;

  @InjectMocks private CrearUsuarioUseCase crearUsuarioUseCase;

  @BeforeEach
  void setUp() {
    // Setup común si es necesario
  }

  @Test
  @DisplayName("Debe crear usuario exitosamente")
  void debeCrearUsuarioExitosamente() {
    // Arrange
    CrearUsuarioCommand command =
        new CrearUsuarioCommand("nuevoUsuario", "nuevo@test.com", "password123");

    when(repositorioUsuarios.existsByUsername(any(Username.class))).thenReturn(false);
    when(repositorioUsuarios.existsByEmail(any(Email.class))).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    UsuarioDTO resultado = crearUsuarioUseCase.execute(command);

    // Assert
    assertNotNull(resultado);
    assertEquals("nuevoUsuario", resultado.username());
    assertEquals("nuevo@test.com", resultado.email());
    assertEquals("PENDIENTE_DE_VERIFICACION", resultado.status());
    assertEquals("USER", resultado.role());
    assertEquals("ESP", resultado.language());
    assertTrue(resultado.notificationsActive());

    verify(repositorioUsuarios).existsByUsername(any(Username.class));
    verify(repositorioUsuarios).existsByEmail(any(Email.class));
    verify(passwordEncoder).encode("password123");
    verify(repositorioUsuarios).save(any(Usuario.class));
  }

  @Test
  @DisplayName("Debe hashear contraseña antes de guardar")
  void debeHashearContrasenaAntesDeGuardar() {
    // Arrange
    CrearUsuarioCommand command =
        new CrearUsuarioCommand("usuario", "usuario@test.com", "plainPassword");

    String hashedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyz";
    when(repositorioUsuarios.existsByUsername(any(Username.class))).thenReturn(false);
    when(repositorioUsuarios.existsByEmail(any(Email.class))).thenReturn(false);
    when(passwordEncoder.encode("plainPassword")).thenReturn(hashedPassword);
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    crearUsuarioUseCase.execute(command);

    // Assert
    verify(passwordEncoder).encode("plainPassword");
    verify(repositorioUsuarios)
        .save(argThat(usuario -> usuario.getPasswordHash().value().equals(hashedPassword)));
  }

  @Test
  @DisplayName("Debe lanzar excepción si username ya existe")
  void debeLanzarExcepcionSiUsernameYaExiste() {
    // Arrange
    CrearUsuarioCommand command =
        new CrearUsuarioCommand("usuarioExistente", "nuevo@test.com", "password123");

    when(repositorioUsuarios.existsByUsername(any(Username.class))).thenReturn(true);

    // Act & Assert
    UsernameYaExisteException exception =
        assertThrows(UsernameYaExisteException.class, () -> crearUsuarioUseCase.execute(command));

    assertNotNull(exception);
    verify(repositorioUsuarios).existsByUsername(any(Username.class));
    verify(repositorioUsuarios, never()).existsByEmail(any(Email.class));
    verify(passwordEncoder, never()).encode(anyString());
    verify(repositorioUsuarios, never()).save(any(Usuario.class));
  }

  @Test
  @DisplayName("Debe lanzar excepción si email ya está registrado")
  void debeLanzarExcepcionSiEmailYaRegistrado() {
    // Arrange
    CrearUsuarioCommand command =
        new CrearUsuarioCommand("nuevoUsuario", "existente@test.com", "password123");

    when(repositorioUsuarios.existsByUsername(any(Username.class))).thenReturn(false);
    when(repositorioUsuarios.existsByEmail(any(Email.class))).thenReturn(true);

    // Act & Assert
    EmailYaRegistradoException exception =
        assertThrows(EmailYaRegistradoException.class, () -> crearUsuarioUseCase.execute(command));

    assertNotNull(exception);
    verify(repositorioUsuarios).existsByUsername(any(Username.class));
    verify(repositorioUsuarios).existsByEmail(any(Email.class));
    verify(passwordEncoder, never()).encode(anyString());
    verify(repositorioUsuarios, never()).save(any(Usuario.class));
  }

  @Test
  @DisplayName("Debe validar formato de email antes de verificar existencia")
  void debeValidarFormatoDeEmailAntesDeVerificarExistencia() {
    // Arrange
    CrearUsuarioCommand command =
        new CrearUsuarioCommand("usuario", "email-invalido", "password123");

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> crearUsuarioUseCase.execute(command));

    assertTrue(exception.getMessage().contains("formato del email es inválido"));
    verify(repositorioUsuarios, never()).existsByUsername(any(Username.class));
    verify(repositorioUsuarios, never()).existsByEmail(any(Email.class));
  }

  @Test
  @DisplayName("Debe validar formato de username antes de verificar existencia")
  void debeValidarFormatoDeUsernameAntesDeVerificarExistencia() {
    // Arrange
    CrearUsuarioCommand command =
        new CrearUsuarioCommand(
            "ab", // Muy corto (mínimo 3)
            "test@test.com",
            "password123");

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> crearUsuarioUseCase.execute(command));

    assertTrue(exception.getMessage().contains("debe tener entre 3 y 30 caracteres"));
    verify(repositorioUsuarios, never()).existsByUsername(any(Username.class));
  }

  @Test
  @DisplayName("Debe crear usuario con valores por defecto correctos")
  void debeCrearUsuarioConValoresPorDefecto() {
    // Arrange
    CrearUsuarioCommand command =
        new CrearUsuarioCommand("usuario", "usuario@test.com", "password123");

    when(repositorioUsuarios.existsByUsername(any(Username.class))).thenReturn(false);
    when(repositorioUsuarios.existsByEmail(any(Email.class))).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash");
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    UsuarioDTO resultado = crearUsuarioUseCase.execute(command);

    // Assert
    assertEquals("PENDIENTE_DE_VERIFICACION", resultado.status());
    assertEquals("USER", resultado.role());
    assertEquals("ESP", resultado.language());
    assertTrue(resultado.notificationsActive());
  }

  @Test
  @DisplayName("Debe normalizar email a minúsculas")
  void debeNormalizarEmailAMinusculas() {
    // Arrange
    CrearUsuarioCommand command =
        new CrearUsuarioCommand("usuario", "Usuario@TEST.COM", "password123");

    when(repositorioUsuarios.existsByUsername(any(Username.class))).thenReturn(false);
    when(repositorioUsuarios.existsByEmail(any(Email.class))).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash");
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    UsuarioDTO resultado = crearUsuarioUseCase.execute(command);

    // Assert
    assertEquals("usuario@test.com", resultado.email());
  }

  @Test
  @DisplayName("Debe verificar existencia con valores normalizados")
  void debeVerificarExistenciaConValoresNormalizados() {
    // Arrange
    CrearUsuarioCommand command =
        new CrearUsuarioCommand("Usuario123", "Email@Test.COM", "password123");

    when(repositorioUsuarios.existsByUsername(any(Username.class))).thenReturn(false);
    when(repositorioUsuarios.existsByEmail(any(Email.class))).thenReturn(false);
    when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash");
    when(repositorioUsuarios.save(any(Usuario.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    crearUsuarioUseCase.execute(command);

    // Assert
    verify(repositorioUsuarios)
        .existsByUsername(
            argThat(
                username ->
                    username.value().equals("Usuario123") // Username NO se normaliza a minúsculas
                ));
    verify(repositorioUsuarios)
        .existsByEmail(
            argThat(
                email -> email.value().equals("email@test.com") // Email SÍ se normaliza
                ));
  }
}
