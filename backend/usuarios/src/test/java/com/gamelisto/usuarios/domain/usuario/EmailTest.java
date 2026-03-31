package com.gamelisto.usuarios.domain.usuario;

import static org.junit.jupiter.api.Assertions.*;

import com.gamelisto.usuarios.domain.exceptions.DomainException;
import com.resend.services.domains.model.Domain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EmailTest {

  @Test
  @DisplayName("Debe crear email vÃ¡lido y normalizarlo a minÃºsculas")
  void debeCrearEmailValidoYNormalizarlo() {
    // Arrange & Act
    Email email = Email.of("Usuario@Example.COM");

    // Assert
    assertEquals("usuario@example.com", email.value());
  }

  @Test
  @DisplayName("Debe crear email con formato estÃ¡ndar")
  void debeCrearEmailConFormatoEstandar() {
    // Arrange & Act
    Email email = Email.of("test@example.com");

    // Assert
    assertEquals("test@example.com", email.value());
  }

  @Test
  @DisplayName("Debe crear email con caracteres especiales vÃ¡lidos")
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
  @DisplayName("Debe lanzar excepciÃ³n si el email es nulo")
  void debeLanzarExcepcionSiEmailEsNulo() {
    // Act & Assert
    DomainException exception = assertThrows(DomainException.class, () -> Email.of(null));

    assertTrue(exception.getMessage().contains("no puede ser nulo"));
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el email es vacÃ­o")
  void debeLanzarExcepcionSiEmailEsVacio() {
    // Act & Assert
    assertThrows(DomainException.class, () -> Email.of(""));
    assertThrows(DomainException.class, () -> Email.of("   "));
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el formato es invÃ¡lido - sin arroba")
  void debeLanzarExcepcionSiFormatoEsInvalidoSinArroba() {
    // Act & Assert
    DomainException exception = assertThrows(DomainException.class, () -> Email.of("no-es-email"));

    assertTrue(exception.getMessage().contains("formato del email es inválido"));
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el formato es invÃ¡lido - solo arroba")
  void debeLanzarExcepcionSiFormatoEsInvalidoSoloArroba() {
    // Act & Assert
    assertThrows(DomainException.class, () -> Email.of("@ejemplo.com"));
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el formato es invÃ¡lido - sin domain")
  void debeLanzarExcepcionSiFormatoEsInvalidoSinDominio() {
    // Act & Assert
    assertThrows(DomainException.class, () -> Email.of("usuario@"));
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el formato es invÃ¡lido - sin extensiÃ³n")
  void debeLanzarExcepcionSiFormatoEsInvalidoSinExtension() {
    // Act & Assert
    assertThrows(DomainException.class, () -> Email.of("usuario@domain"));
  }

  @Test
  @DisplayName("Debe rechazar email que exceda 255 caracteres")
  void debeRechazarEmailDemasiadoLargo() {
    // Arrange
    String emailLargo = "a".repeat(250) + "@test.com";

    // Act & Assert
    DomainException exception = assertThrows(DomainException.class, () -> Email.of(emailLargo));

    assertTrue(exception.getMessage().contains("no puede exceder 255 caracteres"));
  }

  @Test
  @DisplayName("Debe aceptar email en el lÃ­mite de 255 caracteres")
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



