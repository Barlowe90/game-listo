package com.gamelisto.usuarios.infrastructure.in.api.dto;

public record ValidationError(String field, String rejectedValue, String message) {
  public static ValidationError of(String field, Object rejectedValue, String message) {
    return new ValidationError(
        field, rejectedValue != null ? rejectedValue.toString() : "null", message);
  }
}
