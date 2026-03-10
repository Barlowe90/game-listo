package com.gamelisto.biblioteca.infrastructure.out.messaging;

import com.gamelisto.biblioteca.domain.eventos.EstadoActualizado;
import com.gamelisto.biblioteca.domain.eventos.IBibliotecaPublisher;
import com.gamelisto.biblioteca.infrastructure.exceptions.InfrastructureException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BibliotecaPublisherImpl implements IBibliotecaPublisher {

  private static final Logger logger = LoggerFactory.getLogger(BibliotecaPublisherImpl.class);

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void publicarEstadoActualizado(EstadoActualizado evento) {
    publicar(RabbitMQConfig.RK_ESTADO_ACTUALIZADO, evento);
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
