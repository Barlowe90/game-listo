package com.gamelist.catalogo_service.domain.syncstate;

import com.gamelist.catalogo_service.domain.exceptions.InvalidGameDataException;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

@Getter
public class SyncState {
  private final SyncKey key;
  private String value;
  private Instant updatedAt;

  private SyncState(SyncKey key, String value, Instant updatedAt) {
    this.key = Objects.requireNonNull(key, "SyncKey no puede ser nulo");
    this.value = value;
    this.updatedAt = updatedAt != null ? updatedAt : Instant.now();
  }

  public static SyncState create(SyncKey key, String value) {
    if (key == null) {
      throw new InvalidGameDataException("SyncKey es obligatorio");
    }
    return new SyncState(key, value, Instant.now());
  }

  public static SyncState reconstitute(SyncKey key, String value, Instant updatedAt) {
    return new SyncState(key, value, updatedAt);
  }

  public void updateValue(String newValue) {
    this.value = newValue;
    this.updatedAt = Instant.now();
  }

  public Long getValueAsLong() {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return Long.parseLong(value);
    } catch (NumberFormatException e) {
      throw new InvalidGameDataException("El valor no es un número válido: " + value);
    }
  }

  public Instant getValueAsInstant() {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return Instant.parse(value);
    } catch (Exception e) {
      throw new InvalidGameDataException("El valor no es un timestamp válido: " + value);
    }
  }

  public boolean hasValue() {
    return value != null && !value.isBlank();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SyncState syncState = (SyncState) o;
    return key == syncState.key;
  }

  @Override
  public int hashCode() {
    return Objects.hash(key);
  }

  @Override
  public String toString() {
    return "SyncState{"
        + "key="
        + key
        + ", value='"
        + value
        + '\''
        + ", updatedAt="
        + updatedAt
        + '}';
  }
}
