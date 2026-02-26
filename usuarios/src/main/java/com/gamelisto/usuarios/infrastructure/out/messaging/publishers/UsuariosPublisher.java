package com.gamelisto.usuarios.infrastructure.out.messaging.publishers;

import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import com.gamelisto.usuarios.infrastructure.exceptions.InfrastructureException;
import com.gamelisto.usuarios.infrastructure.out.messaging.config.RabbitMQConfig;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(RabbitTemplate.class)
public class UsuariosPublisher implements IUsuarioPublisher {

  private static final Logger logger = LoggerFactory.getLogger(UsuariosPublisher.class);
  private final RabbitTemplate rabbitTemplate;

  public UsuariosPublisher(RabbitTemplate rabbitTemplate) {
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
            message.getMessageProperties().setHeader("eventType", event.getClass().getSimpleName());
            message.getMessageProperties().setHeader("publishedAt", Instant.now().toString());
            message.getMessageProperties().setHeader("service", "usuarios");
            return message;
          });

      logger.info(
          "Evento publicado: {} a routing key: {}", event.getClass().getSimpleName(), routingKey);

    } catch (Exception e) {
      throw new InfrastructureException("Error al publicar evento en RabbitMQ", e);
    }
  }
}
