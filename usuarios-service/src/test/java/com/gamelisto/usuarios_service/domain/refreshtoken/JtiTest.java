package com.gamelisto.usuarios_service.domain.refreshtoken;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Jti - Tests de Value Object")
class JtiTest {

  @Test
  @DisplayName("Debe generar un JTI válido")
  void debeGenerarJtiValido() {
    // Act
    Jti jti = Jti.generate();

    // Assert
    assertNotNull(jti);
    assertNotNull(jti.value());
    assertFalse(jti.value().isEmpty());
    // UUID tiene 36 caracteres (incluyendo guiones)
    assertEquals(36, jti.value().length());
  }

  @Test
  @DisplayName("Debe crear un JTI desde string válido")
  void debeCrearJtiDesdeStringValido() {
    // Arrange
    String uuidString = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";

    // Act
    Jti jti = Jti.of(uuidString);

    // Assert
    assertEquals(uuidString, jti.value());
  }

  @Test
  @DisplayName("Debe lanzar excepción si el JTI es null")
  void debeLanzarExcepcionSiJtiEsNull() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> Jti.of(null));
  }

  @Test
  @DisplayName("Debe lanzar excepción si el JTI es vacío")
  void debeLanzarExcepcionSiJtiEsVacio() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> Jti.of(""));
    assertThrows(IllegalArgumentException.class, () -> Jti.of("   "));
  }

  @Test
  @DisplayName("Debe lanzar excepción si el JTI no es un UUID válido")
  void debeLanzarExcepcionSiJtiNoEsUuidValido() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> Jti.of("invalid-uuid"));
    assertThrows(IllegalArgumentException.class, () -> Jti.of("12345"));
  }

  @Test
  @DisplayName("Dos JTI con el mismo valor deben ser iguales")
  void dosJtiConMismoValorDebenSerIguales() {
    // Arrange
    String uuid = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";
    Jti jti1 = Jti.of(uuid);
    Jti jti2 = Jti.of(uuid);

    // Assert
    assertEquals(jti1, jti2);
    assertEquals(jti1.hashCode(), jti2.hashCode());
  }

  @Test
  @DisplayName("Dos JTI generados deben ser diferentes")
  void dosJtiGeneradosDebenSerDiferentes() {
    // Act
    Jti jti1 = Jti.generate();
    Jti jti2 = Jti.generate();

    // Assert
    assertNotEquals(jti1, jti2);
  }

  @Test
  @DisplayName("toString debe contener información del JTI")
  void toStringDebeContenerInformacion() {
    // Arrange
    Jti jti = Jti.generate();

    // Act
    String toString = jti.toString();

    // Assert
    assertNotNull(toString);
    assertFalse(toString.isEmpty());
  }
}
