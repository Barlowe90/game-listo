package com.gamelisto.catalogo.infrastructure.out.messaging;

import com.gamelisto.catalogo.domain.GamePublisherRepositorio;
import com.gamelisto.catalogo.domain.events.GameCreado;
import com.gamelisto.catalogo.infrastructure.exceptions.InfrastructureException;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GamesPublisher implements GamePublisherRepositorio {

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void publicarGameCreado(GameCreado evento) {
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
    } catch (Exception e) {
      throw new InfrastructureException("Error al publicar evento en RabbitMQ", e);
    }
  }
}
