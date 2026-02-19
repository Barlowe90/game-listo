package com.gamelist.catalogo_service.domain.syncstate;

import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

/** Tests para la entidad SyncState. */
class SyncStateTest {

  @Test
  @DisplayName("Debe crear SyncState con valor")
  void debeCrearSyncState() {
    // Arrange & Act
    SyncState syncState = SyncState.create(SyncKey.LAST_SYNCED_GAME_ID, "12345");

    // Assert
    assertThat(syncState.getKey()).isEqualTo(SyncKey.LAST_SYNCED_GAME_ID);
    assertThat(syncState.getValue()).isEqualTo("12345");
    assertThat(syncState.hasValue()).isTrue();
    assertThat(syncState.getUpdatedAt()).isNotNull();
  }

  @Test
  @DisplayName("Debe lanzar excepción si key es nulo")
  void debeLanzarExcepcionSiKeyNulo() {
    // Act & Assert
    assertThatThrownBy(() -> SyncState.create(null, "value"))
        .isInstanceOf(InvalidGameDataException.class)
        .hasMessageContaining("SyncKey es obligatorio");
  }

  @Test
  @DisplayName("Debe actualizar valor")
  void debeActualizarValor() {
    // Arrange
    SyncState syncState = SyncState.create(SyncKey.LAST_SYNCED_GAME_ID, "100");
    Instant initialUpdatedAt = syncState.getUpdatedAt();

    // Esperar un poco para que el timestamp cambie
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
    }

    // Act
    syncState.updateValue("200");

    // Assert
    assertThat(syncState.getValue()).isEqualTo("200");
    assertThat(syncState.getUpdatedAt()).isAfter(initialUpdatedAt);
  }

  @Test
  @DisplayName("Debe obtener valor como Long")
  void debeObtenerValorComoLong() {
    // Arrange
    SyncState syncState = SyncState.create(SyncKey.LAST_SYNCED_GAME_ID, "12345");

    // Act
    Long value = syncState.getValueAsLong();

    // Assert
    assertThat(value).isEqualTo(12345L);
  }

  @Test
  @DisplayName("Debe lanzar excepción si valor no es un número válido")
  void debeLanzarExcepcionSiValorNoEsNumeroValido() {
    // Arrange
    SyncState syncState = SyncState.create(SyncKey.LAST_SYNCED_GAME_ID, "not-a-number");

    // Act & Assert
    assertThatThrownBy(() -> syncState.getValueAsLong())
        .isInstanceOf(InvalidGameDataException.class)
        .hasMessageContaining("no es un número válido");
  }

  @Test
  @DisplayName("Debe obtener valor como Instant")
  void debeObtenerValorComoInstant() {
    // Arrange
    Instant now = Instant.now();
    SyncState syncState = SyncState.create(SyncKey.LAST_SYNC_TIMESTAMP, now.toString());

    // Act
    Instant value = syncState.getValueAsInstant();

    // Assert
    assertThat(value).isNotNull();
    assertThat(value.toString()).isEqualTo(now.toString());
  }

  @Test
  @DisplayName("Debe lanzar excepción si valor no es timestamp válido")
  void debeLanzarExcepcionSiValorNoEsTimestampValido() {
    // Arrange
    SyncState syncState = SyncState.create(SyncKey.LAST_SYNC_TIMESTAMP, "invalid-timestamp");

    // Act & Assert
    assertThatThrownBy(() -> syncState.getValueAsInstant())
        .isInstanceOf(InvalidGameDataException.class)
        .hasMessageContaining("no es un timestamp válido");
  }

  @Test
  @DisplayName("hasValue debe retornar false si valor es null o vacío")
  void hasValueDebeFalseSiValorVacio() {
    // Arrange
    SyncState syncStateNull = SyncState.create(SyncKey.LAST_SYNCED_GAME_ID, null);
    SyncState syncStateEmpty = SyncState.create(SyncKey.LAST_SYNCED_GAME_ID, "");
    SyncState syncStateBlank = SyncState.create(SyncKey.LAST_SYNCED_GAME_ID, "   ");

    // Assert
    assertThat(syncStateNull.hasValue()).isFalse();
    assertThat(syncStateEmpty.hasValue()).isFalse();
    assertThat(syncStateBlank.hasValue()).isFalse();
  }

  @Test
  @DisplayName("Debe reconstituir desde BD")
  void debeReconstituirDesdeBD() {
    // Arrange
    Instant timestamp = Instant.parse("2024-01-01T00:00:00Z");

    // Act
    SyncState syncState = SyncState.reconstitute(SyncKey.LAST_SYNCED_GAME_ID, "99999", timestamp);

    // Assert
    assertThat(syncState.getKey()).isEqualTo(SyncKey.LAST_SYNCED_GAME_ID);
    assertThat(syncState.getValue()).isEqualTo("99999");
    assertThat(syncState.getUpdatedAt()).isEqualTo(timestamp);
  }

  @Test
  @DisplayName("Dos SyncState con misma key deben ser iguales")
  void dosSyncStateConMismaKeyDebenSerIguales() {
    // Arrange
    SyncState s1 = SyncState.create(SyncKey.LAST_SYNCED_GAME_ID, "100");
    SyncState s2 = SyncState.create(SyncKey.LAST_SYNCED_GAME_ID, "200");

    // Assert
    assertThat(s1).isEqualTo(s2);
    assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
  }

  @Test
  @DisplayName("Dos SyncState con diferente key no deben ser iguales")
  void dosSyncStateConDiferenteKeyNoDebenSerIguales() {
    // Arrange
    SyncState s1 = SyncState.create(SyncKey.LAST_SYNCED_GAME_ID, "100");
    SyncState s2 = SyncState.create(SyncKey.LAST_SYNC_TIMESTAMP, "100");

    // Assert
    assertThat(s1).isNotEqualTo(s2);
  }
}
