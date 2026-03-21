package com.gamelisto.social.infrastructure.out.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;

@DisplayName("RabbitMQ Configuration - Tests (social)")
class RabbitMQConfigTest {

  private RabbitMQConfig config;

  @BeforeEach
  void setUp() {
    config = new RabbitMQConfig();
  }

  @Test
  @DisplayName("debe crear el exchange principal correctamente")
  void debeCrearExchangePrincipal() {
    TopicExchange exchange = config.gamelistoExchange();

    assertThat(exchange).isNotNull();
    assertThat(exchange.getName()).isEqualTo(RabbitMQConfig.EXCHANGE);
    assertThat(exchange.isDurable()).isTrue();
    assertThat(exchange.isAutoDelete()).isFalse();
  }

  @Test
  @DisplayName("debe crear la cola social correctamente")
  void debeCrearColaSocial() {
    Queue queue = config.socialQueue();

    assertThat(queue).isNotNull();
    assertThat(queue.getName()).isEqualTo(RabbitMQConfig.QUEUE_NAME);
    assertThat(queue.isDurable()).isTrue();
  }

  @Test
  @DisplayName("debe crear el binding de estados actualizados")
  void debeCrearBindingDeEstadosActualizados() {
    Queue queue = config.socialQueue();
    TopicExchange exchange = config.gamelistoExchange();

    Binding binding = config.bindingEstadoActualizado(queue, exchange);

    assertThat(binding.getDestination()).isEqualTo(RabbitMQConfig.QUEUE_NAME);
    assertThat(binding.getExchange()).isEqualTo(RabbitMQConfig.EXCHANGE);
    assertThat(binding.getRoutingKey()).isEqualTo(RabbitMQConfig.BINDING_ESTADO_ACTUALIZADO_KEY);
  }
}
