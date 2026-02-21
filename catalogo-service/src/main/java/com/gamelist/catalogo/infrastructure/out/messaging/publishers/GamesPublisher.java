package com.gamelist.catalogo.infrastructure.out.messaging.publishers;

import com.gamelist.catalogo.domain.repositories.IEventPublisherPort;
import com.gamelist.catalogo.infrastructure.exceptions.InfrastructureException;
import com.gamelist.catalogo.infrastructure.out.messaging.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(RabbitTemplate.class)
public class GamesPublisher implements IEventPublisherPort {

  private final RabbitTemplate rabbitTemplate;

  public GamesPublisher(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  @Override
  public void publish(String routingKeySuffix, Object event) {
    try {
      String routingKey = RabbitMQConfig.ROUTING_KEY_PREFIX + "." + routingKeySuffix;

      rabbitTemplate.convertAndSend(
          routingKey,
          message -> {
            message.getMessageProperties().setHeader("eventType", event.getClass().getSimpleName());
            message.getMessageProperties().setHeader("service", "catalogo-service");
            return message;
          });

    } catch (Exception e) {
      throw new InfrastructureException("Error al publicar evento en RabbitMQ", e);
    }
  }
}
