package com.gamelisto.busquedas.infrastructure.in.messaging;

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

  public static final String QUEUE_NAME = "busquedas";
  public static final String EXCHANGE = "gamelisto.eventos";
  public static final String BINDING_GAMES_KEY = "games.#";

  @Bean
  public TopicExchange gamelistoExchange() {
    return new TopicExchange(EXCHANGE, true, false);
  }

  @Bean
  public Queue busquedasQueue() {
    return QueueBuilder.durable(QUEUE_NAME).build();
  }

  @Bean
  public Binding bindingGames(Queue busquedasQueue, TopicExchange gamelistoExchange) {
    return BindingBuilder.bind(busquedasQueue).to(gamelistoExchange).with(BINDING_GAMES_KEY);
  }
}
