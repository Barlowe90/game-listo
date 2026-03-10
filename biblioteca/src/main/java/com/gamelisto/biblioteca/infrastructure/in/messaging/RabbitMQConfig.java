package com.gamelisto.biblioteca.infrastructure.in.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    name = "messaging.rabbitmq.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class RabbitMQConfig {

  public static final String QUEUE_NAME = "biblioteca";
  public static final String EXCHANGE = "gamelisto.eventos";
  public static final String BINDING_USUARIOS_KEY = "usuarios.#";
  public static final String BINDING_GAMES_KEY = "games.#";
  public static final String RK_ESTADO_ACTUALIZADO = "estado.actualizado";

  @Bean
  public TopicExchange gamelistoExchange() {
    return new TopicExchange(EXCHANGE, true, false);
  }

  @Bean
  public Queue bibliotecaQueue() {
    return QueueBuilder.durable(QUEUE_NAME).build();
  }

  @Bean
  public Binding bindingUsuarios(Queue bibliotecaQueue, TopicExchange gamelistoExchange) {
    return BindingBuilder.bind(bibliotecaQueue).to(gamelistoExchange).with(BINDING_USUARIOS_KEY);
  }

  @Bean
  public Binding bindingGames(Queue bibliotecaQueue, TopicExchange gamelistoExchange) {
    return BindingBuilder.bind(bibliotecaQueue).to(gamelistoExchange).with(BINDING_GAMES_KEY);
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(new JacksonJsonMessageConverter());
    return template;
  }
}
