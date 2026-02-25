package com.gamelisto.api_gateway.security;

import com.gamelisto.api_gateway.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Validar tokens JWT. IMPORTANTE: SOLO valida tokens, NO los genera. La generación se realiza en
 * usuarios-service.
 */
@Component
public class JwtValidator {

  private final SecretKey secretKey;

  public JwtValidator(JwtProperties jwtProperties) {
    this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Valida un token JWT y extrae sus claims.
   *
   * @param token Token JWT a validar
   * @return Claims del token si es válido
   * @throws io.jsonwebtoken.JwtException si el token no es válido
   */
  public Claims validateToken(String token) {
    return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
  }

  /** Extrae el ID de usuario del token. */
  public String getUserId(Claims claims) {
    return claims.getSubject();
  }

  /** Extrae el username del token. */
  public String getUsername(Claims claims) {
    return claims.get("username", String.class);
  }

  /** Extrae el email del token. */
  public String getEmail(Claims claims) {
    return claims.get("email", String.class);
  }

  /** Extrae los roles del token. */
  @SuppressWarnings("unchecked")
  public List<String> getRoles(Claims claims) {
    return claims.get("roles", List.class);
  }

  /** Verifica si el token ha expirado. */
  public boolean isTokenExpired(Claims claims) {
    return claims.getExpiration().before(new Date());
  }

  /** Extrae el JTI (JWT ID) para verificar revocación. */
  public String getJti(Claims claims) {
    return claims.getId();
  }
}
