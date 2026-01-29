package com.gamelisto.usuarios_service.domain.usuario;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("DiscordUsername - Tests de Value Object")
class DiscordUsernameTest {

  // ========== CASOS DE ÉXITO ==========

  @Test
  @DisplayName("Debe crear DiscordUsername con valor válido")
  void debeCrearDiscordUsernameConValorValido() {
    // Arrange & Act
    DiscordUsername discordUsername = DiscordUsername.of("usuario_discord#1234");

    // Assert
    assertEquals("usuario_discord#1234", discordUsername.value());
    assertFalse(discordUsername.isEmpty());
  }

  @Test
  @DisplayName("Debe crear DiscordUsername vacío con método empty()")
  void debeCrearDiscordUsernameVacioConMetodoEmpty() {
    // Arrange & Act
    DiscordUsername discordUsername = DiscordUsername.empty();

    // Assert
    assertNull(discordUsername.value());
    assertTrue(discordUsername.isEmpty());
  }

  @ParameterizedTest
  @NullSource
  @EmptySource
  @ValueSource(strings = {"   ", "  ", "\t", "\n"})
  @DisplayName("Debe crear DiscordUsername vacío con valor null, cadena vacía o espacios en blanco")
  void debeCrearDiscordUsernameVacioConValoresInvalidos(String valor) {
    // Arrange & Act
    DiscordUsername discordUsername = DiscordUsername.of(valor);

    // Assert
    assertNull(discordUsername.value());
    assertTrue(discordUsername.isEmpty());
  }

  @Test
  @DisplayName("Debe eliminar espacios en blanco del valor")
  void debeEliminarEspaciosEnBlancoDelValor() {
    // Arrange & Act
    DiscordUsername discordUsername = DiscordUsername.of("  usuario_discord  ");

    // Assert
    assertEquals("usuario_discord", discordUsername.value());
  }

  @Test
  @DisplayName("Debe crear DiscordUsername con exactamente 100 caracteres")
  void debeCrearDiscordUsernameCon100Caracteres() {
    // Arrange
    String valor = "a".repeat(100);

    // Act
    DiscordUsername discordUsername = DiscordUsername.of(valor);

    // Assert
    assertEquals(100, discordUsername.value().length());
    assertFalse(discordUsername.isEmpty());
  }

  // ========== CASOS DE ERROR ==========

  @Test
  @DisplayName("Debe lanzar excepción si el valor excede 100 caracteres")
  void debeLanzarExcepcionSiValorExcede100Caracteres() {
    // Arrange
    String valorLargo = "a".repeat(101);

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> DiscordUsername.of(valorLargo));

    assertTrue(exception.getMessage().contains("100 caracteres"));
  }

  // ========== TESTS DE toString() ==========

  @Test
  @DisplayName("Debe retornar valor en toString() cuando tiene valor")
  void debeRetornarValorEnToString() {
    // Arrange & Act
    DiscordUsername discordUsername = DiscordUsername.of("usuario#1234");

    // Assert
    assertEquals("usuario#1234", discordUsername.toString());
  }

  @Test
  @DisplayName("Debe retornar [NO VINCULADO] en toString() cuando está vacío")
  void debeRetornarNoVinculadoEnToString() {
    // Arrange & Act
    DiscordUsername discordUsername = DiscordUsername.empty();

    // Assert
    assertEquals("[NO VINCULADO]", discordUsername.toString());
  }
}
