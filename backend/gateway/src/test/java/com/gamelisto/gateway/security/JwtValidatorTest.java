package com.gamelisto.gateway.security;

import com.gamelisto.gateway.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JwtValidator - tests básicos")
class JwtValidatorTest {

    private static final String USERNAME = "username";
    private static final String EMAIL = "email";
    private static final String ROLES = "roles";

    private static final String TEST_SECRET =
            "test-secret-key-must-be-at-least-256-bits-long-for-hs256";

    private JwtValidator jwtValidator;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret(TEST_SECRET);
        properties.setExpiration(900000L); // 15 min

        jwtValidator = new JwtValidator(properties);
        secretKey = Keys.hmacShaKeyFor(TEST_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    @DisplayName("Valida un token correcto y extrae claims")
    void validaTokenYExtraeClaims() {
        String userId = UUID.randomUUID().toString();
        String username = "testuser";
        String email = "test@example.com";
        List<String> roles = List.of("USER");
        String jti = UUID.randomUUID().toString();

        String token = createValidToken(userId, username, email, roles, jti);

        Claims claims = jwtValidator.validateToken(token);

        assertThat(claims).isNotNull();
        assertThat(jwtValidator.getUserId(claims)).isEqualTo(userId);
        assertThat(jwtValidator.getRoles(claims)).containsExactlyElementsOf(roles);
        assertThat(jwtValidator.getJti(claims)).isEqualTo(jti);
        assertThat(jwtValidator.isTokenExpired(claims)).isFalse();
    }

    @Test
    @DisplayName("Rechaza un token expirado")
    void rechazaTokenExpirado() {
        String token = createExpiredToken();

        assertThatThrownBy(() -> jwtValidator.validateToken(token))
                .isInstanceOf(io.jsonwebtoken.ExpiredJwtException.class);
    }

    @Test
    @DisplayName("Rechaza un token con firma inválida")
    void rechazaTokenFirmaInvalida() {
        String token = createTokenWithInvalidSignature();

        assertThatThrownBy(() -> jwtValidator.validateToken(token))
                .isInstanceOf(io.jsonwebtoken.security.SignatureException.class);
    }

    // ===== helpers =====

    private String createValidToken(
            String userId, String username, String email, List<String> roles, String jti) {
        Instant now = Instant.now();
        Instant expiration = now.plus(15, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(userId)
                .claim(USERNAME, username)
                .claim(EMAIL, email)
                .claim(ROLES, roles)
                .id(jti)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    private String createExpiredToken() {
        Instant past = Instant.now().minus(1, ChronoUnit.HOURS);
        Instant expiration = past.plus(15, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .claim(USERNAME, "expireduser")
                .claim(EMAIL, "expired@example.com")
                .claim(ROLES, List.of("USER"))
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(past))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    private String createTokenWithInvalidSignature() {
        SecretKey wrongKey =
                Keys.hmacShaKeyFor(
                        "wrong-secret-key-must-be-at-least-256-bits-long-for-hs256"
                                .getBytes(StandardCharsets.UTF_8));

        Instant now = Instant.now();
        Instant expiration = now.plus(15, ChronoUnit.MINUTES);

        return Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .claim(USERNAME, "testuser")
                .claim(EMAIL, "test@example.com")
                .claim(ROLES, List.of("USER"))
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(wrongKey)
                .compact();
    }
}
