package com.gamelisto.publicaciones.infraestructure.in.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro que convierte los headers enviados por el API Gateway en un objeto Authentication de
 * Spring Security.
 *
 * <p>El Gateway valida el JWT y envía información del usuario en headers: - X-User-Id: ID del
 * usuario - X-User-Username: Nombre de usuario - X-User-Email: Email del usuario - X-User-Roles:
 * Roles separados por comas (ej: "USER,ADMIN")
 *
 * <p>Este filtro crea un Authentication con estos datos, permitiendo que @PreAuthorize funcione
 * correctamente.
 */
@Component
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(GatewayAuthenticationFilter.class);

  private static final String HEADER_USER_ID = "X-User-Id";
  private static final String HEADER_USER_USERNAME = "X-User-Username";
  private static final String HEADER_USER_ROLES = "X-User-Roles";

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String userId = request.getHeader(HEADER_USER_ID);
    String username = request.getHeader(HEADER_USER_USERNAME);
    String rolesHeader = request.getHeader(HEADER_USER_ROLES);

    // Si el Gateway envió los headers, crear el Authentication
    if (userId != null && rolesHeader != null) {
      List<SimpleGrantedAuthority> authorities =
          Arrays.stream(rolesHeader.split(","))
              .map(String::trim)
              .map(role -> "ROLE_" + role) // Spring Security requiere prefijo "ROLE_"
              .map(SimpleGrantedAuthority::new)
              .toList();

      // El principal es el userId (necesario para @PreAuthorize comparar con #id)
      Authentication authentication =
          new UsernamePasswordAuthenticationToken(userId, null, authorities);

      SecurityContextHolder.getContext().setAuthentication(authentication);

      log.debug(
          "Authentication creado desde headers del Gateway - UserId: {}, Username: {}, Roles: {}",
          userId,
          username,
          rolesHeader);
    } else {
      log.debug("Headers del Gateway no presentes - Petición pública o directa al servicio");
    }

    filterChain.doFilter(request, response);
  }
}
