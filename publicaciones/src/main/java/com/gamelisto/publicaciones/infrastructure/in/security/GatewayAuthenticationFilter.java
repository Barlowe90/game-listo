package com.gamelisto.publicaciones.infrastructure.in.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
 * usuario - X-User-Roles: Roles separados por comas (ej: "USER,ADMIN")
 *
 * <p>Este filtro crea un Authentication con estos datos, permitiendo que @PreAuthorize funcione
 * correctamente.
 */
@Component
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

  private static final String HEADER_USER_ID = "X-User-Id";
  private static final String HEADER_USER_ROLES = "X-User-Roles";

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    if (siYaHayAuthNoLaPises(request, response, filterChain)) return;

    String userIdHeader = request.getHeader(HEADER_USER_ID);
    String rolesHeader = request.getHeader(HEADER_USER_ROLES);

    if (siFaltaHeaderNoAutentiques(request, response, filterChain, userIdHeader, rolesHeader))
      return;

    try {

      UUID userId = UUID.fromString(userIdHeader);

      List<SimpleGrantedAuthority> authorities =
          Arrays.stream(rolesHeader.split(","))
              .map(String::trim)
              .filter(r -> !r.isBlank())
              .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
              .map(SimpleGrantedAuthority::new)
              .toList();

      Authentication authentication =
          new UsernamePasswordAuthenticationToken(userId, null, authorities);

      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (IllegalArgumentException ex) {
      SecurityContextHolder.clearContext();
    }

    filterChain.doFilter(request, response);
  }

  private static boolean siFaltaHeaderNoAutentiques(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain,
      String userIdHeader,
      String rolesHeader)
      throws IOException, ServletException {
    if (userIdHeader == null
        || userIdHeader.isBlank()
        || rolesHeader == null
        || rolesHeader.isBlank()) {
      filterChain.doFilter(request, response);
      return true;
    }
    return false;
  }

  private static boolean siYaHayAuthNoLaPises(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      filterChain.doFilter(request, response);
      return true;
    }
    return false;
  }
}
