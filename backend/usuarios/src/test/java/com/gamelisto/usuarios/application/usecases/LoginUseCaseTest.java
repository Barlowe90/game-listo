package com.gamelisto.usuarios.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.AuthResponseResult;
import com.gamelisto.usuarios.application.dto.LoginCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.application.usecases.auth.LoginUseCase;
import com.gamelisto.usuarios.domain.refreshtoken.TokenHash;
import com.gamelisto.usuarios.domain.repositories.RepositorioRefreshTokens;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.*;
import com.gamelisto.usuarios.shared.auth.JwtProperties;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("LoginUseCase - Tests")
class LoginUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;

  @Mock private RepositorioRefreshTokens repositorioRefreshTokens;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtProperties jwtProperties;

  @InjectMocks private LoginUseCase loginUseCase;

  private Usuario usuarioActivo;
  private final String passwordPlain = "SecureP@ssw0rd";
  private final String passwordHash = "$2a$10$hashedPassword";

  @BeforeEach
  void setUp() {
    usuarioActivo =
        Usuario.reconstitute(
            UsuarioId.generate(),
            Username.of("testuser"),
            Email.of("test@example.com"),
            PasswordHash.of(passwordHash),
            Avatar.empty(),
            Rol.USER,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null);

    when(jwtProperties.getSecret()).thenReturn("test-secret-key-min-32-chars-long");
    when(jwtProperties.getExpirationMs()).thenReturn(900000L); // 15 min
    when(jwtProperties.getRefreshExpirationMs()).thenReturn(604800000L); // 7 dÃ­as
  }

  // ========== CASOS DE Ã‰XITO ==========

  @Test
  @DisplayName("Debe realizar login exitosamente con credenciales vÃ¡lidas")
  void debeRealizarLoginExitoso() {
    // Arrange
    LoginCommand command = new LoginCommand("test@example.com", passwordPlain);
    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuarioActivo));
    when(passwordEncoder.matches(passwordPlain, passwordHash)).thenReturn(true);

    // Act
    AuthResponseResult response = loginUseCase.execute(command);

    // Assert
    assertNotNull(response);
    assertNotNull(response.accessToken());
    assertNotNull(response.refreshToken());
    assertNotNull(response.usuario());
    assertEquals("testuser", response.usuario().username());
    assertEquals("test@example.com", response.usuario().email());

    // Verificar que se guardÃ³ el refresh token en Redis
    verify(repositorioRefreshTokens)
        .guardarActivo(any(TokenHash.class), any(UsuarioId.class), any(Instant.class));
  }

  @Test
  @DisplayName("Debe generar access token con tiempo de expiraciÃ³n correcto")
  void debeGenerarAccessTokenConExpiracionCorrecta() {
    // Arrange
    LoginCommand command = new LoginCommand("test@example.com", passwordPlain);
    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuarioActivo));
    when(passwordEncoder.matches(passwordPlain, passwordHash)).thenReturn(true);

    // Act
    Instant antesDeLogin = Instant.now();
    AuthResponseResult response = loginUseCase.execute(command);
    Instant despuesDeLogin = Instant.now();

    // Assert
    Instant accessTokenExpiration = response.accessToken().expiresAt();
    long expectedExpirationMs = jwtProperties.getExpirationMs();

    // Verificar que la expiraciÃ³n estÃ¡ en el rango esperado (Â±5 segundos de margen)
    assertTrue(
        accessTokenExpiration.isAfter(antesDeLogin.plusMillis(expectedExpirationMs - 5000))
            && accessTokenExpiration.isBefore(
                despuesDeLogin.plusMillis(expectedExpirationMs + 5000)));
  }

  @Test
  @DisplayName("Debe generar refresh token con tiempo de expiraciÃ³n correcto")
  void debeGenerarRefreshTokenConExpiracionCorrecta() {
    // Arrange
    LoginCommand command = new LoginCommand("test@example.com", passwordPlain);
    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuarioActivo));
    when(passwordEncoder.matches(passwordPlain, passwordHash)).thenReturn(true);

    // Act
    Instant antesDeLogin = Instant.now();
    AuthResponseResult response = loginUseCase.execute(command);
    Instant despuesDeLogin = Instant.now();

    // Assert
    Instant refreshTokenExpiration = response.refreshToken().expiresAt();
    long expectedExpirationMs = jwtProperties.getRefreshExpirationMs();

    assertTrue(
        refreshTokenExpiration.isAfter(antesDeLogin.plusMillis(expectedExpirationMs - 5000))
            && refreshTokenExpiration.isBefore(
                despuesDeLogin.plusMillis(expectedExpirationMs + 5000)));
  }

  // ========== CASOS DE ERROR - CREDENCIALES INVÃLIDAS ==========

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el email no estÃ¡ registrado")
  void debeLanzarExcepcionSiEmailNoRegistrado() {
    // Arrange
    LoginCommand command = new LoginCommand("noexiste@example.com", passwordPlain);
    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.empty());

    // Act & Assert
    ApplicationException exception =
        assertThrows(ApplicationException.class, () -> loginUseCase.execute(command));

    assertEquals("Email o contraseña incorrectos", exception.getMessage());
    verify(passwordEncoder, never()).matches(anyString(), anyString());
    verify(repositorioRefreshTokens, never()).guardarActivo(any(), any(), any());
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si la contraseÃ±a es incorrecta")
  void debeLanzarExcepcionSiContrasenaIncorrecta() {
    // Arrange
    LoginCommand command = new LoginCommand("test@example.com", "WrongPassword");
    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuarioActivo));
    when(passwordEncoder.matches("WrongPassword", passwordHash)).thenReturn(false);

    // Act & Assert
    ApplicationException exception =
        assertThrows(ApplicationException.class, () -> loginUseCase.execute(command));

    assertEquals("Email o contraseña incorrectos", exception.getMessage());
    verify(repositorioRefreshTokens, never()).guardarActivo(any(), any(), any());
  }

  // ========== CASOS DE ERROR - ESTADO DEL USUARIO ==========

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el usuario estÃ¡ PENDIENTE_DE_VERIFICACION")
  void debeLanzarExcepcionSiUsuarioPendienteVerificacion() {
    // Arrange
    Usuario usuarioPendiente =
        Usuario.reconstitute(
            UsuarioId.generate(),
            Username.of("pending"),
            Email.of("pending@example.com"),
            PasswordHash.of(passwordHash),
            Avatar.empty(),
            Rol.USER,
            EstadoUsuario.PENDIENTE_DE_VERIFICACION,
            DiscordUserId.empty(),
            TokenVerificacion.generate(),
            Instant.now().plus(1, ChronoUnit.DAYS),
            TokenVerificacion.empty(),
            null);

    LoginCommand command = new LoginCommand("pending@example.com", passwordPlain);
    when(repositorioUsuarios.findByEmail(any(Email.class)))
        .thenReturn(Optional.of(usuarioPendiente));
    when(passwordEncoder.matches(passwordPlain, passwordHash)).thenReturn(true);

    // Act & Assert
    ApplicationException exception =
        assertThrows(ApplicationException.class, () -> loginUseCase.execute(command));

    assertTrue(exception.getMessage().contains("Usuario no activo"));
    assertTrue(exception.getMessage().contains("PENDIENTE_DE_VERIFICACION"));
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el usuario estÃ¡ SUSPENDIDO")
  void debeLanzarExcepcionSiUsuarioSuspendido() {
    // Arrange
    Usuario usuarioSuspendido =
        Usuario.reconstitute(
            UsuarioId.generate(),
            Username.of("suspended"),
            Email.of("suspended@example.com"),
            PasswordHash.of(passwordHash),
            Avatar.empty(),
            Rol.USER,
            EstadoUsuario.SUSPENDIDO,
            DiscordUserId.empty(),
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null);

    LoginCommand command = new LoginCommand("suspended@example.com", passwordPlain);
    when(repositorioUsuarios.findByEmail(any(Email.class)))
        .thenReturn(Optional.of(usuarioSuspendido));
    when(passwordEncoder.matches(passwordPlain, passwordHash)).thenReturn(true);

    // Act & Assert
    ApplicationException exception =
        assertThrows(ApplicationException.class, () -> loginUseCase.execute(command));

    assertTrue(exception.getMessage().contains("SUSPENDIDO"));
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el usuario estÃ¡ ELIMINADO")
  void debeLanzarExcepcionSiUsuarioEliminado() {
    // Arrange
    Usuario usuarioEliminado =
        Usuario.reconstitute(
            UsuarioId.generate(),
            Username.of("deleted"),
            Email.of("deleted@example.com"),
            PasswordHash.of(passwordHash),
            Avatar.empty(),
            Rol.USER,
            EstadoUsuario.ELIMINADO,
            DiscordUserId.empty(),
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null);

    LoginCommand command = new LoginCommand("deleted@example.com", passwordPlain);
    when(repositorioUsuarios.findByEmail(any(Email.class)))
        .thenReturn(Optional.of(usuarioEliminado));
    when(passwordEncoder.matches(passwordPlain, passwordHash)).thenReturn(true);

    // Act & Assert
    ApplicationException exception =
        assertThrows(ApplicationException.class, () -> loginUseCase.execute(command));

    assertTrue(exception.getMessage().contains("ELIMINADO"));
  }

  // ========== VERIFICACIONES DE TOKENS ==========

  @Test
  @DisplayName("Debe guardar el refresh token hasheado en Redis")
  void debeGuardarRefreshTokenHasheadoEnRedis() {
    // Arrange
    LoginCommand command = new LoginCommand("test@example.com", passwordPlain);
    when(repositorioUsuarios.findByEmail(any(Email.class))).thenReturn(Optional.of(usuarioActivo));
    when(passwordEncoder.matches(passwordPlain, passwordHash)).thenReturn(true);

    ArgumentCaptor<TokenHash> tokenHashCaptor = ArgumentCaptor.forClass(TokenHash.class);
    ArgumentCaptor<UsuarioId> usuarioIdCaptor = ArgumentCaptor.forClass(UsuarioId.class);
    ArgumentCaptor<Instant> expiresAtCaptor = ArgumentCaptor.forClass(Instant.class);

    // Act
    loginUseCase.execute(command);

    // Assert
    verify(repositorioRefreshTokens)
        .guardarActivo(
            tokenHashCaptor.capture(), usuarioIdCaptor.capture(), expiresAtCaptor.capture());

    assertNotNull(tokenHashCaptor.getValue());
    assertEquals(usuarioActivo.getId(), usuarioIdCaptor.getValue());
    assertNotNull(expiresAtCaptor.getValue());
  }
}



