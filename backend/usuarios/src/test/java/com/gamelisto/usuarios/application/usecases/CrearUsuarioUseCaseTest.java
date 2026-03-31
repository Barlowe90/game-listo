package com.gamelisto.usuarios.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.CrearUsuarioCommand;
import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.application.usecases.auth.CrearUsuarioUseCase;
import com.gamelisto.usuarios.domain.exceptions.DomainException;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.repositories.IEmailService;
import com.gamelisto.usuarios.domain.usuario.*;
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

  @Mock private IEmailService emailService;

  @InjectMocks private CrearUsuarioUseCase crearUsuarioUseCase;

  @BeforeEach
  void setUp() {
    // Setup comÃºn si es necesario
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
    UsuarioResult resultado = crearUsuarioUseCase.execute(command);

    // Assert
    assertNotNull(resultado);
    assertEquals("nuevoUsuario", resultado.username());
    assertEquals("nuevo@test.com", resultado.email());
    assertEquals("PENDIENTE_DE_VERIFICACION", resultado.status());
    assertEquals("USER", resultado.role());

    verify(repositorioUsuarios).existsByUsername(any(Username.class));
    verify(repositorioUsuarios).existsByEmail(any(Email.class));
    verify(passwordEncoder).encode("password123");
    verify(repositorioUsuarios).save(any(Usuario.class));
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si username ya existe")
  void debeLanzarExcepcionSiUsernameYaExiste() {
    // Arrange
    CrearUsuarioCommand command =
        new CrearUsuarioCommand("usuarioExistente", "nuevo@test.com", "password123");

    when(repositorioUsuarios.existsByUsername(any(Username.class))).thenReturn(true);

    // Act & Assert
    ApplicationException exception =
        assertThrows(ApplicationException.class, () -> crearUsuarioUseCase.execute(command));

    assertNotNull(exception);
    verify(repositorioUsuarios).existsByUsername(any(Username.class));
    verify(repositorioUsuarios, never()).existsByEmail(any(Email.class));
    verify(passwordEncoder, never()).encode(anyString());
    verify(repositorioUsuarios, never()).save(any(Usuario.class));
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si email ya estÃ¡ registrado")
  void debeLanzarExcepcionSiEmailYaRegistrado() {
    // Arrange
    CrearUsuarioCommand command =
        new CrearUsuarioCommand("nuevoUsuario", "existente@test.com", "password123");

    when(repositorioUsuarios.existsByUsername(any(Username.class))).thenReturn(false);
    when(repositorioUsuarios.existsByEmail(any(Email.class))).thenReturn(true);

    // Act & Assert
    ApplicationException exception =
        assertThrows(ApplicationException.class, () -> crearUsuarioUseCase.execute(command));

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
    DomainException exception =
        assertThrows(DomainException.class, () -> crearUsuarioUseCase.execute(command));

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
            "ab", // Muy corto (mÃ­nimo 3)
            "test@test.com",
            "password123");

    // Act & Assert
    DomainException exception =
        assertThrows(DomainException.class, () -> crearUsuarioUseCase.execute(command));

    assertTrue(exception.getMessage().contains("debe tener entre 3 y 30 caracteres"));
    verify(repositorioUsuarios, never()).existsByUsername(any(Username.class));
  }
}



