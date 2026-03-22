package com.gamelisto.usuarios.infrastructure.out.messaging.publishers;

import static org.assertj.core.api.Assertions.assertThatCode;

import com.gamelisto.usuarios.domain.events.UsuarioActualizado;
import com.gamelisto.usuarios.domain.events.UsuarioCreado;
import com.gamelisto.usuarios.domain.events.UsuarioEliminado;
import java.time.Instant;

import com.gamelisto.usuarios.infrastructure.out.messaging.NoOpUsuarioPublisher;
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
            null);

    // When & Then - No debe lanzar excepciÃ³n
    assertThatCode(() -> publisher.publicarUsuarioCreado(event)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe ejecutar publicarUsuarioActualizado sin lanzar excepciones")
  void debeEjecutarPublicarUsuarioActualizadoSinLanzarExcepciones() {
    // Given
    UsuarioActualizado event =
        UsuarioActualizado.of("user-123", "testuser", "avatar.png", "123456789");

    // When & Then
    assertThatCode(() -> publisher.publicarUsuarioActualizado(event)).doesNotThrowAnyException();
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
      "Debe manejar publicarUsuarioCreado con routing keys vacÃ­os y nulos sin lanzar excepciones")
  void debeAceptarRoutingKeysVacios() {
    // Given
    UsuarioCreado event =
        new UsuarioCreado(
            "1", "user1", "user1@test.com", "avatar1.png", "USER", "ESP", "ACTIVO", null);

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
  @DisplayName("Debe publicar mÃºltiples eventos consecutivamente")
  void debePublicarMultiplesEventosConsecutivamente() {
    // Given
    UsuarioCreado event1 =
        new UsuarioCreado(
            "1", "user1", "user1@test.com", "avatar1.png", "USER", "ESP", "ACTIVO", null);
    UsuarioCreado event2 =
        new UsuarioCreado(
            "2", "user2", "user2@test.com", "avatar2.png", "USER", "ESP", "ACTIVO", null);
    UsuarioCreado event3 =
        new UsuarioCreado(
            "3", "user3", "user3@test.com", "avatar3.png", "USER", "ESP", "ACTIVO", null);

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



