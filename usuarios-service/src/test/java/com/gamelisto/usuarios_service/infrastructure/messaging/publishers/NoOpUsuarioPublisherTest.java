package com.gamelisto.usuarios_service.infrastructure.messaging.publishers;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.gamelisto.usuarios_service.domain.events.UsuarioCreado;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("NoOpUsuarioPublisher - Tests")
class NoOpUsuarioPublisherTest {

  private NoOpUsuarioPublisher publisher;

  @BeforeEach
  void setUp() {
    publisher = new NoOpUsuarioPublisher();
  }

  @Test
  @DisplayName("Debe ejecutar publish sin lanzar excepciones")
  void debeEjecutarPublishSinLanzarExcepciones() {
    // Given
    UsuarioCreado event =
        new UsuarioCreado(
            "user-123", "testuser", "test@example.com", Instant.parse("2026-01-29T10:00:00Z"));

    // When & Then - No debe lanzar excepción
    assertThatCode(() -> publisher.publish("created", event)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe aceptar cualquier tipo de evento")
  void debeAceptarCualquierTipoDeEvento() {
    // Given
    Object customEvent = new CustomEvent("test-data");

    // When & Then
    assertThatCode(() -> publisher.publish("custom", customEvent)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe aceptar routing keys vacíos")
  void debeAceptarRoutingKeysVacios() {
    // Given
    Object event = new CustomEvent("data");

    // When & Then
    assertThatCode(() -> publisher.publish("", event)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe manejar eventos null sin lanzar excepciones")
  void debeManejarEventosNullSinLanzarExcepciones() {
    // When & Then
    assertThatCode(() -> publisher.publish("test", null)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe manejar routing key null sin lanzar excepciones")
  void debeManejarRoutingKeyNullSinLanzarExcepciones() {
    // Given
    Object event = new CustomEvent("data");

    // When & Then - El método debería manejar esto sin problemas
    assertThatCode(() -> publisher.publish(null, event)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe publicar múltiples eventos consecutivamente")
  void debePublicarMultiplesEventosConsecutivamente() {
    // Given
    UsuarioCreado event1 = new UsuarioCreado("1", "user1", "user1@test.com", Instant.now());
    UsuarioCreado event2 = new UsuarioCreado("2", "user2", "user2@test.com", Instant.now());
    UsuarioCreado event3 = new UsuarioCreado("3", "user3", "user3@test.com", Instant.now());

    // When & Then
    assertThatCode(
            () -> {
              publisher.publish("created", event1);
              publisher.publish("created", event2);
              publisher.publish("created", event3);
            })
        .doesNotThrowAnyException();
  }

  // Clase helper para tests
  private record CustomEvent(String data) {}
}
