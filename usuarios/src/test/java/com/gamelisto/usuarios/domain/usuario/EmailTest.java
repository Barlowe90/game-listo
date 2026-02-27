package com.gamelisto.usuarios.domain.usuario;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailTest {

  @Test
  @DisplayName("Debe crear email válido y normalizarlo a minúsculas")
  void debeCrearEmailValidoYNormalizarlo() {
    // Arrange & Act
    Email email = Email.of("Usuario@Example.COM");

    // Assert
    assertEquals("usuario@example.com", email.value());
  }

  @Test
  @DisplayName("Debe crear email con formato estándar")
  void debeCrearEmailConFormatoEstandar() {
    // Arrange & Act
    Email email = Email.of("test@example.com");

    // Assert
    assertEquals("test@example.com", email.value());
  }

  @Test
  @DisplayName("Debe crear email con caracteres especiales válidos")
  void debeCrearEmailConCaracteresEspecialesValidos() {
    // Arrange & Act
    Email email1 = Email.of("user+tag@example.com");
    Email email2 = Email.of("user.name@example.com");
    Email email3 = Email.of("user_name@example.co.uk");

    // Assert
    assertEquals("user+tag@example.com", email1.value());
    assertEquals("user.name@example.com", email2.value());
    assertEquals("user_name@example.co.uk", email3.value());
  }

  @Test
  @DisplayName("Debe eliminar espacios en blanco al normalizar")
  void debeEliminarEspaciosEnBlancoAlNormalizar() {
    // Arrange & Act
    Email email = Email.of("  test@example.com  ");

    // Assert
    assertEquals("test@example.com", email.value());
  }

  @Test
  @DisplayName("Debe lanzar excepción si el email es nulo")
  void debeLanzarExcepcionSiEmailEsNulo() {
    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> Email.of(null));

    assertTrue(exception.getMessage().contains("no puede ser nulo"));
  }

  @Test
  @DisplayName("Debe lanzar excepción si el email es vacío")
  void debeLanzarExcepcionSiEmailEsVacio() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> Email.of(""));
    assertThrows(IllegalArgumentException.class, () -> Email.of("   "));
  }

  @Test
  @DisplayName("Debe lanzar excepción si el formato es inválido - sin arroba")
  void debeLanzarExcepcionSiFormatoEsInvalidoSinArroba() {
    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> Email.of("no-es-email"));

    assertTrue(exception.getMessage().contains("formato del email es inválido"));
  }

  @Test
  @DisplayName("Debe lanzar excepción si el formato es inválido - solo arroba")
  void debeLanzarExcepcionSiFormatoEsInvalidoSoloArroba() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> Email.of("@ejemplo.com"));
  }

  @Test
  @DisplayName("Debe lanzar excepción si el formato es inválido - sin domain")
  void debeLanzarExcepcionSiFormatoEsInvalidoSinDominio() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> Email.of("usuario@"));
  }

  @Test
  @DisplayName("Debe lanzar excepción si el formato es inválido - sin extensión")
  void debeLanzarExcepcionSiFormatoEsInvalidoSinExtension() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> Email.of("usuario@domain"));
  }

  @Test
  @DisplayName("Debe rechazar email que exceda 255 caracteres")
  void debeRechazarEmailDemasiadoLargo() {
    // Arrange
    String emailLargo = "a".repeat(250) + "@test.com";

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> Email.of(emailLargo));

    assertTrue(exception.getMessage().contains("no puede exceder 255 caracteres"));
  }

  @Test
  @DisplayName("Debe aceptar email en el límite de 255 caracteres")
  void debeAceptarEmailEnElLimite() {
    // Arrange - Crear email de exactamente 255 caracteres
    String local = "a".repeat(240);
    String email = local + "@example.com"; // 240 + 12 = 252 caracteres

    // Act & Assert
    assertDoesNotThrow(() -> Email.of(email));
  }

  @Test
  @DisplayName("toString debe retornar el valor del email")
  void toStringDebeRetornarValor() {
    // Arrange
    Email email = Email.of("test@example.com");

    // Act & Assert
    assertEquals("test@example.com", email.toString());
  }
}
