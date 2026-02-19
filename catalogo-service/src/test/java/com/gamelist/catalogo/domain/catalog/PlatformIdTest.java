package com.gamelist.catalogo.domain.catalog;

import com.gamelist.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/** Tests para el Value Object PlatformId. */
class PlatformIdTest {

  @Test
  @DisplayName("Debe crear PlatformId válido con valor positivo")
  void debeCrearPlatformIdValido() {
    // Arrange & Act
    PlatformId platformId = PlatformId.of(48L); // PS5 en IGDB

    // Assert
    assertThat(platformId.value()).isEqualTo(48L);
  }

  @Test
  @DisplayName("Debe lanzar excepción si el ID es nulo")
  void debeLanzarExcepcionSiIdNulo() {
    // Act & Assert
    assertThatThrownBy(() -> PlatformId.of(null))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("no puede ser nulo");
  }

  @Test
  @DisplayName("Debe lanzar excepción si el ID es cero")
  void debeLanzarExcepcionSiIdCero() {
    // Act & Assert
    assertThatThrownBy(() -> PlatformId.of(0L))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("debe ser un número positivo");
  }

  @Test
  @DisplayName("Debe lanzar excepción si el ID es negativo")
  void debeLanzarExcepcionSiIdNegativo() {
    // Act & Assert
    assertThatThrownBy(() -> PlatformId.of(-100L))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("debe ser un número positivo");
  }

  @Test
  @DisplayName("Dos PlatformId con mismo valor deben ser iguales")
  void dosPlatformIdConMismoValorDebenSerIguales() {
    // Arrange
    PlatformId id1 = PlatformId.of(48L);
    PlatformId id2 = PlatformId.of(48L);

    // Assert
    assertThat(id1).isEqualTo(id2);
    assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
  }
}
