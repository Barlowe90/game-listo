package com.gamelisto.usuarios.application.usecases;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.gamelisto.usuarios.application.dto.AuthResponseResult;
import com.gamelisto.usuarios.application.dto.RefreshTokenCommand;
import com.gamelisto.usuarios.application.exceptions.ApplicationException;
import com.gamelisto.usuarios.application.usecases.auth.RefreshTokenUseCase;
import com.gamelisto.usuarios.domain.refreshtoken.RefreshToken;
import com.gamelisto.usuarios.domain.refreshtoken.TokenHash;
import com.gamelisto.usuarios.domain.refreshtoken.TokenValue;
import com.gamelisto.usuarios.domain.repositories.RepositorioRefreshTokens;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.*;
import com.gamelisto.usuarios.shared.auth.JwtProperties;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("RefreshTokenUseCase - Tests")
class RefreshTokenUseCaseTest {

  @Mock private RepositorioUsuarios repositorioUsuarios;

  @Mock private RepositorioRefreshTokens repositorioRefreshTokens;

  @Mock private JwtProperties jwtProperties;

  @InjectMocks private RefreshTokenUseCase refreshTokenUseCase;

  private Usuario usuario;
  private TokenValue tokenValue;
  private TokenHash tokenHash;
  private RefreshToken refreshToken;

  @BeforeEach
  void setUp() {
    usuario =
        Usuario.reconstitute(
            UsuarioId.generate(),
            Username.of("testuser"),
            Email.of("test@example.com"),
            PasswordHash.of("$2a$10$hash"),
            Avatar.empty(),
            Rol.USER,
            EstadoUsuario.ACTIVO,
            DiscordUserId.empty(),
            TokenVerificacion.empty(),
            null,
            TokenVerificacion.empty(),
            null);

    tokenValue = TokenValue.generate();
    tokenHash = TokenHash.from(tokenValue);
    refreshToken = RefreshToken.create(tokenHash, usuario.getId(), Duration.ofDays(7));

    when(jwtProperties.getSecret()).thenReturn("test-secret-key-min-32-chars-long");
    when(jwtProperties.getExpirationMs()).thenReturn(900000L);
    when(jwtProperties.getRefreshExpirationMs()).thenReturn(604800000L);
  }

  // ========== CASOS DE Ã‰XITO ==========

