package com.gamelisto.usuarios.domain.usuario;

import static org.junit.jupiter.api.Assertions.*;

import com.gamelisto.usuarios.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("DiscordUserId - Tests de Value Object")
class DiscordUserIdTest {

  // ========== CASOS DE Ã‰XITO ==========

  @Test
  @DisplayName("Debe crear DiscordUserId con valor vÃ¡lido")
  void debeCrearDiscordUserIdConValorValido() {
    // Arrange & Act
    DiscordUserId discordUserId = DiscordUserId.of("123456789012345678");

    // Assert
    assertEquals("123456789012345678", discordUserId.value());
    assertFalse(discordUserId.isEmpty());
  }

  @Test
  @DisplayName("Debe crear DiscordUserId vacÃ­o con mÃ©todo empty()")
  void debeCrearDiscordUserIdVacioConMetodoEmpty() {
    // Arrange & Act
    DiscordUserId discordUserId = DiscordUserId.empty();

    // Assert
    assertNull(discordUserId.value());
    assertTrue(discordUserId.isEmpty());
  }

  @ParameterizedTest
  @NullSource
  @EmptySource
  @ValueSource(strings = {"   ", "  ", "\t", "\n"})
  @DisplayName("Debe crear DiscordUserId vacÃ­o con valor null, cadena vacÃ­a o espacios en blanco")
  void debeCrearDiscordUserIdVacioConValoresInvalidos(String valor) {
    // Arrange & Act
    DiscordUserId discordUserId = DiscordUserId.of(valor);

    // Assert
    assertNull(discordUserId.value());
    assertTrue(discordUserId.isEmpty());
  }

  @Test
  @DisplayName("Debe eliminar espacios en blanco del valor")
  void debeEliminarEspaciosEnBlancoDelValor() {
    // Arrange & Act
    DiscordUserId discordUserId = DiscordUserId.of("  123456789  ");

    // Assert
    assertEquals("123456789", discordUserId.value());
  }

  @Test
  @DisplayName("Debe crear DiscordUserId con exactamente 100 caracteres")
  void debeCrearDiscordUserIdCon100Caracteres() {
    // Arrange
    String valor = "a".repeat(100);

    // Act
    DiscordUserId discordUserId = DiscordUserId.of(valor);

    // Assert
    assertEquals(100, discordUserId.value().length());
    assertFalse(discordUserId.isEmpty());
  }

  // ========== CASOS DE ERROR ==========

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el valor excede 100 caracteres")
  void debeLanzarExcepcionSiValorExcede100Caracteres() {
    // Arrange
    String valorLargo = "a".repeat(101);

    // Act & Assert
    DomainException exception =
        assertThrows(DomainException.class, () -> DiscordUserId.of(valorLargo));

    assertTrue(exception.getMessage().contains("100 caracteres"));
  }

  // ========== TESTS DE toString() ==========

  @Test
  @DisplayName("Debe retornar valor en toString() cuando tiene valor")
  void debeRetornarValorEnToString() {
    // Arrange & Act
    DiscordUserId discordUserId = DiscordUserId.of("123456789");

    // Assert
    assertEquals("123456789", discordUserId.toString());
  }

  @Test
  @DisplayName("Debe retornar [NO VINCULADO] en toString() cuando estÃ¡ vacÃ­o")
  void debeRetornarNoVinculadoEnToString() {
    // Arrange & Act
    DiscordUserId discordUserId = DiscordUserId.empty();

    // Assert
    assertEquals("[NO VINCULADO]", discordUserId.toString());
  }
}



