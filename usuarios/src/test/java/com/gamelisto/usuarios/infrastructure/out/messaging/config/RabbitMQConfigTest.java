package com.gamelisto.usuarios.infrastructure.out.messaging.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

@DisplayName("RabbitMQ Configuration - Tests")
class RabbitMQConfigTest {

  private RabbitMQConfig config;

  @BeforeEach
  void setUp() {
    config = new RabbitMQConfig();
  }

  @Test
  @DisplayName("Debe crear el exchange principal correctamente")
  void debeCrearExchangePrincipal() {
    // When
    TopicExchange exchange = config.exchange();

    // Then
    assertThat(exchange).isNotNull();
    assertThat(exchange.getName()).isEqualTo("bus");
    assertThat(exchange.isDurable()).isTrue();
    assertThat(exchange.isAutoDelete()).isFalse();
  }

  @Test
  @DisplayName("Debe crear el exchange de Dead Letter Queue correctamente")
  void debeCrearDLQExchange() {
    // When
    TopicExchange dlqExchange = config.dlqExchange();

    // Then
    assertThat(dlqExchange).isNotNull();
    assertThat(dlqExchange.getName()).isEqualTo("usuarios.dlq.exchange");
    assertThat(dlqExchange.isDurable()).isTrue();
    assertThat(dlqExchange.isAutoDelete()).isFalse();
  }

  @Test
  @DisplayName("Debe crear la cola principal con configuración DLQ y TTL")
  void debeCrearColaPrincipalConDLQyTTL() {
    // When
    Queue queue = config.queue();

    // Then
    assertThat(queue).isNotNull();
    assertThat(queue.getName()).isEqualTo("usuarios.queue");
    assertThat(queue.isDurable()).isTrue();

    // Verificar argumentos de DLQ
    assertThat(queue.getArguments())
        .containsEntry("x-dead-letter-exchange", "usuarios.dlq.exchange")
        .containsEntry("x-dead-letter-routing-key", "dlq.usuarios")
        .containsEntry("x-message-ttl", 30000);
  }

  @Test
  @DisplayName("Debe crear la cola de Dead Letter Queue")
  void debeCrearDeadLetterQueue() {
    // When
    Queue dlq = config.deadLetterQueue();

    // Then
    assertThat(dlq).isNotNull();
    assertThat(dlq.getName()).isEqualTo("usuarios.dlq");
    assertThat(dlq.isDurable()).isTrue();
  }

  @Test
  @DisplayName("Debe crear el binding entre queue y exchange")
  void debeCrearBindingQueueExchange() {
    // Given
    Queue queue = config.queue();
    TopicExchange exchange = config.exchange();

    // When
    Binding binding = config.binding(queue, exchange);

    // Then
    assertThat(binding).isNotNull();
    assertThat(binding.getDestination()).isEqualTo("usuarios.queue");
    assertThat(binding.getExchange()).isEqualTo("bus");
    assertThat(binding.getRoutingKey()).isEqualTo("bus.*.#");
  }

  @Test
  @DisplayName("Debe crear el binding de DLQ")
  void debeCrearBindingDLQ() {
    // Given
    Queue dlq = config.deadLetterQueue();
    TopicExchange dlqExchange = config.dlqExchange();

    // When
    Binding binding = config.dlqBinding(dlq, dlqExchange);

    // Then
    assertThat(binding).isNotNull();
    assertThat(binding.getDestination()).isEqualTo("usuarios.dlq");
    assertThat(binding.getExchange()).isEqualTo("usuarios.dlq.exchange");
    assertThat(binding.getRoutingKey()).isEqualTo("dlq.usuarios");
  }

  @Test
  @DisplayName("Debe crear el conversor de mensajes JSON con configuración correcta")
  void debeCrearConversordeMensajesJSON() {
    // When
    MessageConverter converter = config.jsonMessageConverter();

    // Then
    assertThat(converter).isNotNull().isInstanceOf(Jackson2JsonMessageConverter.class);
  }

  @Test
  @DisplayName("Debe crear RabbitTemplate con ConnectionFactory y MessageConverter")
  void debeCrearRabbitTemplate() {
    // Given
    ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
    MessageConverter converter = config.jsonMessageConverter();

    // When
    RabbitTemplate rabbitTemplate = config.rabbitTemplate(connectionFactory, converter);

    // Then
    assertThat(rabbitTemplate).isNotNull();
    assertThat(rabbitTemplate.getConnectionFactory()).isEqualTo(connectionFactory);
    assertThat(rabbitTemplate.getMessageConverter()).isEqualTo(converter);
  }

  @Test
  @DisplayName("Debe lanzar excepción si ConnectionFactory es null en RabbitTemplate")
  void debeLanzarExcepcionSiConnectionFactoryEsNullEnRabbitTemplate() {
    // Given
    MessageConverter converter = config.jsonMessageConverter();

    // When & Then
    assertThatThrownBy(() -> config.rabbitTemplate(null, converter))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("connectionFactory must not be null");
  }

  @Test
  @DisplayName("Debe lanzar excepción si MessageConverter es null en RabbitTemplate")
  void debeLanzarExcepcionSiMessageConverterEsNullEnRabbitTemplate() {
    // Given
    ConnectionFactory connectionFactory = mock(ConnectionFactory.class);

    // When & Then
    assertThatThrownBy(() -> config.rabbitTemplate(connectionFactory, null))
        .isInstanceOf(NullPointerException.class)
        .hasMessageContaining("converter must not be null");
  }

  @Test
  @DisplayName("Debe crear RabbitListenerContainerFactory con configuración correcta")
  void debeCrearRabbitListenerContainerFactory() {
    // Given
    ConnectionFactory connectionFactory = mock(ConnectionFactory.class);
    MessageConverter converter = config.jsonMessageConverter();

    // When
    var factory = config.rabbitListenerContainerFactory(connectionFactory, converter);

    // Then
    assertThat(factory).isNotNull();
  }

  @Test
  @DisplayName("Debe tener las constantes correctas configuradas")
  void debeTenerConstantesCorrectas() {
    assertThat(RabbitMQConfig.EXCHANGE_NAME).isEqualTo("bus");
    assertThat(RabbitMQConfig.QUEUE_NAME).isEqualTo("usuarios.queue");
    assertThat(RabbitMQConfig.DLQ_NAME).isEqualTo("usuarios.dlq");
    assertThat(RabbitMQConfig.DLQ_EXCHANGE_NAME).isEqualTo("usuarios.dlq.exchange");
    assertThat(RabbitMQConfig.ROUTING_KEY_PREFIX).isEqualTo("bus.usuarios");
    assertThat(RabbitMQConfig.BINDING_KEY).isEqualTo("bus.*.#");
  }
}
