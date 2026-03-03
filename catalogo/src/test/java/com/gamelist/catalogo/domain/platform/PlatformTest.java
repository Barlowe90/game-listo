package com.gamelist.catalogo.domain.platform;

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
    Platform platform = Platform.create(id, name, abbreviation, null, null, null);

    // Assert
    assertThat(platform.getId()).isEqualTo(id);
    assertThat(platform.getName()).isEqualTo(name);
    assertThat(platform.getAbbreviation()).isEqualTo(abbreviation);
    assertThat(platform.getAbbreviation().isEmpty()).isFalse();
  }

  @Test
  @DisplayName("Debe crear plataforma sin abreviación")
  void debeCrearPlataformaSinAbreviacion() {
    // Arrange
    PlatformId id = PlatformId.of(6L);
    PlatformName name = PlatformName.of("PC (Microsoft Windows)");

    // Act
    Platform platform = Platform.create(id, name, null, null, null, null);

    // Assert
    assertThat(platform.getAbbreviation()).isNotNull();
    assertThat(platform.getAbbreviation().isEmpty()).isTrue();
  }

  @Test
  @DisplayName("Debe lanzar excepción si ID es nulo")
  void debeLanzarExcepcionSiIdNulo() {
    // Arrange
    PlatformName name = PlatformName.of("Test Platform");

    // Act & Assert
    assertThatThrownBy(() -> Platform.create(null, name, null, null, null, null))
        .isInstanceOf(DomainException.class)
        .hasMessageContaining("ID de plataforma es obligatorio");
  }

  @Test
  @DisplayName("Debe lanzar excepción si nombre es nulo")
  void debeLanzarExcepcionSiNombreNulo() {
    // Arrange
    PlatformId id = PlatformId.of(1L);

    // Act & Assert
    assertThatThrownBy(() -> Platform.create(id, null, null, null, null, null))
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
    Platform platform = Platform.reconstitute(id, name, abbreviation, null, null, null);

    // Assert
    assertThat(platform.getId()).isEqualTo(id);
    assertThat(platform.getName()).isEqualTo(name);
    assertThat(platform.getAbbreviation()).isEqualTo(abbreviation);
  }

  @Test
  @DisplayName("Debe actualizar datos de plataforma durante sincronización")
  void debeActualizarDatosPlataforma() {
    // Arrange - simulamos la actualización creando una nueva instancia con los nuevos valores
    Platform original =
        Platform.create(
            PlatformId.of(1L),
            PlatformName.of("Old Name"),
            PlatformAbbreviation.of("OLD"),
            null,
            null,
            null);

    Platform updated =
        Platform.create(
            original.getId(),
            PlatformName.of("New Name"),
            PlatformAbbreviation.of("NEW"),
            null,
            null,
            null);

    // Assert
    assertThat(updated.getName()).isEqualTo(PlatformName.of("New Name"));
    assertThat(updated.getAbbreviation()).isEqualTo(PlatformAbbreviation.of("NEW"));
  }

  @Test
  @DisplayName("Dos plataformas con mismo ID deben ser iguales")
  void dosPlataformasConMismoIdDebenSerIguales() {
    // Arrange
    PlatformId id = PlatformId.of(48L);
    Platform p1 =
        Platform.create(
            id, PlatformName.of("PlayStation 5"), PlatformAbbreviation.of("PS5"), null, null, null);
    Platform p2 =
        Platform.create(
            id, PlatformName.of("Different Name"), PlatformAbbreviation.of("DN"), null, null, null);

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
            PlatformId.of(48L),
            PlatformName.of("PlayStation 5"),
            PlatformAbbreviation.of("PS5"),
            null,
            null,
            null);
    Platform p2 =
        Platform.create(
            PlatformId.of(49L),
            PlatformName.of("Xbox Series X/S"),
            PlatformAbbreviation.of("Series X/S"),
            null,
            null,
            null);

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
            PlatformAbbreviation.of("PC"),
            null,
            null,
            null);

    // Act
    String result = platform.toString();

    // Assert
    assertThat(result).contains("Platform");
    assertThat(result).contains("id=");
    assertThat(result).contains("name=");
    assertThat(result).contains("abbreviation=");
  }
}
