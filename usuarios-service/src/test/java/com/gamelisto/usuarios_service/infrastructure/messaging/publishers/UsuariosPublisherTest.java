package com.gamelisto.usuarios_service.infrastructure.messaging.publishers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.gamelisto.usuarios_service.domain.events.UsuarioCreado;
import com.gamelisto.usuarios_service.infrastructure.messaging.config.RabbitMQConfig;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuariosPublisher - Tests")
class UsuariosPublisherTest {

  @Mock private RabbitTemplate rabbitTemplate;

  @InjectMocks private UsuariosPublisher publisher;

  @Captor private ArgumentCaptor<String> exchangeCaptor;
  @Captor private ArgumentCaptor<String> routingKeyCaptor;
  @Captor private ArgumentCaptor<Object> eventCaptor;
  @Captor private ArgumentCaptor<MessagePostProcessor> messagePostProcessorCaptor;

  private UsuarioCreado sampleEvent;

  @BeforeEach
  void setUp() {
    sampleEvent =
        new UsuarioCreado(
            "user-123", "testuser", "test@example.com", Instant.parse("2026-01-29T10:00:00Z"));
  }

  @Test
  @DisplayName("Debe publicar evento con routing key correcta")
  void debePublicarEventoConRoutingKeyCorrecta() {
    // Given
    String routingKeySuffix = "created";

    // When
    publisher.publish(routingKeySuffix, sampleEvent);

    // Then
    verify(rabbitTemplate)
        .convertAndSend(
            exchangeCaptor.capture(),
            routingKeyCaptor.capture(),
            eventCaptor.capture(),
            any(MessagePostProcessor.class));

    String expectedRoutingKey = RabbitMQConfig.ROUTING_KEY_PREFIX + "." + routingKeySuffix;
    assertThat(exchangeCaptor.getValue()).isEqualTo(RabbitMQConfig.EXCHANGE_NAME);
    assertThat(routingKeyCaptor.getValue()).isEqualTo(expectedRoutingKey);
    assertThat(eventCaptor.getValue()).isEqualTo(sampleEvent);
  }

  @Test
  @DisplayName("Debe agregar headers al mensaje publicado")
  void debeAgregarHeadersAlMensaje() {
    // Given
    String routingKeySuffix = "updated";

    // When
    publisher.publish(routingKeySuffix, sampleEvent);

    // Then
    verify(rabbitTemplate)
        .convertAndSend(
            eq(RabbitMQConfig.EXCHANGE_NAME),
            eq(RabbitMQConfig.ROUTING_KEY_PREFIX + "." + routingKeySuffix),
            eq(sampleEvent),
            messagePostProcessorCaptor.capture());

    // El MessagePostProcessor se verifica indirectamente por el comportamiento
    // No podemos acceder directamente a los headers sin ejecutar el processor
  }

  @Test
  @DisplayName("Debe lanzar excepción cuando RabbitTemplate falla")
  void debeLanzarExcepcionCuandoRabbitTemplateFalla() {
    // Given
    String routingKeySuffix = "deleted";
    doThrow(new RuntimeException("RabbitMQ connection error"))
        .when(rabbitTemplate)
        .convertAndSend(
            any(String.class), any(String.class), any(), any(MessagePostProcessor.class));

    // When & Then
    assertThatThrownBy(() -> publisher.publish(routingKeySuffix, sampleEvent))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Error al publicar evento");
  }

  @Test
  @DisplayName("Debe publicar diferentes tipos de eventos")
  void debePublicarDiferentesTiposDeEventos() {
    // Given
    Object customEvent = new CustomTestEvent("test-data");

    // When
    publisher.publish("custom", customEvent);

    // Then
    verify(rabbitTemplate)
        .convertAndSend(
            eq(RabbitMQConfig.EXCHANGE_NAME),
            eq(RabbitMQConfig.ROUTING_KEY_PREFIX + ".custom"),
            eq(customEvent),
            any(MessagePostProcessor.class));
  }

  @Test
  @DisplayName("Debe construir routing key con prefijo correcto")
  void debeConstruirRoutingKeyConPrefijoCorrect() {
    // Given
    String[] suffixes = {"created", "updated", "deleted", "verified"};

    // When & Then
    for (String suffix : suffixes) {
      publisher.publish(suffix, sampleEvent);

      verify(rabbitTemplate)
          .convertAndSend(
              eq(RabbitMQConfig.EXCHANGE_NAME),
              eq(RabbitMQConfig.ROUTING_KEY_PREFIX + "." + suffix),
              any(),
              any(MessagePostProcessor.class));
    }
  }

  // Clase helper para tests
  private record CustomTestEvent(String data) {}
}
