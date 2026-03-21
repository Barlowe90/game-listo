package com.gamelisto.social.infrastructure.out.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    name = "messaging.rabbitmq.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class RabbitMQConfig {
  public static final String QUEUE_NAME = "social";
  public static final String EXCHANGE = "gamelisto.eventos";
  public static final String BINDING_USUARIOS_KEY = "usuarios.#";
  public static final String BINDING_BIBLIOTECA_KEY = "biblioteca.#";
  public static final String BINDING_ESTADO_ACTUALIZADO_KEY = "estado.actualizado";

  @Bean
  public TopicExchange gamelistoExchange() {
    return new TopicExchange(EXCHANGE, true, false);
  }

  @Bean
  public Queue socialQueue() {
    return QueueBuilder.durable(QUEUE_NAME).build();
  }

  @Bean
  public Binding bindingUsuarios(Queue socialQueue, TopicExchange gamelistoExchange) {
    return BindingBuilder.bind(socialQueue).to(gamelistoExchange).with(BINDING_USUARIOS_KEY);
  }

  @Bean
  public Binding bindingBiblioteca(Queue socialQueue, TopicExchange gamelistoExchange) {
    return BindingBuilder.bind(socialQueue).to(gamelistoExchange).with(BINDING_BIBLIOTECA_KEY);
  }

  @Bean
  public Binding bindingEstadoActualizado(Queue socialQueue, TopicExchange gamelistoExchange) {
    return BindingBuilder.bind(socialQueue)
        .to(gamelistoExchange)
        .with(BINDING_ESTADO_ACTUALIZADO_KEY);
  }
}
