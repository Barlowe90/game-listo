package com.gamelisto.social.infrastructure.in.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class GatewayAuthenticationFilter extends OncePerRequestFilter {
  private static final Logger log = LoggerFactory.getLogger(GatewayAuthenticationFilter.class);
  private static final String HEADER_USER_ID = "X-User-Id";
  private static final String HEADER_USER_ROLES = "X-User-Roles";

  @Override
  protected void doFilterInternal(
      @Nonnull HttpServletRequest request,
      @Nonnull HttpServletResponse response,
      @Nonnull FilterChain filterChain)
      throws ServletException, IOException {
    String userId = request.getHeader(HEADER_USER_ID);
    String rolesHeader = request.getHeader(HEADER_USER_ROLES);
    if (userId != null && rolesHeader != null) {
      List<SimpleGrantedAuthority> authorities =
          Arrays.stream(rolesHeader.split(","))
              .map(String::trim)
              .map(role -> "ROLE_" + role)
              .map(SimpleGrantedAuthority::new)
              .toList();
      Authentication auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
      SecurityContextHolder.getContext().setAuthentication(auth);
      log.debug("Usuario autenticado via Gateway headers: id={}, roles={}", userId, rolesHeader);
    }
    filterChain.doFilter(request, response);
  }
}
