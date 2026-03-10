package com.gamelisto.usuarios.shared.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@DisplayName("GatewayAuthenticationFilter - Procesamiento de headers del Gateway")
class GatewayAuthenticationFilterTest {

  @InjectMocks private GatewayAuthenticationFilter filter;

  @Mock private FilterChain filterChain;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @BeforeEach
  void setUp() {
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    SecurityContextHolder.clearContext();
  }

  // ========== CASOS DE ÉXITO ==========

  @Test
  @DisplayName("Debe extraer userId de header X-User-Id")
  void debeExtraerUserId() throws ServletException, IOException {
    // Arrange
    request.addHeader("X-User-Id", "123e4567-e89b-12d3-a456-426614174000");
    request.addHeader("X-User-Roles", "USER");

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth.getPrincipal())
        .isEqualTo(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("Debe extraer roles de header X-User-Roles")
  void debeExtraerRoles() throws ServletException, IOException {
    // Arrange
    request.addHeader("X-User-Id", "123e4567-e89b-12d3-a456-426614174000");
    request.addHeader("X-User-Roles", "USER,ADMIN");

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth.getAuthorities())
        .extracting(GrantedAuthority::getAuthority)
        .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("Debe crear autenticación en SecurityContext")
  void debeCrearAutenticacionEnSecurityContext() throws ServletException, IOException {
    // Arrange
    request.addHeader("X-User-Id", "123e4567-e89b-12d3-a456-426614174000");
    request.addHeader("X-User-Roles", "USER");

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth.isAuthenticated()).isTrue();
    assertThat(auth.getPrincipal())
        .isEqualTo(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    assertThat(auth.getCredentials()).isNull();
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("Debe manejar request sin headers (rutas públicas)")
  void debeManejarRequestSinHeaders() throws ServletException, IOException {
    // Arrange - No headers agregados

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNull();
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("Debe validar formato de headers correctamente")
  void debeValidarFormatoHeaders() throws ServletException, IOException {
    // Arrange
    request.addHeader("X-User-Id", "123e4567-e89b-12d3-a456-426614174000");
    request.addHeader("X-User-Roles", "USER");

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth.getPrincipal())
        .isEqualTo(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));
    verify(filterChain, times(1)).doFilter(request, response);
  }

  // ========== CASOS DE EDGE ==========

  @Test
  @DisplayName("Debe ignorar request si solo tiene X-User-Id sin roles")
  void debeIgnorarSiSoloTieneUserId() throws ServletException, IOException {
    // Arrange
    request.addHeader("X-User-Id", "123e4567-e89b-12d3-a456-426614174000");
    // No header de roles

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNull();
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("Debe agregar prefijo ROLE_ a los roles")
  void debeAgregarPrefijoRoleARoles() throws ServletException, IOException {
    // Arrange
    request.addHeader("X-User-Id", "123e4567-e89b-12d3-a456-426614174000");
    request.addHeader("X-User-Roles", "USER");

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
    assertThat(authorities).hasSize(1);
    assertThat(authorities).extracting(GrantedAuthority::getAuthority).containsExactly("ROLE_USER");
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("Debe manejar múltiples roles separados por comas")
  void debeManejarMultiplesRoles() throws ServletException, IOException {
    // Arrange
    request.addHeader("X-User-Id", "123e4567-e89b-12d3-a456-426614174000");
    request.addHeader("X-User-Roles", "USER,ADMIN");

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth.getAuthorities())
        .hasSize(2)
        .extracting(GrantedAuthority::getAuthority)
        .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("Debe manejar roles con espacios adicionales")
  void debeManejarRolesConEspacios() throws ServletException, IOException {
    // Arrange
    request.addHeader("X-User-Id", "123e4567-e89b-12d3-a456-426614174000");
    request.addHeader("X-User-Roles", " USER , ADMIN ");

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    assertThat(auth).isNotNull();
    assertThat(auth.getAuthorities())
        .hasSize(2)
        .extracting(GrantedAuthority::getAuthority)
        .containsExactlyInAnyOrder("ROLE_USER", "ROLE_ADMIN");
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("Debe permitir continuar filter chain incluso sin autenticación")
  void debePermitirContinuarFilterChain() throws ServletException, IOException {
    // Arrange - No headers

    // Act
    filter.doFilterInternal(request, response, filterChain);

    // Assert
    verify(filterChain, times(1)).doFilter(request, response);
  }

  @Test
  @DisplayName("Debe limpiar contexto de seguridad antes de cada test")
  void debeComenzarConContextoLimpio() {
    // Assert
    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
  }
}
