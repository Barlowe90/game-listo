package com.gamelisto.usuarios_service.infrastructure.messaging.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UsuarioMensaje - Tests")
class UsuarioMensajeTest {

  @Test
  @DisplayName("Debe crear UsuarioMensaje con constructor completo")
  void debeCrearUsuarioMensajeConConstructorCompleto() {
    // Given
    String eventId = "evt-123";
    String eventType = "UsuarioCreado";
    String service = "usuarios-service";
    Instant timestamp = Instant.parse("2026-01-29T10:00:00Z");
    String data = "test-data";

    // When
    UsuarioMensaje<String> mensaje =
        new UsuarioMensaje<>(eventId, eventType, service, timestamp, data);

    // Then
    assertThat(mensaje.eventId()).isEqualTo(eventId);
    assertThat(mensaje.eventType()).isEqualTo(eventType);
    assertThat(mensaje.service()).isEqualTo(service);
    assertThat(mensaje.timestamp()).isEqualTo(timestamp);
    assertThat(mensaje.data()).isEqualTo(data);
  }

  @Test
  @DisplayName("Debe crear UsuarioMensaje usando método factory of()")
  void debeCrearUsuarioMensajeUsandoMetodoFactory() {
    // Given
    String eventType = "UsuarioActualizado";
    Map<String, String> data = Map.of("userId", "123", "action", "update");

    // When
    UsuarioMensaje<Map<String, String>> mensaje = UsuarioMensaje.of(eventType, data);

    // Then
    assertThat(mensaje).isNotNull();
    assertThat(mensaje.eventType()).isEqualTo(eventType);
    assertThat(mensaje.service()).isEqualTo("usuarios-service");
    assertThat(mensaje.data()).isEqualTo(data);
    assertThat(mensaje.eventId()).isNotNull().matches("^[0-9a-f-]{36}$"); // UUID format
    assertThat(mensaje.timestamp()).isNotNull().isBeforeOrEqualTo(Instant.now());
  }

  @Test
  @DisplayName("Debe generar eventId único en cada llamada a of()")
  void debeGenerarEventIdUnicoEnCadaLlamada() {
    // Given
    String eventType = "TestEvent";
    String data = "test";

    // When
    UsuarioMensaje<String> mensaje1 = UsuarioMensaje.of(eventType, data);
    UsuarioMensaje<String> mensaje2 = UsuarioMensaje.of(eventType, data);
    UsuarioMensaje<String> mensaje3 = UsuarioMensaje.of(eventType, data);

    // Then
    assertThat(mensaje1.eventId())
        .isNotEqualTo(mensaje2.eventId())
        .isNotEqualTo(mensaje3.eventId());
    assertThat(mensaje2.eventId()).isNotEqualTo(mensaje3.eventId());
  }

  @Test
  @DisplayName("Debe crear UsuarioMensaje con diferentes tipos de datos")
  void debeCrearUsuarioMensajeConDiferentesTiposDeDatos() {
    // String data
    UsuarioMensaje<String> mensajeString = UsuarioMensaje.of("StringEvent", "string-data");
    assertThat(mensajeString.data()).isInstanceOf(String.class);

    // Integer data
    UsuarioMensaje<Integer> mensajeInteger = UsuarioMensaje.of("IntegerEvent", 42);
    assertThat(mensajeInteger.data()).isInstanceOf(Integer.class);

    // Custom object data
    TestData customData = new TestData("test", 123);
    UsuarioMensaje<TestData> mensajeCustom = UsuarioMensaje.of("CustomEvent", customData);
    assertThat(mensajeCustom.data()).isInstanceOf(TestData.class);

    // Map data
    Map<String, Object> mapData = Map.of("key1", "value1", "key2", 456);
    UsuarioMensaje<Map<String, Object>> mensajeMap = UsuarioMensaje.of("MapEvent", mapData);
    assertThat(mensajeMap.data()).isInstanceOf(Map.class);
  }

