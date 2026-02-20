package com.gamelist.catalogo.infrastructure.out.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(
    name = "messaging.rabbitmq.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class MessagingConfig {

  // Exchange principal para eventos de catálogo
  public static final String CATALOG_EXCHANGE = "catalog.events";

  // Colas para diferentes tipos de eventos
  public static final String GAME_UPSERTED_QUEUE = "catalog.game.upserted";
  public static final String SYNC_BATCH_COMPLETED_QUEUE = "catalog.sync.batch.completed";
  public static final String SYNC_COMPLETED_QUEUE = "catalog.sync.completed";
  public static final String PLATFORMS_SYNC_COMPLETED_QUEUE = "catalog.platforms.sync.completed";

  // Routing keys
  public static final String GAME_UPSERTED_ROUTING_KEY = "catalog.game.upserted";
  public static final String SYNC_BATCH_COMPLETED_ROUTING_KEY = "catalog.sync.batch.completed";
  public static final String SYNC_COMPLETED_ROUTING_KEY = "catalog.sync.completed";
  public static final String PLATFORMS_SYNC_COMPLETED_ROUTING_KEY =
      "catalog.sync.platforms.completed";

  @Bean
  public TopicExchange catalogExchange() {
    return new TopicExchange(CATALOG_EXCHANGE, true, false);
  }

  @Bean
  public Queue gameUpsertedQueue() {
    return new Queue(GAME_UPSERTED_QUEUE, true);
  }

  @Bean
  public Queue syncBatchCompletedQueue() {
    return new Queue(SYNC_BATCH_COMPLETED_QUEUE, true);
  }

  @Bean
  public Queue syncCompletedQueue() {
    return new Queue(SYNC_COMPLETED_QUEUE, true);
  }

  @Bean
  public Queue platformsSyncCompletedQueue() {
    return new Queue(PLATFORMS_SYNC_COMPLETED_QUEUE, true);
  }

  @Bean
  public Binding gameUpsertedBinding(Queue gameUpsertedQueue, TopicExchange catalogExchange) {
    return BindingBuilder.bind(gameUpsertedQueue)
        .to(catalogExchange)
        .with(GAME_UPSERTED_ROUTING_KEY);
  }

  @Bean
  public Binding syncBatchCompletedBinding(
      Queue syncBatchCompletedQueue, TopicExchange catalogExchange) {
    return BindingBuilder.bind(syncBatchCompletedQueue)
        .to(catalogExchange)
        .with(SYNC_BATCH_COMPLETED_ROUTING_KEY);
  }

  @Bean
  public Binding syncCompletedBinding(Queue syncCompletedQueue, TopicExchange catalogExchange) {
    return BindingBuilder.bind(syncCompletedQueue)
        .to(catalogExchange)
        .with(SYNC_COMPLETED_ROUTING_KEY);
  }

  @Bean
  public Binding platformsSyncCompletedBinding(
      Queue platformsSyncCompletedQueue, TopicExchange catalogExchange) {
    return BindingBuilder.bind(platformsSyncCompletedQueue)
        .to(catalogExchange)
        .with(PLATFORMS_SYNC_COMPLETED_ROUTING_KEY);
  }

  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(jsonMessageConverter());
    return template;
  }
}
