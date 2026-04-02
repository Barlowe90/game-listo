package com.gamelisto.usuarios.shared.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

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
 * <p>El Gateway valida el JWT y envía información del usuario en headers:
 * - X-User-Id: ID del usuario
 * - X-User-Roles: Rol del usuario (un único rol, por ejemplo: "USER")
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
            @Nonnull HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull FilterChain filterChain)
            throws ServletException, IOException {

        if (siYaHayAuthNoLaPises(request, response, filterChain)) return;

        String userIdHeader = request.getHeader(HEADER_USER_ID);
        String rolesHeader = request.getHeader(HEADER_USER_ROLES);

        if (siFaltaHeaderNoAutentiques(request, response, filterChain, userIdHeader, rolesHeader))
            return;

        try {

            UUID userId = UUID.fromString(userIdHeader);
            String role = rolesHeader.trim();
            String normalizedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;

            List<SimpleGrantedAuthority> authorities =
                    role.isBlank() ? List.of() : List.of(new SimpleGrantedAuthority(normalizedRole));

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
