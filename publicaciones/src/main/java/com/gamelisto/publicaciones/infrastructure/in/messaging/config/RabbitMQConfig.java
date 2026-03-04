package com.gamelisto.publicaciones.infrastructure.in.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
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

  public static final String QUEUE_NAME = "publicaciones";
  public static final String EXCHANGE = "gamelisto.eventos";
  public static final String BINDING_USUARIOS_KEY = "usuarios.#";
  public static final String BINDING_GAMES_KEY = "games.#";

  @Bean
  public TopicExchange gamelistoExchange() {
    return new TopicExchange(EXCHANGE, true, false);
  }

  @Bean
  public Queue publicacionesQueue() {
    return QueueBuilder.durable(QUEUE_NAME).build();
  }

  @Bean
  public Binding bindingUsuarios(Queue publicacionesQueue, TopicExchange gamelistoExchange) {
    return BindingBuilder.bind(publicacionesQueue).to(gamelistoExchange).with(BINDING_USUARIOS_KEY);
  }

  @Bean
  public Binding bindingGames(Queue publicacionesQueue, TopicExchange gamelistoExchange) {
    return BindingBuilder.bind(publicacionesQueue).to(gamelistoExchange).with(BINDING_GAMES_KEY);
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new JacksonJsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(
      ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(jsonMessageConverter);
    return template;
  }

  @Bean
  public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
      ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setMessageConverter(jsonMessageConverter);
    factory.setDefaultRequeueRejected(false);
    return factory;
  }
}
