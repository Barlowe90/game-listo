package com.gamelisto.usuarios_service.infrastructure.messaging.listeners;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuariosListener - Tests")
class UsuariosListenerTest {

  @Mock private Message message;

  @Mock private MessageProperties messageProperties;

  @InjectMocks private UsuariosListener listener;

  private Map<String, Object> headers;

  @BeforeEach
  void setUp() {
    headers = new HashMap<>();
    when(message.getMessageProperties()).thenReturn(messageProperties);
    when(messageProperties.getHeaders()).thenReturn(headers);
  }

  @Test
  @DisplayName("Debe procesar evento con headers completos correctamente")
  void debeProcesarEventoConHeadersCompletos() {
    // Given
    headers.put("eventType", "UsuarioCreado");
    headers.put("service", "auth-service");
    Object payload = new TestPayload("test-data");

    // When & Then - No debe lanzar excepción
    assertThatCode(() -> listener.handleEvent(message, payload)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe procesar evento sin service header")
  void debeProcesarEventoSinServiceHeader() {
    // Given
    headers.put("eventType", "UsuarioActualizado");
    Object payload = new TestPayload("test-data");

    // When & Then
    assertThatCode(() -> listener.handleEvent(message, payload)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe procesar evento sin eventType header")
  void debeProcesarEventoSinEventTypeHeader() {
    // Given
    headers.put("service", "auth-service");
    Object payload = new TestPayload("test-data");

    // When & Then
    assertThatCode(() -> listener.handleEvent(message, payload)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe procesar evento con headers vacíos")
  void debeProcesarEventoConHeadersVacios() {
    // Given - headers está vacío por defecto
    Object payload = new TestPayload("test-data");

    // When & Then
    assertThatCode(() -> listener.handleEvent(message, payload)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe procesar diferentes tipos de payloads")
  void debeProcesarDiferentesTiposDePayloads() {
    // Given
    headers.put("eventType", "TestEvent");
    headers.put("service", "test-service");

    Object stringPayload = "string payload";
    Object mapPayload = Map.of("key", "value");
    Object customPayload = new TestPayload("custom");

    // When & Then
    assertThatCode(() -> listener.handleEvent(message, stringPayload)).doesNotThrowAnyException();
    assertThatCode(() -> listener.handleEvent(message, mapPayload)).doesNotThrowAnyException();
    assertThatCode(() -> listener.handleEvent(message, customPayload)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe lanzar RuntimeException cuando ocurre error en procesamiento")
  void debeLanzarRuntimeExceptionCuandoOcurreError() {
    // Given
    headers.put("eventType", "ErrorEvent");
    when(messageProperties.getHeaders()).thenThrow(new RuntimeException("Simulated error"));

    // When & Then
    assertThatThrownBy(() -> listener.handleEvent(message, new TestPayload("data")))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Error al procesar evento");
  }

  @Test
  @DisplayName("Debe manejar payload null")
  void debeManejarPayloadNull() {
    // Given
    headers.put("eventType", "NullPayloadEvent");
    headers.put("service", "test-service");

    // When & Then
    assertThatCode(() -> listener.handleEvent(message, null)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe procesar eventos con headers de diferentes tipos de datos")
  void debeProcesarEventosConHeadersDeDiferentesTiposDeDatos() {
    // Given
    headers.put("eventType", "ComplexEvent");
    headers.put("service", "complex-service");
    headers.put("timestamp", System.currentTimeMillis());
    headers.put("version", 1);
    headers.put("isRetry", false);

    Object payload = new TestPayload("complex-data");

    // When & Then
    assertThatCode(() -> listener.handleEvent(message, payload)).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe procesar múltiples eventos consecutivamente")
  void debeProcesarMultiplesEventosConsecutivamente() {
    // Given
    headers.put("eventType", "Event1");
    Object payload1 = new TestPayload("data1");
    Object payload2 = new TestPayload("data2");
    Object payload3 = new TestPayload("data3");

    // When & Then
    assertThatCode(
            () -> {
              listener.handleEvent(message, payload1);
              listener.handleEvent(message, payload2);
              listener.handleEvent(message, payload3);
            })
        .doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Debe procesar evento con eventType vacío")
  void debeProcesarEventoConEventTypeVacio() {
    // Given
    headers.put("eventType", "");
    headers.put("service", "test-service");
    Object payload = new TestPayload("test");

    // When & Then
    assertThatCode(() -> listener.handleEvent(message, payload)).doesNotThrowAnyException();
  }

  // Clase helper para tests
  private record TestPayload(String data) {}
}
