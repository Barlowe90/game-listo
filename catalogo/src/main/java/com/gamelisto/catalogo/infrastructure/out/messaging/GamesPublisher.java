package com.gamelisto.catalogo.infrastructure.out.messaging;

import com.gamelisto.catalogo.domain.GamePublisherRepositorio;
import com.gamelisto.catalogo.domain.events.GameCreado;
import com.gamelisto.catalogo.infrastructure.exceptions.InfrastructureException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GamesPublisher implements GamePublisherRepositorio {

  private final RabbitTemplate rabbitTemplate;

  private static final Logger logger = LoggerFactory.getLogger(GamesPublisher.class);

  @Override
  public void publicarGameCreado(GameCreado evento) {
    logger.info(
        "Estoy en la implementacion del repjo para llamar a publicar evento"); // ya no entra
    publicar(RabbitMQConfig.RK_GAME_CREADO, evento);
  }

  private void publicar(String routingKey, Object evento) {
    try {
      MessagePostProcessor mpp =
          message -> {
            message
                .getMessageProperties()
                .setHeader("eventType", evento.getClass().getSimpleName());
            return message;
          };

      rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, routingKey, evento, mpp);
      logger.info("Evento publicado: {} → {}", evento.getClass().getSimpleName(), routingKey);
    } catch (Exception e) {
      throw new InfrastructureException("Error al publicar evento en RabbitMQ", e);
    }
  }
}
