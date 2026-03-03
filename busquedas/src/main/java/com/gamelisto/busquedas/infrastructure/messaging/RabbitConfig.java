package com.gamelisto.busquedas.infrastructure.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

  @Value("${busquedas.rabbit.exchange}")
  private String exchange;

  @Value("${busquedas.rabbit.queue}")
  private String queue;

  @Value("${busquedas.rabbit.routing.created}")
  private String routingCreated;

  @Bean
  public TopicExchange catalogExchange() {
    return new TopicExchange(exchange, true, false);
  }

  @Bean
  public Queue catalogGamesQueue() {
    return QueueBuilder.durable(queue).build();
  }

  @Bean
  public Binding catalogGamesBinding(Queue catalogGamesQueue, TopicExchange catalogExchange) {
    return BindingBuilder.bind(catalogGamesQueue).to(catalogExchange).with(routingCreated);
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new JacksonJsonMessageConverter();
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
