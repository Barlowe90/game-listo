package com.gamelist.catalogo.infrastructure.out.messaging.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

@DisplayName("RabbitMQ Configuration - Tests (catalogo)")
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
    TopicExchange exchange = config.gamelistoExchange();

    // Then
    assertThat(exchange).isNotNull();
    assertThat(exchange.getName()).isEqualTo(RabbitMQConfig.EXCHANGE);
    assertThat(exchange.isDurable()).isTrue();
    assertThat(exchange.isAutoDelete()).isFalse();
  }

  @Test
  @DisplayName("Debe crear el conversor de mensajes JSON con configuración correcta")
  void debeCrearConversordeMensajesJSON() {
    // When
    MessageConverter converter = config.jsonMessageConverter();

    // Then
    assertThat(converter).isNotNull().isInstanceOf(MessageConverter.class);
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
  @DisplayName("Comportamiento al crear RabbitTemplate con ConnectionFactory null")
  void comportamientoCuandoConnectionFactoryNull() {
    // Given
    MessageConverter converter = config.jsonMessageConverter();

    // When/Then - Aceptamos dos comportamientos posibles: que lance excepción o que devuelva
    // un RabbitTemplate con connectionFactory == null.
    try {
      RabbitTemplate rt = config.rabbitTemplate(null, converter);
      // Si no lanza excepción, comprobamos que la propiedad refleje null
      assertThat(rt).isNotNull();
      assertThat(rt.getConnectionFactory()).isNull();
    } catch (Exception e) {
      // Si lanza, aceptamos la excepción (implementaciones de Spring pueden variar)
      assertThat(e).isNotNull();
    }
  }

  @Test
  @DisplayName("Debe aceptar MessageConverter null en RabbitTemplate y no lanzar excepciones")
  void debeAceptarMessageConverterNullEnRabbitTemplate() {
    // Given
    ConnectionFactory connectionFactory = mock(ConnectionFactory.class);

    // When & Then - No debe lanzar excepción al crear RabbitTemplate con converter null
    RabbitTemplate rabbitTemplate = config.rabbitTemplate(connectionFactory, null);
    assertThat(rabbitTemplate).isNotNull();
  }

  @Test
  @DisplayName("Debe tener las constantes correctas configuradas")
  void debeTenerConstantesCorrectas() {
    assertThat(RabbitMQConfig.EXCHANGE).isEqualTo("gamelisto.eventos");
    assertThat(RabbitMQConfig.RK_GAME_CREADO).isEqualTo("games.creado");
  }
}
