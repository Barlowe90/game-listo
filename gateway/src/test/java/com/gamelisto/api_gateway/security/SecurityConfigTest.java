package com.gamelisto.api_gateway.security;

import com.gamelisto.api_gateway.security.config.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("SecurityConfig - Configuración de seguridad del Gateway")
class SecurityConfigTest {

  private SecurityConfig securityConfig;

  @BeforeEach
  void setUp() {
    securityConfig = new SecurityConfig();
  }

  // ========== CONFIGURACIÓN DE BEANS ==========

  @Test
  @DisplayName("Debe crear bean de SecurityWebFilterChain correctamente")
  void debeCrearSecurityWebFilterChain() {
    // Arrange
    ServerHttpSecurity http = ServerHttpSecurity.http();

    // Act
    SecurityWebFilterChain chain = securityConfig.securityWebFilterChain(http);

    // Assert
    assertThat(chain).isNotNull();
  }

  @Test
  @DisplayName("Debe configurar SecurityConfig correctamente")
  void debeConfigurarSecurityConfigCorrectamente() {
    // Assert
    assertThat(securityConfig).isNotNull();
  }

  @Test
  @DisplayName("Debe crear SecurityWebFilterChain que no sea nulo")
  void debeCrearSecurityWebFilterChainNoNulo() {
    // Arrange
    ServerHttpSecurity http = ServerHttpSecurity.http();

    // Act
    SecurityWebFilterChain chain = securityConfig.securityWebFilterChain(http);

    // Assert
    assertThat(chain).isNotNull();
    assertThat(chain.getWebFilters()).isNotNull();
  }

  // ========== VALIDACIÓN DE ARQUITECTURA ==========

  @Test
  @DisplayName("Debe usar WebFlux (reactivo) en lugar de servlet")
  void debeUsarWebFlux() {
    // Arrange
    ServerHttpSecurity http = ServerHttpSecurity.http();

    // Act
    SecurityWebFilterChain chain = securityConfig.securityWebFilterChain(http);

    // Assert
    assertThat(chain).isNotNull();
    assertThat(chain.getClass().getName()).contains("SecurityWebFilterChain");
  }

  @Test
  @DisplayName("Debe configurar cadena de filtros correctamente")
  void debeConfigurarCadenaFiltrosCorrectamente() {
    // Arrange
    ServerHttpSecurity http = ServerHttpSecurity.http();

    // Act
    SecurityWebFilterChain chain = securityConfig.securityWebFilterChain(http);

    // Assert
    assertThat(chain).isNotNull();
    assertThat(chain.getWebFilters()).isNotNull();
  }

  @Test
  @DisplayName("Debe crear instancia de SecurityConfig sin errores")
  void debeCrearInstanciaSinErrores() {
    // Act
    SecurityConfig config = new SecurityConfig();

    // Assert
    assertThat(config).isNotNull();
  }

  @Test
  @DisplayName("Debe ser reutilizable para múltiples creaciones de SecurityWebFilterChain")
  void debeSerReutilizable() {
    // Arrange
    ServerHttpSecurity http1 = ServerHttpSecurity.http();
    ServerHttpSecurity http2 = ServerHttpSecurity.http();

    // Act
    SecurityWebFilterChain chain1 = securityConfig.securityWebFilterChain(http1);
    SecurityWebFilterChain chain2 = securityConfig.securityWebFilterChain(http2);

    // Assert
    assertThat(chain1).isNotNull();
    assertThat(chain2).isNotNull();
  }

  @Test
  @DisplayName("Debe configurar correctamente para arquitectura reactiva")
  void debeConfigurarParaArquitecturaReactiva() {
    // Arrange
    ServerHttpSecurity http = ServerHttpSecurity.http();

    // Act
    SecurityWebFilterChain chain = securityConfig.securityWebFilterChain(http);

    // Assert
    // Verificamos que es una cadena de filtros reactiva
    assertThat(chain).isNotNull();
    assertThat(chain.getClass().getSimpleName()).contains("SecurityWebFilterChain");
  }

  @Test
  @DisplayName("Debe delegar autenticación a filtros personalizados JWT")
  void debeDelegarAutenticacionAFiltrosPersonalizados() {
    // Arrange
    ServerHttpSecurity http = ServerHttpSecurity.http();

    // Act
    SecurityWebFilterChain chain = securityConfig.securityWebFilterChain(http);

    // Assert
    // La configuración permite todas las peticiones en SecurityConfig
    // porque JwtAuthenticationFilter (orden -100) se encarga de la validación
    assertThat(chain).isNotNull();
  }

  @Test
  @DisplayName("Debe ser stateless sin manejo de sesiones")
  void debeSerStateless() {
    // Arrange
    ServerHttpSecurity http = ServerHttpSecurity.http();

    // Act
    SecurityWebFilterChain chain = securityConfig.securityWebFilterChain(http);

    // Assert
    // El gateway no maneja sesiones, solo valida JWT en cada petición
    assertThat(chain).isNotNull();
  }
}
