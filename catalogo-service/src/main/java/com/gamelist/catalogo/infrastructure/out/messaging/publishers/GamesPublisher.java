package com.gamelist.catalogo.infrastructure.out.messaging.publishers;

import com.gamelist.catalogo.domain.repositories.IGamePublisher;
import com.gamelist.catalogo.infrastructure.exceptions.InfrastructureException;
import com.gamelist.catalogo.infrastructure.out.messaging.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(RabbitTemplate.class)
public class GamesPublisher implements IGamePublisher {

  private final RabbitTemplate rabbitTemplate;

  private static final Logger logger = LoggerFactory.getLogger(GamesPublisher.class);

  public GamesPublisher(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void publish(String routingKeySuffix, Object event) {
    try {
      String routingKey = RabbitMQConfig.ROUTING_KEY_PREFIX + "." + routingKeySuffix;

      rabbitTemplate.convertAndSend(
          RabbitMQConfig.EXCHANGE_NAME,
          routingKey,
          event,
          message -> {
            String eventType = event.getClass().getSimpleName();
            message.getMessageProperties().setHeader("eventType", eventType);
            message.getMessageProperties().setHeader("service", "catalogo-service");
            message
                .getMessageProperties()
                .setHeader("publishedAt", java.time.Instant.now().toString());
            return message;
          });

      logger.info(
          "Evento publicado: {} a routing key: {}", event.getClass().getSimpleName(), routingKey);

    } catch (Exception e) {
      throw new InfrastructureException("Error al publicar evento en RabbitMQ", e);
    }
  }
}
