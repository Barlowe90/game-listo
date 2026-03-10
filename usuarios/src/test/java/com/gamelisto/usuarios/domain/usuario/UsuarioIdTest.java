package com.gamelisto.usuarios.domain.usuario;

import static org.junit.jupiter.api.Assertions.*;

import com.gamelisto.usuarios.domain.exceptions.DomainException;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UsuarioIdTest {

  @Test
  @DisplayName("Debe crear UsuarioId desde UUID existente")
  void debeCrearUsuarioIdDesdeUUID() {
    // Arrange
    UUID uuid = UUID.randomUUID();

    // Act
    UsuarioId usuarioId = UsuarioId.of(uuid);

    // Assert
    assertNotNull(usuarioId);
    assertEquals(uuid, usuarioId.value());
  }

  @Test
  @DisplayName("Debe generar nuevo UsuarioId con UUID aleatorio")
  void debeGenerarNuevoUsuarioId() {
    // Act
    UsuarioId usuarioId1 = UsuarioId.generate();
    UsuarioId usuarioId2 = UsuarioId.generate();

    // Assert
    assertNotNull(usuarioId1);
    assertNotNull(usuarioId2);
    assertNotEquals(usuarioId1.value(), usuarioId2.value());
  }

  @Test
  @DisplayName("Debe crear UsuarioId desde String válido")
  void debeCrearUsuarioIdDesdeString() {
    // Arrange
    String uuidString = "550e8400-e29b-41d4-a716-446655440000";

    // Act
    UsuarioId usuarioId = UsuarioId.fromString(uuidString);

    // Assert
    assertNotNull(usuarioId);
    assertEquals(uuidString, usuarioId.value().toString());
  }

  @Test
  @DisplayName("Debe crear UsuarioId desde String con mayúsculas")
  void debeCrearUsuarioIdDesdStringConMayusculas() {
    // Arrange
    String uuidString = "550E8400-E29B-41D4-A716-446655440000";

    // Act
    UsuarioId usuarioId = UsuarioId.fromString(uuidString);

    // Assert
    assertNotNull(usuarioId);
    assertEquals(uuidString.toLowerCase(), usuarioId.value().toString());
  }

  @Test
  @DisplayName("Debe lanzar excepción si UUID es nulo")
  void debeLanzarExcepcionSiUUIDEsNulo() {
    // Act & Assert
    DomainException exception = assertThrows(DomainException.class, () -> UsuarioId.of(null));

    assertTrue(exception.getMessage().contains("no puede ser nulo"));
  }

  @Test
  @DisplayName("Debe lanzar excepción si String es nulo")
  void debeLanzarExcepcionSiStringEsNulo() {
    // Act & Assert
    DomainException exception =
        assertThrows(DomainException.class, () -> UsuarioId.fromString(null));

    assertTrue(exception.getMessage().contains("no puede ser nulo"));
  }

  @Test
  @DisplayName("Debe lanzar excepción si String es vacío")
  void debeLanzarExcepcionSiStringEsVacio() {
    // Act & Assert
    assertThrows(DomainException.class, () -> UsuarioId.fromString(""));
    assertThrows(DomainException.class, () -> UsuarioId.fromString("   "));
  }

  @Test
  @DisplayName("Debe lanzar excepción si formato de UUID es inválido")
  void debeLanzarExcepcionSiFormatoUUIDEsInvalido() {
    // Act & Assert
    DomainException exception =
        assertThrows(DomainException.class, () -> UsuarioId.fromString("no-es-un-uuid"));

    assertTrue(exception.getMessage().contains("Formato de UUID inválido"));
  }

  @Test
  @DisplayName("Debe lanzar excepción si UUID tiene formato incompleto")
  void debeLanzarExcepcionSiUUIDIncompleto() {
    // Act & Assert
    assertThrows(DomainException.class, () -> UsuarioId.fromString("550e8400-e29b-41d4"));
  }

  @Test
  @DisplayName("Debe lanzar excepción si UUID tiene caracteres inválidos")
  void debeLanzarExcepcionSiUUIDConCaracteresInvalidos() {
    // Act & Assert
    assertThrows(
        DomainException.class, () -> UsuarioId.fromString("550e8400-ZZZZ-41d4-a716-446655440000"));
  }

  @Test
  @DisplayName("toString debe retornar representación String del UUID")
  void toStringDebeRetornarStringDelUUID() {
    // Arrange
    String uuidString = "550e8400-e29b-41d4-a716-446655440000";
    UsuarioId usuarioId = UsuarioId.fromString(uuidString);

    // Act & Assert
    assertEquals(uuidString, usuarioId.toString());
  }

  @Test
  @DisplayName("Dos UsuarioId con mismo UUID deben tener mismo value")
  void dosUsuarioIdConMismoUUID() {
    // Arrange
    UUID uuid = UUID.randomUUID();

    // Act
    UsuarioId usuarioId1 = UsuarioId.of(uuid);
    UsuarioId usuarioId2 = UsuarioId.of(uuid);

    // Assert
    assertEquals(usuarioId1.value(), usuarioId2.value());
  }
}
