package com.gamelist.catalogo.infrastructure.out.messaging.dto;

public record GameMensaje<T>(String eventId, String eventType, String service, T data) {
  public static <T> GameMensaje<T> of(String eventType, T data) {
    return new GameMensaje<>(
        java.util.UUID.randomUUID().toString(), eventType, "catalogo-service", data);
  }
}
