package com.gamelisto.usuarios_service.domain.usuario;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TokenVerificacion - Tests de Value Object")
class TokenVerificacionTest {

  // ========== CASOS DE ÉXITO ==========

  @Test
  @DisplayName("Debe generar token con método generate()")
  void debeGenerarTokenConMetodoGenerate() {
    // Arrange & Act
    TokenVerificacion token = TokenVerificacion.generate();

    // Assert
    assertNotNull(token.value());
    assertFalse(token.isEmpty());
    assertFalse(token.value().isBlank());
  }

  @Test
  @DisplayName("Debe generar tokens únicos cada vez")
  void debeGenerarTokensUnicosСadaVez() {
    // Arrange & Act
    TokenVerificacion token1 = TokenVerificacion.generate();
    TokenVerificacion token2 = TokenVerificacion.generate();
    TokenVerificacion token3 = TokenVerificacion.generate();

    // Assert
    assertNotEquals(token1.value(), token2.value());
    assertNotEquals(token2.value(), token3.value());
    assertNotEquals(token1.value(), token3.value());
  }

  @Test
  @DisplayName("Debe crear token con método of() y valor válido")
  void debeCrearTokenConMetodoOf() {
    // Arrange
    String valorToken = "abc123xyz789";

    // Act
    TokenVerificacion token = TokenVerificacion.of(valorToken);

    // Assert
    assertEquals(valorToken, token.value());
    assertFalse(token.isEmpty());
  }

  @Test
  @DisplayName("Debe crear token vacío con método empty()")
  void debeCrearTokenVacioConMetodoEmpty() {
    // Arrange & Act
    TokenVerificacion token = TokenVerificacion.empty();

    // Assert
    assertNull(token.value());
    assertTrue(token.isEmpty());
  }

  // ========== CASOS DE ERROR ==========

  @Test
  @DisplayName("Debe lanzar excepción si el token es nulo en of()")
  void debeLanzarExcepcionSiTokenEsNulo() {
    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> TokenVerificacion.of(null));

    assertTrue(exception.getMessage().contains("nulo o vacío"));
  }

  @Test
  @DisplayName("Debe lanzar excepción si el token es vacío en of()")
  void debeLanzarExcepcionSiTokenEsVacio() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> TokenVerificacion.of(""));
    assertThrows(IllegalArgumentException.class, () -> TokenVerificacion.of("   "));
  }

  // ========== TESTS DE IGUALDAD ==========

  @Test
  @DisplayName("Debe ser igual a otro token con el mismo valor")
  void debeSerIgualAOtroTokenConMismoValor() {
    // Arrange
    String valor = "token_value_123";
    TokenVerificacion token1 = TokenVerificacion.of(valor);
    TokenVerificacion token2 = TokenVerificacion.of(valor);

    // Assert
    assertEquals(token1, token2);
    assertEquals(token1.hashCode(), token2.hashCode());
  }

  @Test
  @DisplayName("No debe ser igual a otro token con diferente valor")
  void noDebeSerIgualAOtroTokenConDiferenteValor() {
    // Arrange
    TokenVerificacion token1 = TokenVerificacion.of("token_1");
    TokenVerificacion token2 = TokenVerificacion.of("token_2");

    // Assert
    assertNotEquals(token1, token2);
  }

  @Test
  @DisplayName("Tokens vacíos deben ser iguales")
  void tokensVaciosDebenSerIguales() {
    // Arrange
    TokenVerificacion token1 = TokenVerificacion.empty();
    TokenVerificacion token2 = TokenVerificacion.empty();

    // Assert
    assertEquals(token1, token2);
    assertEquals(token1.hashCode(), token2.hashCode());
  }

  @Test
  @DisplayName("No debe ser igual a null")
  void noDebeSerIgualANull() {
    // Arrange
    TokenVerificacion token = TokenVerificacion.of("token_value");

    // Assert
    assertNotEquals(null, token);
  }

  @Test
  @DisplayName("No debe ser igual a objeto de otro tipo")
  void noDebeSerIgualAObjetoDeOtroTipo() {
    // Arrange
    TokenVerificacion token = TokenVerificacion.of("token_value");

    // Assert
    assertNotEquals("token_value", token);
  }

  // ========== TESTS DE toString() ==========

  @Test
  @DisplayName("Debe proteger el valor en toString()")
  void debeProtegerValorEnToString() {
    // Arrange
    TokenVerificacion token = TokenVerificacion.of("mi_token_secreto");

    // Act
    String resultado = token.toString();

    // Assert
    assertFalse(resultado.contains("mi_token_secreto"));
    assertTrue(resultado.contains("[PROTECTED]"));
  }
}
