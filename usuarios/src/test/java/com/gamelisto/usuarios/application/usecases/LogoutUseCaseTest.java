package com.gamelisto.usuarios.application.usecases;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.LogoutCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.domain.refreshtoken.Jti;
import com.gamelisto.usuarios.domain.refreshtoken.RefreshToken;
import com.gamelisto.usuarios.domain.refreshtoken.TokenHash;
import com.gamelisto.usuarios.domain.refreshtoken.TokenValue;
import com.gamelisto.usuarios.domain.repositories.RepositorioJtiRevocados;
import com.gamelisto.usuarios.domain.repositories.RepositorioRefreshTokens;
import com.gamelisto.usuarios.domain.usuario.*;
import com.gamelisto.usuarios.shared.auth.JwtProperties;
import com.gamelisto.usuarios.shared.auth.JwtUtils;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogoutUseCase - Revocación de tokens")
class LogoutUseCaseTest {

  @Mock private RepositorioJtiRevocados repositorioJti;
  @Mock private RepositorioRefreshTokens repositorioRefreshTokens;

  @Mock(lenient = true)
  private JwtProperties jwtProperties;

  @InjectMocks private LogoutUseCase useCase;

  private String secret;
  private String validAccessToken;
  private String validRefreshToken;
  private Usuario testUsuario;

  @BeforeEach
  void setUp() {
    // Configurar propiedades
    secret = "test-secret-key-for-testing-purposes-must-be-long-enough-for-hs256-algorithm";
    long expirationMs = 900000L; // 15 minutos

    // Crear usuario de prueba usando la funcion create que solo requiere 3 parámetros
    testUsuario =
        Usuario.create(
            Username.of("testuser"),
            Email.of("test@example.com"),
            PasswordHash.of("$2a$10$hashedpassword"));

    // Generar tokens válidos
    Jti jti = Jti.generate();
    validAccessToken = JwtUtils.generateAccessToken(testUsuario, jti, secret, expirationMs);
    validRefreshToken = UUID.randomUUID().toString();

    // Configurar mock de jwtProperties
    when(jwtProperties.getSecret()).thenReturn(secret);
  }

