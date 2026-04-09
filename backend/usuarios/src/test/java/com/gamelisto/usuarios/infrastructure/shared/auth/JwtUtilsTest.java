package com.gamelisto.usuarios.infrastructure.shared.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gamelisto.usuarios.domain.refreshtoken.Jti;
import com.gamelisto.usuarios.domain.usuario.*;
import com.gamelisto.usuarios.shared.auth.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("JwtUtils - GeneraciÃ³n de tokens JWT")
class JwtUtilsTest {

    private String secret;
    private long expirationMs;
    private Usuario usuario;
    private Jti jti;

    @BeforeEach
    void setUp() {
        secret = "test-secret-key-must-be-at-least-256-bits-long-for-hs256";
        expirationMs = 900000L; // 15 minutos

        // Crear usuario de prueba
        usuario =
                Usuario.create(
                        Username.of("testuser"),
                        Email.of("test@example.com"),
                        PasswordHash.of("$2a$10$hashedPassword"));

        jti = Jti.generate();
    }

    @Test
    @DisplayName("Debe generar token JWT vÃ¡lido con claims correctos")
    void debeGenerarTokenJwtValidoConClaimsCorrectos() {
        // Act
        String token = JwtUtils.generateAccessToken(usuario, jti, secret, expirationMs);

        // Assert
        assertThat(token).isNotNull().isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // Header.Payload.Signature

        // Verificar claims parseando el token
        Claims claims = JwtUtils.parseToken(token, secret);
        assertThat(claims.getSubject()).isEqualTo(usuario.getId().value().toString());
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles");
        assertThat(roles).containsExactly("USER");
        assertThat(claims.get("jti", String.class)).isEqualTo(jti.value());
    }

    @Test
    @DisplayName("Token generado tiene firma vÃ¡lida HMAC-SHA256")
    void tokenGeneradoTieneFirmaValida() {
        // Act
        String token = JwtUtils.generateAccessToken(usuario, jti, secret, expirationMs);

        // Assert - debe parsear sin excepciÃ³n si la firma es vÃ¡lida
        Claims claims = JwtUtils.parseToken(token, secret);
        assertThat(claims).isNotNull();
    }

    @Test
    @DisplayName("Token contiene expiraciÃ³n correcta (15 minutos por defecto)")
    void tokenContieneExpiracionCorrecta() {
        // Arrange
        Instant beforeGeneration = Instant.now();

        // Act
        String token = JwtUtils.generateAccessToken(usuario, jti, secret, expirationMs);
        Claims claims = JwtUtils.parseToken(token, secret);

        // Assert
        Instant tokenIssuedAt = claims.getIssuedAt().toInstant();
        Instant tokenExpiresAt = claims.getExpiration().toInstant();

        // JWT pierde precisiÃ³n de milisegundos, comparar con truncamiento a segundos
        assertThat(tokenIssuedAt.truncatedTo(java.time.temporal.ChronoUnit.SECONDS))
                .isBeforeOrEqualTo(beforeGeneration.plusSeconds(1));
        assertThat(tokenExpiresAt).isAfter(tokenIssuedAt);

        long actualExpirationMs = tokenExpiresAt.toEpochMilli() - tokenIssuedAt.toEpochMilli();
        assertThat(actualExpirationMs).isEqualTo(expirationMs);
    }

    @Test
    @DisplayName("Genera JTI Ãºnico para cada token")
    void generaJtiUnicoParaCadaToken() {
        // Arrange
        Jti jti1 = Jti.generate();
        Jti jti2 = Jti.generate();

        // Act
        String token1 = JwtUtils.generateAccessToken(usuario, jti1, secret, expirationMs);
        String token2 = JwtUtils.generateAccessToken(usuario, jti2, secret, expirationMs);

        // Assert
        String jtiFromToken1 = JwtUtils.extractJti(token1, secret);
        String jtiFromToken2 = JwtUtils.extractJti(token2, secret);

        assertThat(jtiFromToken1).isNotEqualTo(jtiFromToken2).isEqualTo(jti1.value());
        assertThat(jtiFromToken2).isEqualTo(jti2.value());
    }

    @Test
    @DisplayName("Valida formato de secret (mÃ­nimo 256 bits)")
    void validaFormatoDeSecretMinimo256Bits() {
        // Arrange
        String shortSecret = "short"; // Solo 40 bits (5 bytes * 8)

        // Act & Assert - JJWT lanza excepciÃ³n si el secret es muy corto
        assertThatThrownBy(() -> JwtUtils.generateAccessToken(usuario, jti, shortSecret, expirationMs))
                .isInstanceOf(io.jsonwebtoken.security.WeakKeyException.class);
    }

    @Test
    @DisplayName("Genera access token con duraciÃ³n corta configurable")
    void generaAccessTokenConDuracionCortaConfigurable() {
        // Arrange
        long shortExpirationMs = 60000L; // 1 minuto

        // Act
        String token = JwtUtils.generateAccessToken(usuario, jti, secret, shortExpirationMs);
        Claims claims = JwtUtils.parseToken(token, secret);

        // Assert
        long actualExpiration =
                claims.getExpiration().toInstant().toEpochMilli()
                        - claims.getIssuedAt().toInstant().toEpochMilli();
        assertThat(actualExpiration).isEqualTo(shortExpirationMs);
    }

    @Test
    @DisplayName("Claims son parseables despuÃ©s de generaciÃ³n")
    void claimsSonParseablesDespuesDeGeneracion() {
        // Act
        String token = JwtUtils.generateAccessToken(usuario, jti, secret, expirationMs);

        // Assert - debe extraer todos los claims sin error
        String userId = JwtUtils.extractUserId(token, secret);
        String jtiValue = JwtUtils.extractJti(token, secret);

        assertThat(userId).isEqualTo(usuario.getId().value().toString());
        assertThat(jtiValue).isEqualTo(jti.value());
    }

