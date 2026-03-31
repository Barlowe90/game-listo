package com.gamelisto.usuarios.domain.usuario;

import static org.junit.jupiter.api.Assertions.*;

import com.gamelisto.usuarios.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UsernameTest {

  @Test
  @DisplayName("Debe crear username vÃ¡lido con letras y nÃºmeros")
  void debeCrearUsernameValidoConLetrasYNumeros() {
    // Arrange & Act
    Username username = Username.of("usuario123");

    // Assert
    assertEquals("usuario123", username.value());
  }

  @Test
  @DisplayName("Debe crear username con guiones y guiones bajos")
  void debeCrearUsernameConGuiones() {
    // Arrange & Act
    Username username1 = Username.of("user-name");
    Username username2 = Username.of("user_name");
    Username username3 = Username.of("user-name_123");

    // Assert
    assertEquals("user-name", username1.value());
    assertEquals("user_name", username2.value());
    assertEquals("user-name_123", username3.value());
  }

  @Test
  @DisplayName("Debe crear username con 3 caracteres (mÃ­nimo)")
  void debeCrearUsernameConTresCaracteres() {
    // Arrange & Act
    Username username = Username.of("abc");

    // Assert
    assertEquals("abc", username.value());
  }

  @Test
  @DisplayName("Debe crear username con 30 caracteres (mÃ¡ximo)")
  void debeCrearUsernameConTreintaCaracteres() {
    // Arrange
    String username30 = "a".repeat(30);

    // Act
    Username username = Username.of(username30);

    // Assert
    assertEquals(30, username.value().length());
  }

  @Test
  @DisplayName("Debe eliminar espacios en blanco al normalizar")
  void debeEliminarEspaciosEnBlancoAlNormalizar() {
    // Arrange & Act
    Username username = Username.of("  usuario  ");

    // Assert
    assertEquals("usuario", username.value());
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el username es nulo")
  void debeLanzarExcepcionSiUsernameEsNulo() {
    // Act & Assert
    DomainException exception = assertThrows(DomainException.class, () -> Username.of(null));

    assertTrue(exception.getMessage().contains("no puede ser nulo"));
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el username es vacÃ­o")
  void debeLanzarExcepcionSiUsernameEsVacio() {
    // Act & Assert
    assertThrows(DomainException.class, () -> Username.of(""));
    assertThrows(DomainException.class, () -> Username.of("   "));
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el username tiene menos de 3 caracteres")
  void debeLanzarExcepcionSiUsernameMuyCorto() {
    // Act & Assert
    DomainException exception = assertThrows(DomainException.class, () -> Username.of("ab"));

    assertTrue(exception.getMessage().contains("debe tener entre 3 y 30 caracteres"));
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el username tiene mÃ¡s de 30 caracteres")
  void debeLanzarExcepcionSiUsernameMuyLargo() {
    // Arrange
    String username31 = "a".repeat(31);

    // Act & Assert
    DomainException exception = assertThrows(DomainException.class, () -> Username.of(username31));

    assertTrue(exception.getMessage().contains("debe tener entre 3 y 30 caracteres"));
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el username contiene espacios")
  void debeLanzarExcepcionSiUsernameContieneEspacios() {
    // Act & Assert
    assertThrows(DomainException.class, () -> Username.of("user name"));
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el username contiene caracteres especiales invÃ¡lidos")
  void debeLanzarExcepcionSiUsernameContieneCaracteresInvalidos() {
    // Act & Assert
    assertThrows(DomainException.class, () -> Username.of("user@name"));
    assertThrows(DomainException.class, () -> Username.of("user#name"));
    assertThrows(DomainException.class, () -> Username.of("user.name"));
    assertThrows(DomainException.class, () -> Username.of("user+name"));
    assertThrows(DomainException.class, () -> Username.of("user!name"));
  }

  @Test
  @DisplayName("Debe lanzar excepciÃ³n si el username contiene caracteres acentuados")
  void debeLanzarExcepcionSiUsernameContieneAcentos() {
    // Act & Assert
    assertThrows(DomainException.class, () -> Username.of("usuÃ¡rio"));
    assertThrows(DomainException.class, () -> Username.of("usÃ¼ario"));
  }

  @Test
  @DisplayName("toString debe retornar el valor del username")
  void toStringDebeRetornarValor() {
    // Arrange
    Username username = Username.of("testuser");

    // Act & Assert
    assertEquals("testuser", username.toString());
  }
}



