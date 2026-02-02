package com.gamelisto.usuarios_service.infrastructure.auth;

import com.gamelisto.usuarios_service.domain.refreshtoken.Jti;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class JwtUtils {

  private JwtUtils() {
    // Constructor privado para evitar instanciación
  }

  public static String generateAccessToken(
      Usuario usuario, Jti jti, String secret, long expirationMs) {

    Instant now = Instant.now();
    Instant expiresAt = now.plusMillis(expirationMs);

    // Construir claims
    Map<String, Object> claims = new HashMap<>();
    claims.put("username", usuario.getUsername().value());
    claims.put("email", usuario.getEmail().value());
    claims.put("roles", List.of(usuario.getRole().name()));
    claims.put("jti", jti.value());

    // Generar clave de firma HS256
    SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(usuario.getId().value().toString())
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(expiresAt))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * Parsea un token JWT y extrae sus claims.
   *
   * <p>OJOOOOO. NO se usa en producción. La validación de tokens es responsabilidad del API
   * Gateway. Solo se incluye para tests.
   *
   * @param token Token JWT a parsear
   * @param secret Clave secreta usada para firmar
   * @return Claims del token
   * @throws io.jsonwebtoken.JwtException si el token es inválido
   */
  public static Claims parseToken(String token, String secret) {
    SecretKey key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  /** Extrae el userId (claim 'sub') de un token JWT. */
  public static String extractUserId(String token, String secret) {
    Claims claims = parseToken(token, secret);
    return claims.getSubject();
  }

  /** Extrae el jti (JWT ID) de un token JWT. */
  public static String extractJti(String token, String secret) {
    Claims claims = parseToken(token, secret);
    return claims.get("jti", String.class);
  }

  /** Verifica si un token ha expirado. */
  public static boolean isTokenExpired(String token, String secret) {
    Claims claims = parseToken(token, secret);
    return claims.getExpiration().before(new Date());
  }
}
