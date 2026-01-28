package com.gamelisto.usuarios_service.domain.usuario;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Avatar - Tests de Value Object")
class AvatarTest {

  // ========== CASOS DE ÉXITO ==========

  @Test
  @DisplayName("Debe crear avatar con URL válida")
  void debeCrearAvatarConUrlValida() {
    // Arrange & Act
    Avatar avatar = Avatar.of("https://example.com/avatar.jpg");

    // Assert
    assertEquals("https://example.com/avatar.jpg", avatar.url());
    assertFalse(avatar.isEmpty());
  }

  @Test
  @DisplayName("Debe crear avatar vacío con método empty()")
  void debeCrearAvatarVacioConMetodoEmpty() {
    // Arrange & Act
    Avatar avatar = Avatar.empty();

    // Assert
    assertNull(avatar.url());
    assertTrue(avatar.isEmpty());
  }

  @Test
  @DisplayName("Debe crear avatar vacío con valor null")
  void debeCrearAvatarVacioConValorNull() {
    // Arrange & Act
    Avatar avatar = Avatar.of(null);

    // Assert
    assertNull(avatar.url());
    assertTrue(avatar.isEmpty());
  }

  @Test
  @DisplayName("Debe crear avatar vacío con cadena vacía")
  void debeCrearAvatarVacioConCadenaVacia() {
    // Arrange & Act
    Avatar avatar = Avatar.of("");

    // Assert
    assertNull(avatar.url());
    assertTrue(avatar.isEmpty());
  }

  @Test
  @DisplayName("Debe crear avatar vacío con espacios en blanco")
  void debeCrearAvatarVacioConEspaciosEnBlanco() {
    // Arrange & Act
    Avatar avatar = Avatar.of("   ");

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
  @DisplayName("Debe lanzar excepción si la URL excede 500 caracteres")
  void debeLanzarExcepcionSiUrlExcede500Caracteres() {
    // Arrange
    String urlLarga = "https://example.com/" + "a".repeat(481);
    assertTrue(urlLarga.length() > 500);

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> Avatar.of(urlLarga));

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
  @DisplayName("Debe retornar [SIN AVATAR] en toString() cuando está vacío")
  void debeRetornarSinAvatarEnToString() {
    // Arrange & Act
    Avatar avatar = Avatar.empty();

    // Assert
    assertEquals("[SIN AVATAR]", avatar.toString());
  }
}
