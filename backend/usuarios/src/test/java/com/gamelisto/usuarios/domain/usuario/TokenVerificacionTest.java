package com.gamelisto.usuarios.domain.usuario;

import static org.junit.jupiter.api.Assertions.*;

import com.gamelisto.usuarios.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TokenVerificacion - Value Object tests")
class TokenVerificacionTest {

  @Test
  @DisplayName("Should generate token with generate()")
  void debeGenerarTokenConMetodoGenerate() {
    TokenVerificacion token = TokenVerificacion.generate();

    assertNotNull(token.value());
    assertFalse(token.isEmpty());
    assertFalse(token.value().isBlank());
  }

  @Test
  @DisplayName("Should generate unique tokens each time")
  void debeGenerarTokensUnicosCadaVez() {
    TokenVerificacion token1 = TokenVerificacion.generate();
    TokenVerificacion token2 = TokenVerificacion.generate();
    TokenVerificacion token3 = TokenVerificacion.generate();

    assertNotEquals(token1.value(), token2.value());
    assertNotEquals(token2.value(), token3.value());
    assertNotEquals(token1.value(), token3.value());
  }

  @Test
  @DisplayName("Should create token with of() and valid value")
  void debeCrearTokenConMetodoOf() {
    String valorToken = "abc123xyz789";

    TokenVerificacion token = TokenVerificacion.of(valorToken);

    assertEquals(valorToken, token.value());
    assertFalse(token.isEmpty());
  }

  @Test
  @DisplayName("Should create empty token with empty()")
  void debeCrearTokenVacioConMetodoEmpty() {
    TokenVerificacion token = TokenVerificacion.empty();

    assertNull(token.value());
    assertTrue(token.isEmpty());
  }

  @Test
  @DisplayName("Should throw if token is null in of()")
  void debeLanzarExcepcionSiTokenEsNulo() {
    DomainException exception =
        assertThrows(DomainException.class, () -> TokenVerificacion.of(null));

    assertTrue(exception.getMessage().contains("nulo o vacío"));
  }

  @Test
  @DisplayName("Should throw if token is blank in of()")
  void debeLanzarExcepcionSiTokenEsVacio() {
    assertThrows(DomainException.class, () -> TokenVerificacion.of(""));
    assertThrows(DomainException.class, () -> TokenVerificacion.of("   "));
  }

  @Test
  @DisplayName("Should equal another token with same value")
  void debeSerIgualAOtroTokenConMismoValor() {
    String valor = "token_value_123";
    TokenVerificacion token1 = TokenVerificacion.of(valor);
    TokenVerificacion token2 = TokenVerificacion.of(valor);

    assertEquals(token1, token2);
    assertEquals(token1.hashCode(), token2.hashCode());
  }

  @Test
  @DisplayName("Should not equal token with different value")
  void noDebeSerIgualAOtroTokenConDiferenteValor() {
    TokenVerificacion token1 = TokenVerificacion.of("token_1");
    TokenVerificacion token2 = TokenVerificacion.of("token_2");

    assertNotEquals(token1, token2);
  }

  @Test
  @DisplayName("Empty tokens should be equal")
  void tokensVaciosDebenSerIguales() {
    TokenVerificacion token1 = TokenVerificacion.empty();
    TokenVerificacion token2 = TokenVerificacion.empty();

    assertEquals(token1, token2);
    assertEquals(token1.hashCode(), token2.hashCode());
  }

  @Test
  @DisplayName("Should not equal null")
  void noDebeSerIgualANull() {
    TokenVerificacion token = TokenVerificacion.of("token_value");

    assertNotEquals(null, token);
  }

  @Test
  @DisplayName("Should not equal object of another type")
  void noDebeSerIgualAObjetoDeOtroTipo() {
    TokenVerificacion token = TokenVerificacion.of("token_value");
    Object otroTipo = "token_value";

    assertNotEquals(otroTipo, token);
  }

  @Test
  @DisplayName("Should protect value in toString()")
  void debeProtegerValorEnToString() {
    TokenVerificacion token = TokenVerificacion.of("mi_token_secreto");

    String resultado = token.toString();

    assertFalse(resultado.contains("mi_token_secreto"));
    assertTrue(resultado.contains("[PROTECTED]"));
  }
}
