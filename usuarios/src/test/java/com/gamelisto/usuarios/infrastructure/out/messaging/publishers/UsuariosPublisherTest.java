package com.gamelisto.usuarios.infrastructure.out.messaging.publishers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.gamelisto.usuarios.domain.events.UsuarioCreado;
import com.gamelisto.usuarios.domain.events.UsuarioEliminado;
import com.gamelisto.usuarios.infrastructure.out.messaging.RabbitMQConfig;
import java.time.Instant;

import com.gamelisto.usuarios.infrastructure.out.messaging.UsuariosPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuariosPublisher - Tests")
class UsuariosPublisherTest {

  @Mock private RabbitTemplate rabbitTemplate;

  @InjectMocks private UsuariosPublisher publisher;

  @Captor private ArgumentCaptor<String> exchangeCaptor;
  @Captor private ArgumentCaptor<String> routingKeyCaptor;
  @Captor private ArgumentCaptor<Object> eventCaptor;

  private UsuarioCreado sampleEvent;

  @BeforeEach
  void setUp() {
    sampleEvent =
        new UsuarioCreado(
            "user-123",
            "testuser",
            "test@example.com",
            "avatar.png",
            "USER",
            "ESP",
            "PENDIENTE_DE_VERIFICACION",
            null,
            null);
  }

  @Test
  @DisplayName("Debe publicar UsuarioCreado con exchange y routing key correctos")
  void debePublicarUsuarioCreadoConExchangeYRk() {
    // When
    publisher.publicarUsuarioCreado(sampleEvent);

    // Then
    verify(rabbitTemplate)
        .convertAndSend(
            exchangeCaptor.capture(),
            routingKeyCaptor.capture(),
            eventCaptor.capture(),
            any(org.springframework.amqp.core.MessagePostProcessor.class));

    assertThat(exchangeCaptor.getValue()).isEqualTo(RabbitMQConfig.EXCHANGE);
    assertThat(routingKeyCaptor.getValue()).isEqualTo(RabbitMQConfig.RK_USUARIO_CREADO);
    assertThat(eventCaptor.getValue()).isEqualTo(sampleEvent);
  }

  @Test
  @DisplayName("Debe publicar UsuarioEliminado con exchange y routing key correctos")
  void debePublicarUsuarioEliminadoConExchangeYRk() {
    // Given
    UsuarioEliminado evento = UsuarioEliminado.of("user-123");

    // When
    publisher.publicarUsuarioEliminado(evento);

    // Then
    verify(rabbitTemplate)
        .convertAndSend(
            exchangeCaptor.capture(),
            routingKeyCaptor.capture(),
            eventCaptor.capture(),
            any(org.springframework.amqp.core.MessagePostProcessor.class));

    assertThat(exchangeCaptor.getValue()).isEqualTo(RabbitMQConfig.EXCHANGE);
    assertThat(routingKeyCaptor.getValue()).isEqualTo(RabbitMQConfig.RK_USUARIO_ELIMINADO);
    assertThat(eventCaptor.getValue()).isEqualTo(evento);
  }

  @Test
  @DisplayName("Debe lanzar InfrastructureException cuando RabbitTemplate falla")
  void debeLanzarExcepcionCuandoRabbitTemplateFalla() {
    // Given
    doThrow(new RuntimeException("RabbitMQ connection error"))
        .when(rabbitTemplate)
        .convertAndSend(
            eq(RabbitMQConfig.EXCHANGE),
            eq(RabbitMQConfig.RK_USUARIO_CREADO),
            any(Object.class),
            any(org.springframework.amqp.core.MessagePostProcessor.class));

    // When & Then
    assertThatThrownBy(() -> publisher.publicarUsuarioCreado(sampleEvent))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Error al publicar evento");
  }
}
