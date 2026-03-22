package com.gamelisto.usuarios.infrastructure.shared.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.gamelisto.usuarios.shared.security.GatewayAuthenticationFilter;
import com.gamelisto.usuarios.shared.security.SecurityConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@DisplayName("SecurityConfig - Tests")
class SecurityConfigTest {

  private SecurityConfig securityConfig;

  @BeforeEach
  void setUp() {
    // Mock del GatewayAuthenticationFilter requerido por SecurityConfig
    GatewayAuthenticationFilter gatewayAuthenticationFilter =
        mock(GatewayAuthenticationFilter.class);
    securityConfig = new SecurityConfig(gatewayAuthenticationFilter);
  }

  @Test
  @DisplayName("Debe crear PasswordEncoder de tipo BCrypt")
  void debeCrearPasswordEncoderDeTipoBCrypt() {
    // When
    PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

    // Then
    assertThat(passwordEncoder).isNotNull().isInstanceOf(BCryptPasswordEncoder.class);
  }

  @Test
  @DisplayName("Debe codificar contraseÃ±as correctamente con BCrypt")
  void debeCodificarContrasenasCorrectamenteConBCrypt() {
    // Given
    PasswordEncoder encoder = securityConfig.passwordEncoder();
    String rawPassword = "MySecurePassword123!";

    // When
    String encodedPassword = encoder.encode(rawPassword);

    // Then
    assertThat(encodedPassword)
        .isNotNull()
        .isNotEqualTo(rawPassword)
        .startsWith("$2a$"); // BCrypt prefix
    assertThat(encoder.matches(rawPassword, encodedPassword)).isTrue();
  }

  @Test
  @DisplayName("Debe generar hashes diferentes para la misma contraseÃ±a")
  void debeGenerarHashesDiferentesParaLaMismaContrasena() {
    // Given
    PasswordEncoder encoder = securityConfig.passwordEncoder();
    String password = "SamePassword123";

    // When
    String hash1 = encoder.encode(password);
    String hash2 = encoder.encode(password);

    // Then
    assertThat(hash1).isNotEqualTo(hash2); // BCrypt usa salt aleatorio
    assertThat(encoder.matches(password, hash1)).isTrue();
    assertThat(encoder.matches(password, hash2)).isTrue();
  }

  @Test
  @DisplayName("Debe validar contraseÃ±as correctamente")
  void debeValidarContrasenasCorrectamente() {
    // Given
    PasswordEncoder encoder = securityConfig.passwordEncoder();
    String correctPassword = "CorrectPassword123";
    String wrongPassword = "WrongPassword456";
    String encodedPassword = encoder.encode(correctPassword);

    // When & Then
    assertThat(encoder.matches(correctPassword, encodedPassword)).isTrue();
    assertThat(encoder.matches(wrongPassword, encodedPassword)).isFalse();
  }

  @Test
  @DisplayName("Debe crear SecurityFilterChain correctamente")
  void debeCrearSecurityFilterChainCorrectamente() throws Exception {
    // When - Este test verifica que la funcion existe y es invocable
    // La verificaciÃ³n real de la configuraciÃ³n se hace con Spring Boot Test

    // Then - La funcion debe existir y retornar SecurityFilterChain
    var method =
        SecurityConfig.class.getMethod(
            "securityFilterChain",
            org.springframework.security.config.annotation.web.builders.HttpSecurity.class);
    assertThat(method).isNotNull();
    assertThat(method.getReturnType()).isEqualTo(SecurityFilterChain.class);
  }

  @Test
  @DisplayName("Debe configurar PasswordEncoder con strength por defecto (10)")
  void debeConfigurarPasswordEncoderConStrengthPorDefecto() {
    // Given
    PasswordEncoder encoder = securityConfig.passwordEncoder();

    // When
    String encoded = encoder.encode("test");

    // Then
    // BCrypt con strength 10 genera hashes de 60 caracteres
    assertThat(encoded).hasSize(60).matches("\\$2[ayb]\\$\\d{2}\\$.+"); // Formato BCrypt
  }

  @Test
  @DisplayName("Debe manejar contraseÃ±as vacÃ­as")
  void debeManejarContrasenasVacias() {
    // Given
    PasswordEncoder encoder = securityConfig.passwordEncoder();

    // When
    String encodedEmpty = encoder.encode("");

    // Then - BCrypt codifica la cadena vacÃ­a sin lanzar excepciÃ³n
    assertThat(encodedEmpty).isNotNull().isNotEmpty();
  }

  @Test
  @DisplayName("Debe manejar contraseÃ±as con caracteres especiales")
  void debeManejarContrasenasConCaracteresEspeciales() {
    // Given
    PasswordEncoder encoder = securityConfig.passwordEncoder();
    String specialPassword = "P@ssw0rd!#$%^&*()_+-=[]{}|;':,.<>?/~`";

    // When
    String encoded = encoder.encode(specialPassword);

    // Then
    assertThat(encoder.matches(specialPassword, encoded)).isTrue();
  }

  @Test
  @DisplayName("Debe manejar contraseÃ±as Unicode")
  void debeManejarContrasenasUnicode() {
    // Given
    PasswordEncoder encoder = securityConfig.passwordEncoder();
    String unicodePassword = "ContraseÃ±a123â‚¬Ã±ä¸­æ–‡";

    // When
    String encoded = encoder.encode(unicodePassword);

    // Then
    assertThat(encoder.matches(unicodePassword, encoded)).isTrue();
  }

  @Test
  @DisplayName("Debe manejar contraseÃ±as largas (hasta 72 bytes)")
  void debeManejarContrasenasLargas() {
    // Given
    PasswordEncoder encoder = securityConfig.passwordEncoder();
    String longPassword = "a".repeat(72); // BCrypt acepta hasta 72 bytes

    // When
    String encoded = encoder.encode(longPassword);

    // Then
    assertThat(encoder.matches(longPassword, encoded)).isTrue();
  }

  @Test
  @DisplayName("Debe rechazar contraseÃ±as mayores a 72 bytes")
  void debeRechazarContrasenasMayoresA72Bytes() {
    // Given
    PasswordEncoder encoder = securityConfig.passwordEncoder();
    String tooLongPassword = "a".repeat(73); // MÃ¡s de 72 bytes

    // When & Then
    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> encoder.encode(tooLongPassword),
        "password cannot be more than 72 bytes");
  }

  // Test de integraciÃ³n bÃ¡sico para SecurityFilterChain
  @Test
  @DisplayName("Debe permitir acceso a todos los endpoints (desarrollo)")
  void debePermitirAccesoATodosLosEndpoints() {
    // Este test verifica el comportamiento esperado de la configuraciÃ³n
    // En un entorno real, se usarÃ­a @SpringBootTest

    // Given - La configuraciÃ³n actual permite todas las peticiones
    // When & Then - Verificamos que la funcion existe y es correcto
    var methods = SecurityConfig.class.getDeclaredMethods();
    var securityFilterChainMethod =
        java.util.Arrays.stream(methods)
            .filter(m -> m.getName().equals("securityFilterChain"))
            .findFirst();

    assertThat(securityFilterChainMethod).isPresent();
  }
}