    @Test
    @DisplayName("Debe lanzar excepciÃ³n con token expirado")
    void debeLanzarExcepcionConTokenExpirado() {
        // Arrange - token con expiraciÃ³n de -1 segundo (ya expirado)
        long expiredExpirationMs = -1000L;

        // Act
        String token = JwtUtils.generateAccessToken(usuario, jti, secret, expiredExpirationMs);

        // Assert - parsear token expirado debe lanzar ExpiredJwtException
        assertThatThrownBy(() -> JwtUtils.parseToken(token, secret))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("Debe verificar correctamente si token ha expirado")
    void debeVerificarCorrectamenteSiTokenHaExpirado() {
        // Arrange - token vÃ¡lido por 5 segundos
        long validExpiration = 5000L;
        String validToken = JwtUtils.generateAccessToken(usuario, jti, secret, validExpiration);

        // Assert - token vÃ¡lido no debe estar expirado
        boolean isExpiredValid = JwtUtils.isTokenExpired(validToken, secret);
        assertThat(isExpiredValid).isFalse();

        // Arrange - token ya expirado (expiraciÃ³n negativa)
        long expiredExpiration = -1000L;
        String expiredToken =
                JwtUtils.generateAccessToken(usuario, Jti.generate(), secret, expiredExpiration);

        // Assert - token expirado debe lanzar excepciÃ³n al verificar
        assertThatThrownBy(() -> JwtUtils.isTokenExpired(expiredToken, secret))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    @DisplayName("Debe lanzar excepciÃ³n con firma invÃ¡lida")
    void debeLanzarExcepcionConFirmaInvalida() {
        // Arrange
        String wrongSecret = "wrong-secret-key-different-from-configured-secret-256bits";
        String token = JwtUtils.generateAccessToken(usuario, jti, secret, expirationMs);

        // Act & Assert - parsear con secreto diferente debe fallar
        assertThatThrownBy(() -> JwtUtils.parseToken(token, wrongSecret))
                .isInstanceOf(SignatureException.class);
    }

    @Test
    @DisplayName("Debe incluir rol USER en claims correctamente")
    void debeIncluirRolUserEnClaimsCorrectamente() {
        // Arrange
        Usuario userWithUserRole =
                Usuario.create(
                        Username.of("user1"), Email.of("user1@example.com"), PasswordHash.of("$2a$10$hash"));

        // Act
        String token = JwtUtils.generateAccessToken(userWithUserRole, jti, secret, expirationMs);
        Claims claims = JwtUtils.parseToken(token, secret);

        // Assert
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);
        assertThat(roles).containsExactly("USER");
    }

    @Test
    @DisplayName("Debe incluir rol ADMIN en claims correctamente")
    void debeIncluirRolAdminEnClaimsCorrectamente() {
        // Arrange - crear usuario y luego reconstituir con rol ADMIN
        Usuario baseUser =
                Usuario.create(
                        Username.of("admin1"), Email.of("admin1@example.com"), PasswordHash.of("$2a$10$hash"));

        Usuario admin =
                Usuario.reconstitute(
                        baseUser.getId(),
                        baseUser.getUsername(),
                        baseUser.getEmail(),
                        baseUser.getPasswordHash(),
                        baseUser.getAvatar(),
                        Rol.ADMIN, // Cambiar a ADMIN
                        baseUser.getStatus(),
                        baseUser.getDiscordUserId(),
                        baseUser.getTokenVerificacion(),
                        baseUser.getTokenVerificacionExpiracion(),
                        baseUser.getTokenRestablecimiento(),
                        baseUser.getTokenRestablecimientoExpiracion());

        // Act
        String token = JwtUtils.generateAccessToken(admin, jti, secret, expirationMs);
        Claims claims = JwtUtils.parseToken(token, secret);

        // Assert
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);
        assertThat(roles).containsExactly("ADMIN");
    }

    @Test
    @DisplayName("Debe extraer userId (subject) correctamente")
    void debeExtraerUserIdCorrectamente() {
        // Act
        String token = JwtUtils.generateAccessToken(usuario, jti, secret, expirationMs);
        String extractedUserId = JwtUtils.extractUserId(token, secret);

        // Assert
        assertThat(extractedUserId).isEqualTo(usuario.getId().value().toString());
    }

    @Test
    @DisplayName("Debe extraer jti correctamente")
    void debeExtraerJtiCorrectamente() {
        // Act
        String token = JwtUtils.generateAccessToken(usuario, jti, secret, expirationMs);
        String extractedJti = JwtUtils.extractJti(token, secret);

        // Assert
        assertThat(extractedJti).isEqualTo(jti.value());
    }

    @Test
    @DisplayName("Token debe contener claim 'iat' (issued at)")
    void tokenDebeContenerClaimIat() {
        // Act
        String token = JwtUtils.generateAccessToken(usuario, jti, secret, expirationMs);
        Claims claims = JwtUtils.parseToken(token, secret);

        // Assert
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getIssuedAt().toInstant()).isBefore(Instant.now().plusSeconds(5));
    }

    @Test
    @DisplayName("Token debe contener claim 'exp' (expiration)")
    void tokenDebeContenerClaimExp() {
        // Act
        String token = JwtUtils.generateAccessToken(usuario, jti, secret, expirationMs);
        Claims claims = JwtUtils.parseToken(token, secret);

        // Assert
        assertThat(claims.getExpiration()).isNotNull();
        assertThat(claims.getExpiration().toInstant()).isAfter(Instant.now());
    }
}