  @Test
  @DisplayName("Debe tener timestamp cercano al momento de creación")
  void debeTenerTimestampCercanoAlMomentoDeCreacion() {
    // Given
    Instant before = Instant.now();

    // When
    UsuarioMensaje<String> mensaje = UsuarioMensaje.of("TestEvent", "data");

    // Then
    Instant after = Instant.now();
    assertThat(mensaje.timestamp()).isAfterOrEqualTo(before).isBeforeOrEqualTo(after);
  }

  @Test
  @DisplayName("Debe manejar data null correctamente")
  void debeManejarDataNullCorrectamente() {
    // When
    UsuarioMensaje<String> mensaje = UsuarioMensaje.of("NullDataEvent", null);

    // Then
    assertThat(mensaje.data()).isNull();
    assertThat(mensaje.eventId()).isNotNull();
    assertThat(mensaje.eventType()).isEqualTo("NullDataEvent");
    assertThat(mensaje.service()).isEqualTo("usuarios-service");
    assertThat(mensaje.timestamp()).isNotNull();
  }

  @Test
  @DisplayName("Debe ser inmutable (record)")
  void debeSerInmutable() {
    // Given
    UsuarioMensaje<String> mensaje = UsuarioMensaje.of("TestEvent", "original");

    // When - Intentar crear uno nuevo con los mismos valores
    UsuarioMensaje<String> copiedMessage =
        new UsuarioMensaje<>(
            mensaje.eventId(),
            mensaje.eventType(),
            mensaje.service(),
            mensaje.timestamp(),
            "modified");

    // Then - El original no cambia
    assertThat(mensaje.data()).isEqualTo("original");
    assertThat(copiedMessage.data()).isEqualTo("modified");
  }

  @Test
  @DisplayName("Debe implementar equals y hashCode correctamente (record)")
  void debeImplementarEqualsYHashCodeCorrectamente() {
    // Given
    String eventId = "evt-123";
    String eventType = "TestEvent";
    Instant timestamp = Instant.now();
    String data = "test-data";

    UsuarioMensaje<String> mensaje1 =
        new UsuarioMensaje<>(eventId, eventType, "usuarios-service", timestamp, data);
    UsuarioMensaje<String> mensaje2 =
        new UsuarioMensaje<>(eventId, eventType, "usuarios-service", timestamp, data);
    UsuarioMensaje<String> mensaje3 =
        new UsuarioMensaje<>("different-id", eventType, "usuarios-service", timestamp, data);

    // Then
    assertThat(mensaje1).isEqualTo(mensaje2).isNotEqualTo(mensaje3);
    assertThat(mensaje1.hashCode())
        .isEqualTo(mensaje2.hashCode())
        .isNotEqualTo(mensaje3.hashCode());
  }

  @Test
  @DisplayName("Debe tener toString con información útil")
  void debeTenerToStringConInformacionUtil() {
    // Given
    UsuarioMensaje<String> mensaje = UsuarioMensaje.of("TestEvent", "test-data");

    // When
    String toString = mensaje.toString();

    // Then
    assertThat(toString)
        .contains("UsuarioMensaje")
        .contains("eventType=TestEvent")
        .contains("service=usuarios-service")
        .contains("data=test-data");
  }

  @Test
  @DisplayName("Debe crear múltiples mensajes con diferentes eventTypes")
  void debeCrearMultiplesMensajesConDiferentesEventTypes() {
    // When
    UsuarioMensaje<String> mensajeCreado = UsuarioMensaje.of("UsuarioCreado", "data1");
    UsuarioMensaje<String> mensajeActualizado = UsuarioMensaje.of("UsuarioActualizado", "data2");
    UsuarioMensaje<String> mensajeEliminado = UsuarioMensaje.of("UsuarioEliminado", "data3");

    // Then
    assertThat(mensajeCreado.eventType()).isEqualTo("UsuarioCreado");
    assertThat(mensajeActualizado.eventType()).isEqualTo("UsuarioActualizado");
    assertThat(mensajeEliminado.eventType()).isEqualTo("UsuarioEliminado");

    assertThat(mensajeCreado.service())
        .isEqualTo(mensajeActualizado.service())
        .isEqualTo(mensajeEliminado.service())
        .isEqualTo("usuarios-service");
  }

  // Clase helper para tests
  private record TestData(String name, int value) {}
}