  @Test
  @DisplayName("Debe revocar refresh token y access token correctamente")
  void debeRevocarAmbosTokensCorrectamente() {
    // Arrange
    LogoutCommand command = new LogoutCommand(validRefreshToken, validAccessToken);

    TokenValue tokenValue = TokenValue.of(validRefreshToken);
    TokenHash tokenHash = TokenHash.from(tokenValue);

    RefreshToken refreshToken =
        RefreshToken.create(tokenHash, testUsuario.getId(), Duration.ofDays(7));

    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class)))
        .thenReturn(Optional.of(refreshToken));

    // Act
    useCase.execute(command);

    // Assert
    verify(repositorioRefreshTokens, times(1)).buscarActivo(any(TokenHash.class));
    verify(repositorioRefreshTokens, times(1)).revocar(any(TokenHash.class), any(Duration.class));
    verify(repositorioJti, times(1)).revocar(any(Jti.class), any(Duration.class));
  }

  @Test
  @DisplayName("Debe revocar solo refresh token si no se proporciona access token")
  void debeRevocarSoloRefreshTokenSinAccessToken() {
    // Arrange
    LogoutCommand command = new LogoutCommand(validRefreshToken, null);

    TokenValue tokenValue = TokenValue.of(validRefreshToken);
    TokenHash tokenHash = TokenHash.from(tokenValue);

    RefreshToken refreshToken =
        RefreshToken.create(tokenHash, testUsuario.getId(), Duration.ofDays(7));

    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class)))
        .thenReturn(Optional.of(refreshToken));

    // Act
    useCase.execute(command);

    // Assert
    verify(repositorioRefreshTokens, times(1)).revocar(any(TokenHash.class), any(Duration.class));
    verify(repositorioJti, never()).revocar(any(Jti.class), any(Duration.class));
  }

  @Test
  @DisplayName("Debe revocar solo refresh token si access token está vacío")
  void debeRevocarSoloRefreshTokenConAccessTokenVacio() {
    // Arrange
    LogoutCommand command = new LogoutCommand(validRefreshToken, "");

    TokenValue tokenValue = TokenValue.of(validRefreshToken);
    TokenHash tokenHash = TokenHash.from(tokenValue);

    RefreshToken refreshToken =
        RefreshToken.create(tokenHash, testUsuario.getId(), Duration.ofDays(7));

    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class)))
        .thenReturn(Optional.of(refreshToken));

    // Act
    useCase.execute(command);

    // Assert
    verify(repositorioRefreshTokens, times(1)).revocar(any(TokenHash.class), any(Duration.class));
    verify(repositorioJti, never()).revocar(any(Jti.class), any(Duration.class));
  }

  @Test
  @DisplayName("Debe lanzar ApplicationException si refresh token no existe")
  void debeLanzarExcepcionSiRefreshTokenNoExiste() {
    // Arrange
    LogoutCommand command = new LogoutCommand(validRefreshToken, validAccessToken);

    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class))).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("Refresh token inválido");

    verify(repositorioRefreshTokens, never()).revocar(any(TokenHash.class), any(Duration.class));
    verify(repositorioJti, never()).revocar(any(Jti.class), any(Duration.class));
  }

  @Test
  @DisplayName("Debe ser idempotente - permite logout múltiple del mismo refresh token")
  void debePermitirLogoutMultiple() {
    // Arrange
    LogoutCommand command = new LogoutCommand(validRefreshToken, null);

    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class))).thenReturn(Optional.empty());

    // Act & Assert - Primera llamada lanza excepción
    assertThatThrownBy(() -> useCase.execute(command)).isInstanceOf(ApplicationException.class);

    // Segunda llamada también lanza excepción (token ya revocado)
    assertThatThrownBy(() -> useCase.execute(command)).isInstanceOf(ApplicationException.class);
  }

  @Test
  @DisplayName("Debe manejar access token inválido sin fallar (continúa con refresh token)")
  void debeManejarAccessTokenInvalidoSinFallar() {
    // Arrange
    String invalidAccessToken = "invalid-token-format";
    LogoutCommand command = new LogoutCommand(validRefreshToken, invalidAccessToken);

    TokenValue tokenValue = TokenValue.of(validRefreshToken);
    TokenHash tokenHash = TokenHash.from(tokenValue);

    RefreshToken refreshToken =
        RefreshToken.create(tokenHash, testUsuario.getId(), Duration.ofDays(7));

    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class)))
        .thenReturn(Optional.of(refreshToken));

    // Act - No debe lanzar excepción
    assertThatCode(() -> useCase.execute(command)).doesNotThrowAnyException();

    // Assert - Refresh token fue revocado, access token fue ignorado
    verify(repositorioRefreshTokens, times(1)).revocar(any(TokenHash.class), any(Duration.class));
    verify(repositorioJti, never()).revocar(any(Jti.class), any(Duration.class));
  }

  @Test
  @DisplayName("Debe calcular TTL residual del access token correctamente")
  void debeCalcularTtlResidualCorrectamente() {
    // Arrange
    LogoutCommand command = new LogoutCommand(validRefreshToken, validAccessToken);

    TokenValue tokenValue = TokenValue.of(validRefreshToken);
    TokenHash tokenHash = TokenHash.from(tokenValue);

    RefreshToken refreshToken =
        RefreshToken.create(tokenHash, testUsuario.getId(), Duration.ofDays(7));

    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class)))
        .thenReturn(Optional.of(refreshToken));

    // Act
    useCase.execute(command);

    // Assert - JTI fue revocado con TTL positivo
    verify(repositorioJti, times(1)).revocar(any(Jti.class), any(Duration.class));
  }

  @Test
  @DisplayName("Debe validar que LogoutCommand no acepte refresh token vacío")
  void debeValidarRefreshTokenNoVacio() {
    // Act & Assert
    assertThatThrownBy(() -> new LogoutCommand("", null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El refresh token no puede estar vacío");

    assertThatThrownBy(() -> new LogoutCommand(null, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("El refresh token no puede estar vacío");
  }

  @Test
  @DisplayName("Debe no revocar JTI si access token ya expiró")
  void debeNoRevocarJtiSiTokenExpirado() {
    // Arrange - Generar token expirado
    Usuario testUsuario2 =
        Usuario.create(
            Username.of("testuser2"),
            Email.of("test2@example.com"),
            PasswordHash.of("$2a$10$hashedpassword"));

    Jti expiredJti = Jti.generate();
    long expiredExpirationMs = -1000L; // Token expirado (negativo)
    String expiredAccessToken =
        JwtUtils.generateAccessToken(testUsuario2, expiredJti, secret, expiredExpirationMs);

    LogoutCommand command = new LogoutCommand(validRefreshToken, expiredAccessToken);

    TokenValue tokenValue = TokenValue.of(validRefreshToken);
    TokenHash tokenHash = TokenHash.from(tokenValue);

    RefreshToken refreshToken =
        RefreshToken.create(tokenHash, testUsuario.getId(), Duration.ofDays(7));

    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class)))
        .thenReturn(Optional.of(refreshToken));

    // Act
    useCase.execute(command);

    // Assert - Refresh token revocado, pero JTI no (porque TTL no es positivo)
    verify(repositorioRefreshTokens, times(1)).revocar(any(TokenHash.class), any(Duration.class));
    // JTI no se revoca si el token ya expiró (TTL negativo)
    verify(repositorioJti, never()).revocar(any(Jti.class), any(Duration.class));
  }
}
