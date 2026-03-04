package com.gamelisto.usuarios.infrastructure.out.messaging.publishers;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.gamelisto.usuarios.domain.events.UsuarioCreado;
import com.gamelisto.usuarios.domain.events.UsuarioEliminado;
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
  @DisplayName("Debe ejecutar publicarUsuarioCreado sin lanzar excepciones")
  void debeEjecutarPublicarUsuarioCreadoSinLanzarExcepciones() {
    // Given
    UsuarioCreado event =
        new UsuarioCreado(
            "user-123",
            "testuser",
            "test@example.com",
            "avatar.png",
            "USER",
            "ESP",
            "PENDIENTE_DE_VERIFICACION",
            null,
            null,
            Instant.parse("2026-01-29T10:00:00Z"));

    // When & Then - No debe lanzar excepción
    assertThatCode(() -> publisher.publicarUsuarioCreado(event)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe aceptar cualquier tipo de evento para publicarUsuarioEliminado")
  void debeAceptarCualquierTipoDeEvento() {
    // Given
    UsuarioEliminado evento = UsuarioEliminado.of("id-1");

    // When & Then
    assertThatCode(() -> publisher.publicarUsuarioEliminado(evento)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName(
      "Debe manejar publicarUsuarioCreado con routing keys vacíos y nulos sin lanzar excepciones")
  void debeAceptarRoutingKeysVacios() {
    // Given
    UsuarioCreado event =
        new UsuarioCreado(
            "1",
            "user1",
            "user1@test.com",
            "avatar1.png",
            "USER",
            "ESP",
            "ACTIVO",
            null,
            null,
            Instant.now());

    // When & Then
    assertThatCode(() -> publisher.publicarUsuarioCreado(event)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe manejar eventos null sin lanzar excepciones")
  void debeManejarEventosNullSinLanzarExcepciones() {
    // When & Then
    assertThatCode(() -> publisher.publicarUsuarioCreado(null)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe publicar múltiples eventos consecutivamente")
  void debePublicarMultiplesEventosConsecutivamente() {
    // Given
    UsuarioCreado event1 =
        new UsuarioCreado(
            "1",
            "user1",
            "user1@test.com",
            "avatar1.png",
            "USER",
            "ESP",
            "ACTIVO",
            null,
            null,
            Instant.now());
    UsuarioCreado event2 =
        new UsuarioCreado(
            "2",
            "user2",
            "user2@test.com",
            "avatar2.png",
            "USER",
            "ESP",
            "ACTIVO",
            null,
            null,
            Instant.now());
    UsuarioCreado event3 =
        new UsuarioCreado(
            "3",
            "user3",
            "user3@test.com",
            "avatar3.png",
            "USER",
            "ESP",
            "ACTIVO",
            null,
            null,
            Instant.now());

    // When & Then
    assertThatCode(
            () -> {
              publisher.publicarUsuarioCreado(event1);
              publisher.publicarUsuarioCreado(event2);
              publisher.publicarUsuarioCreado(event3);
            })
        .doesNotThrowAnyException();
  }
}
