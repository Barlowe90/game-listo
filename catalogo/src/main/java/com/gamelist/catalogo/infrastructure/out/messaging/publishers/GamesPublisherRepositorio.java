package com.gamelist.catalogo.infrastructure.out.messaging.publishers;

import com.gamelist.catalogo.domain.GamePublisherRepositorio;
import com.gamelist.catalogo.domain.events.GameCreado;
import com.gamelist.catalogo.infrastructure.exceptions.InfrastructureException;
import com.gamelist.catalogo.infrastructure.out.messaging.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnBean(RabbitTemplate.class)
@RequiredArgsConstructor
public class GamesPublisherRepositorio implements GamePublisherRepositorio {

  private final RabbitTemplate rabbitTemplate;

  private static final Logger logger = LoggerFactory.getLogger(GamesPublisherRepositorio.class);

  @Override
  public void publicarGameCreado(GameCreado evento) {
    publicar(RabbitMQConfig.RK_GAME_CREADO, evento);
  }

  private void publicar(String routingKey, Object evento) {
    try {
      rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, routingKey, evento);
      logger.info("Evento publicado: {} → {}", evento.getClass().getSimpleName(), routingKey);
    } catch (Exception e) {
      throw new InfrastructureException("Error al publicar evento en RabbitMQ", e);
    }
  }
}