  @Test
  @DisplayName("Debe renovar tokens exitosamente con refresh token vÃ¡lido")
  void debeRenovarTokensExitosamente() {
    // Arrange
    RefreshTokenCommand command = new RefreshTokenCommand(tokenValue.value());
    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class)))
        .thenReturn(Optional.of(refreshToken));
    when(repositorioRefreshTokens.estaRevocado(any(TokenHash.class))).thenReturn(false);
    when(repositorioUsuarios.findById(usuario.getId())).thenReturn(Optional.of(usuario));

    // Act
    AuthResponseResult response = refreshTokenUseCase.execute(command);

    // Assert
    assertNotNull(response);
    assertNotNull(response.accessToken());
    assertNotNull(response.refreshToken());
    assertNotNull(response.usuario());
    assertEquals("testuser", response.usuario().username());

    // Verificar que se revocÃ³ el token antiguo
    verify(repositorioRefreshTokens).revocar(any(TokenHash.class), any(Duration.class));

    // Verificar que se guardÃ³ el nuevo refresh token
    verify(repositorioRefreshTokens)
        .guardarActivo(any(TokenHash.class), any(UsuarioId.class), any(Instant.class));
  }

  @Test
  @DisplayName("Debe revocar el refresh token antiguo (rotation)")
  void debeRevocarRefreshTokenAntiguo() {
    // Arrange
    RefreshTokenCommand command = new RefreshTokenCommand(tokenValue.value());
    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class)))
        .thenReturn(Optional.of(refreshToken));
    when(repositorioRefreshTokens.estaRevocado(any(TokenHash.class))).thenReturn(false);
    when(repositorioUsuarios.findById(usuario.getId())).thenReturn(Optional.of(usuario));

    // Act
    refreshTokenUseCase.execute(command);

    // Assert
    verify(repositorioRefreshTokens, times(1)).revocar(any(TokenHash.class), any(Duration.class));
  }

  @Test
  @DisplayName("Debe generar nuevo refresh token diferente al antiguo")
  void debeGenerarNuevoRefreshTokenDiferente() {
    // Arrange
    RefreshTokenCommand command = new RefreshTokenCommand(tokenValue.value());
    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class)))
        .thenReturn(Optional.of(refreshToken));
    when(repositorioRefreshTokens.estaRevocado(any(TokenHash.class))).thenReturn(false);
    when(repositorioUsuarios.findById(usuario.getId())).thenReturn(Optional.of(usuario));

    // Act
    AuthResponseResult response = refreshTokenUseCase.execute(command);

    // Assert
    assertNotEquals(tokenValue.value(), response.refreshToken().token());
  }

  // ========== CASOS DE ERROR - TOKEN INVÃLIDO ==========

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el refresh token no existe")
  void debeLanzarExcepcionSiTokenNoExiste() {
    // Arrange
    RefreshTokenCommand command = new RefreshTokenCommand(tokenValue.value());
    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class))).thenReturn(Optional.empty());

    // Act & Assert
    ApplicationException exception =
        assertThrows(ApplicationException.class, () -> refreshTokenUseCase.execute(command));

    assertEquals("Refresh token inválido o revocado", exception.getMessage());
    verify(repositorioRefreshTokens, never()).revocar(any(), any());
    verify(repositorioRefreshTokens, never()).guardarActivo(any(), any(), any());
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el refresh token estÃ¡ revocado")
  void debeLanzarExcepcionSiTokenRevocado() {
    // Arrange
    RefreshTokenCommand command = new RefreshTokenCommand(tokenValue.value());
    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class)))
        .thenReturn(Optional.of(refreshToken));
    when(repositorioRefreshTokens.estaRevocado(any(TokenHash.class))).thenReturn(true);

    // Act & Assert
    ApplicationException exception =
        assertThrows(ApplicationException.class, () -> refreshTokenUseCase.execute(command));

    assertEquals("Refresh token revocado", exception.getMessage());
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el refresh token ha expirado")
  void debeLanzarExcepcionSiTokenExpirado() {
    // Arrange
    RefreshToken tokenExpirado =
        RefreshToken.reconstitute(
            tokenHash,
            usuario.getId(),
            Instant.now().minus(8, ChronoUnit.DAYS),
            Instant.now().minus(1, ChronoUnit.DAYS), // Expirado hace 1 dÃ­a
            false);

    RefreshTokenCommand command = new RefreshTokenCommand(tokenValue.value());
    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class)))
        .thenReturn(Optional.of(tokenExpirado));
    when(repositorioRefreshTokens.estaRevocado(any(TokenHash.class))).thenReturn(false);

    // Act & Assert
    ApplicationException exception =
        assertThrows(ApplicationException.class, () -> refreshTokenUseCase.execute(command));

    assertEquals("Refresh token expirado", exception.getMessage());

    // Verificar que se revocÃ³ el token expirado
    verify(repositorioRefreshTokens).revocar(any(TokenHash.class), any(Duration.class));
  }

  // ========== CASOS DE USUARIO NO ENCONTRADO ==========

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el usuario no existe")
  void debeLanzarExcepcionSiUsuarioNoExiste() {
    // Arrange
    RefreshTokenCommand command = new RefreshTokenCommand(tokenValue.value());
    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class)))
        .thenReturn(Optional.of(refreshToken));
    when(repositorioRefreshTokens.estaRevocado(any(TokenHash.class))).thenReturn(false);
    when(repositorioUsuarios.findById(usuario.getId())).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(Exception.class, () -> refreshTokenUseCase.execute(command));
  }

  // ========== VERIFICACIONES DE SEGURIDAD ==========

  @Test
  @DisplayName("Debe hashear el refresh token antes de buscarlo")
  void debeHashearTokenAntesDeBuscar() {
    // Arrange
    String tokenPlain = tokenValue.value();
    RefreshTokenCommand command = new RefreshTokenCommand(tokenPlain);
    when(repositorioRefreshTokens.buscarActivo(any(TokenHash.class)))
        .thenReturn(Optional.of(refreshToken));
    when(repositorioRefreshTokens.estaRevocado(any(TokenHash.class))).thenReturn(false);
    when(repositorioUsuarios.findById(usuario.getId())).thenReturn(Optional.of(usuario));

    // Act
    refreshTokenUseCase.execute(command);

    // Assert
    verify(repositorioRefreshTokens).buscarActivo(any(TokenHash.class));
  }
}



