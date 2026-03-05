package com.gamelisto.usuarios.infrastructure.out.messaging;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    name = "messaging.rabbitmq.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class RabbitMQConfig {

  public static final String EXCHANGE = "gamelisto.eventos";
  public static final String RK_USUARIO_CREADO = "usuarios.creado";
  public static final String RK_USUARIO_ELIMINADO = "usuarios.eliminado";

  @Bean
  public TopicExchange gamelistoExchange() {
    return new TopicExchange(EXCHANGE, true, false);
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new JacksonJsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(
      ConnectionFactory connectionFactory, MessageConverter converter) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(converter);
    return template;
  }
}
