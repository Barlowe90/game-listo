package com.gamelist.catalogo.domain.catalog;

import com.gamelist.catalogo.domain.exceptions.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests para la entidad Platform. Platform ahora se sincroniza desde IGDB en lugar de usar enums.
 */
class PlatformTest {

  @Test
  @DisplayName("Debe crear plataforma válida desde datos de IGDB")
  void debeCrearPlataformaValida() {
    // Arrange
    PlatformId id = PlatformId.of(48L); // ID real de PS5 en IGDB
    PlatformName name = PlatformName.of("PlayStation 5");
    PlatformAbbreviation abbreviation = PlatformAbbreviation.of("PS5");

    // Act
    Platform platform = Platform.create(id, name, abbreviation);

    // Assert
    assertThat(platform.getId()).isEqualTo(id);
    assertThat(platform.getName()).isEqualTo(name);
    assertThat(platform.getAbbreviation()).isEqualTo(abbreviation);
    assertThat(platform.hasAbbreviation()).isTrue();
  }

  @Test
  @DisplayName("Debe crear plataforma sin abreviación")
  void debeCrearPlataformaSinAbreviacion() {
    // Arrange
    PlatformId id = PlatformId.of(6L);
    PlatformName name = PlatformName.of("PC (Microsoft Windows)");

    // Act
    Platform platform = Platform.create(id, name, null);

    // Assert
    assertThat(platform.getAbbreviation()).isNotNull();
    assertThat(platform.getAbbreviation().isEmpty()).isTrue();
    assertThat(platform.hasAbbreviation()).isFalse();
  }

  @Test
  @DisplayName("Debe lanzar excepción si ID es nulo")
  void debeLanzarExcepcionSiIdNulo() {
    // Arrange
    PlatformName name = PlatformName.of("Test Platform");

    // Act & Assert
    assertThatThrownBy(() -> Platform.create(null, name, null))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("ID de plataforma es obligatorio");
  }

  @Test
  @DisplayName("Debe lanzar excepción si nombre es nulo")
  void debeLanzarExcepcionSiNombreNulo() {
    // Arrange
    PlatformId id = PlatformId.of(1L);

    // Act & Assert
    assertThatThrownBy(() -> Platform.create(id, null, null))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("nombre de plataforma es obligatorio");
  }

  @Test
  @DisplayName("Debe reconstituir plataforma desde BD")
  void debeReconstituirPlataforma() {
    // Arrange
    PlatformId id = PlatformId.of(130L); // Nintendo Switch en IGDB
    PlatformName name = PlatformName.of("Nintendo Switch");
    PlatformAbbreviation abbreviation = PlatformAbbreviation.of("Switch");

    // Act
    Platform platform = Platform.reconstitute(id, name, abbreviation);

    // Assert
    assertThat(platform.getId()).isEqualTo(id);
    assertThat(platform.getName()).isEqualTo(name);
    assertThat(platform.getAbbreviation()).isEqualTo(abbreviation);
  }

  @Test
  @DisplayName("Debe actualizar datos de plataforma durante sincronización")
  void debeActualizarDatosPlataforma() {
    // Arrange
    Platform platform =
        Platform.create(
            PlatformId.of(1L), PlatformName.of("Old Name"), PlatformAbbreviation.of("OLD"));

    // Act
    PlatformName newName = PlatformName.of("New Name");
    PlatformAbbreviation newAbbr = PlatformAbbreviation.of("NEW");
    platform.update(newName, newAbbr);

    // Assert
    assertThat(platform.getName()).isEqualTo(newName);
    assertThat(platform.getAbbreviation()).isEqualTo(newAbbr);
  }

  @Test
  @DisplayName("Dos plataformas con mismo ID deben ser iguales")
  void dosPlataformasConMismoIdDebenSerIguales() {
    // Arrange
    PlatformId id = PlatformId.of(48L);
    Platform p1 =
        Platform.create(id, PlatformName.of("PlayStation 5"), PlatformAbbreviation.of("PS5"));
    Platform p2 =
        Platform.create(id, PlatformName.of("Different Name"), PlatformAbbreviation.of("DN"));

    // Assert
    assertThat(p1).isEqualTo(p2);
    assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
  }

  @Test
  @DisplayName("Dos plataformas con diferente ID no deben ser iguales")
  void dosPlataformasConDiferenteIdNoDebenSerIguales() {
    // Arrange
    Platform p1 =
        Platform.create(
            PlatformId.of(48L), PlatformName.of("PlayStation 5"), PlatformAbbreviation.of("PS5"));
    Platform p2 =
        Platform.create(
            PlatformId.of(49L),
            PlatformName.of("Xbox Series X/S"),
            PlatformAbbreviation.of("Series X/S"));

    // Assert
    assertThat(p1).isNotEqualTo(p2);
  }

  @Test
  @DisplayName("Debe tener toString correcto")
  void debeTenerToStringCorrecto() {
    // Arrange
    Platform platform =
        Platform.create(
            PlatformId.of(6L),
            PlatformName.of("PC (Microsoft Windows)"),
            PlatformAbbreviation.of("PC"));

    // Act
    String result = platform.toString();

    // Assert
    assertThat(result).contains("Platform");
    assertThat(result).contains("id=");
    assertThat(result).contains("name=");
    assertThat(result).contains("abbreviation=");
  }
}
