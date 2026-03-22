package com.gamelisto.usuarios.domain.usuario;

import static org.junit.jupiter.api.Assertions.*;

import com.gamelisto.usuarios.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Avatar - Tests de Value Object")
class AvatarTest {

  // ========== CASOS DE Ã‰XITO ==========

  @Test
  @DisplayName("Debe crear avatar con URL vÃ¡lida")
  void debeCrearAvatarConUrlValida() {
    // Arrange & Act
    Avatar avatar = Avatar.of("https://example.com/avatar.jpg");

    // Assert
    assertEquals("https://example.com/avatar.jpg", avatar.url());
    assertFalse(avatar.isEmpty());
  }

  @Test
  @DisplayName("Debe crear avatar vacÃ­o con mÃ©todo empty()")
  void debeCrearAvatarVacioConMetodoEmpty() {
    // Arrange & Act
    Avatar avatar = Avatar.empty();

    // Assert
    assertNull(avatar.url());
    assertTrue(avatar.isEmpty());
  }

  @ParameterizedTest
  @NullSource
  @EmptySource
  @ValueSource(strings = {"   ", "  ", "\t", "\n"})
  @DisplayName("Debe crear avatar vacÃ­o con valor null, cadena vacÃ­a o espacios en blanco")
  void debeCrearAvatarVacioConValoresInvalidos(String valor) {
    // Arrange & Act
    Avatar avatar = Avatar.of(valor);

    // Assert
    assertNull(avatar.url());
    assertTrue(avatar.isEmpty());
  }

  @Test
  @DisplayName("Debe eliminar espacios en blanco de la URL")
  void debeEliminarEspaciosEnBlancoDeLaUrl() {
    // Arrange & Act
    Avatar avatar = Avatar.of("  https://example.com/avatar.jpg  ");

    // Assert
    assertEquals("https://example.com/avatar.jpg", avatar.url());
  }

  @Test
  @DisplayName("Debe crear avatar con URL de exactamente 500 caracteres")
  void debeCrearAvatarConUrlDe500Caracteres() {
    // Arrange
    String url = "https://example.com/" + "a".repeat(480);
    assertEquals(500, url.length());

    // Act
    Avatar avatar = Avatar.of(url);

    // Assert
    assertEquals(500, avatar.url().length());
    assertFalse(avatar.isEmpty());
  }

  // ========== CASOS DE ERROR ==========

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si la URL excede 500 caracteres")
  void debeLanzarExcepcionSiUrlExcede500Caracteres() {
    // Arrange
    String urlLarga = "https://example.com/" + "a".repeat(481);
    assertTrue(urlLarga.length() > 500);

    // Act & Assert
    DomainException exception = assertThrows(DomainException.class, () -> Avatar.of(urlLarga));

    assertTrue(exception.getMessage().contains("500 caracteres"));
  }

  // ========== TESTS DE toString() ==========

  @Test
  @DisplayName("Debe retornar URL en toString() cuando tiene valor")
  void debeRetornarUrlEnToString() {
    // Arrange & Act
    Avatar avatar = Avatar.of("https://example.com/avatar.jpg");

    // Assert
    assertEquals("https://example.com/avatar.jpg", avatar.toString());
  }

  @Test
  @DisplayName("Debe retornar [SIN AVATAR] en toString() cuando estÃ¡ vacÃ­o")
  void debeRetornarSinAvatarEnToString() {
    // Arrange & Act
    Avatar avatar = Avatar.empty();

    // Assert
    assertEquals("[SIN AVATAR]", avatar.toString());
  }
}



