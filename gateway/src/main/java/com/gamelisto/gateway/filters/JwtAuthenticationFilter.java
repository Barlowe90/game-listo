package com.gamelisto.gateway.filters;

import com.gamelisto.gateway.security.JwtValidator;
import com.gamelisto.gateway.security.TokenRevocationService;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Filtro global que valida tokens JWT en todas las peticiones. Se ejecuta antes de enrutar la
 * petición a los microservicios.
 *
 * <p>Responsabilidades: 1. Extraer el token JWT del header Authorization 2. Validar firma,
 * expiración y claims del token 3. Verificar que el token no ha sido revocado (blacklist en Redis)
 * 4. Agregar claims del usuario a los headers para los microservicios 5. Permitir paso libre a
 * rutas públicas
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

  private static final String BEARER_PREFIX = "Bearer ";
  private static final int BEARER_PREFIX_LENGTH = 7;

  // Rutas que NO requieren autenticación
  private static final List<String> PUBLIC_PATHS =
      List.of(
          // AuthController - Endpoints públicos de autenticación
          "/v1/usuarios/auth/register",
          "/v1/usuarios/auth/verify-email",
          "/v1/usuarios/auth/resend-verification",
          "/v1/usuarios/auth/forgot-password",
          "/v1/usuarios/auth/reset-password",
          "/v1/usuarios/auth/login",
          "/v1/usuarios/auth/logout",
          "/v1/usuarios/auth/refresh",
          "/actuator/health",
          // Catalogo (desarrollo) - permitir acceso sin JWT temporalmente
          "/v1/catalogo");

  private final JwtValidator jwtValidator;
  private final TokenRevocationService tokenRevocationService;

  public JwtAuthenticationFilter(
      JwtValidator jwtValidator, TokenRevocationService tokenRevocationService) {
    this.jwtValidator = jwtValidator;
    this.tokenRevocationService = tokenRevocationService;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String path = exchange.getRequest().getURI().getPath();

    // Permitir rutas públicas sin validación
    if (isPublicPath(path)) {
      logger.debug("Ruta pública detectada: {}", path);
      return chain.filter(exchange);
    }

    // Extraer token del header Authorization
    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
      logger.warn("Token JWT ausente o formato inválido en ruta protegida: {}", path);
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }

    String token = authHeader.substring(BEARER_PREFIX_LENGTH);

    try {
      // Validar token y extraer claims
      Claims claims = jwtValidator.validateToken(token);

      // Verificar si el token ha sido revocado
      String jti = jwtValidator.getJti(claims);

      return tokenRevocationService
          .isTokenRevoked(jti)
          .flatMap(
              isRevoked -> {
                if (Boolean.TRUE.equals(isRevoked)) {
                  logger.warn("Token revocado detectado. JTI: {}", jti);
                  exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                  return exchange.getResponse().setComplete();
                }

                // Token válido: agregar información del usuario a los headers
                ServerHttpRequest mutatedRequest =
                    exchange
                        .getRequest()
                        .mutate()
                        .header("X-User-Id", jwtValidator.getUserId(claims))
                        .header("X-User-Username", jwtValidator.getUsername(claims))
                        .header("X-User-Email", jwtValidator.getEmail(claims))
                        .header("X-User-Roles", String.join(",", jwtValidator.getRoles(claims)))
                        .build();

                logger.debug(
                    "Token JWT validado correctamente para usuario: {}",
                    jwtValidator.getUsername(claims));

                return chain.filter(exchange.mutate().request(mutatedRequest).build());
              });

    } catch (Exception e) {
      logger.error("Error validando token JWT: {}", e.getMessage());
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }
  }

  private boolean isPublicPath(String path) {
    return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
  }

  @Override
  public int getOrder() {
    // Ejecutar antes que otros filtros
    return -100;
  }
}
