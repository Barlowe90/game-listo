package com.gamelisto.usuarios.infrastructure.out.messaging;

import com.gamelisto.usuarios.domain.events.UsuarioCreado;
import com.gamelisto.usuarios.domain.events.UsuarioEliminado;
import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import com.gamelisto.usuarios.infrastructure.exceptions.InfrastructureException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuariosPublisher implements IUsuarioPublisher {

  private static final Logger logger = LoggerFactory.getLogger(UsuariosPublisher.class);

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void publicarUsuarioCreado(UsuarioCreado evento) {
    publicar(RabbitMQConfig.RK_USUARIO_CREADO, evento);
  }

  @Override
  public void publicarUsuarioEliminado(UsuarioEliminado evento) {
    publicar(RabbitMQConfig.RK_USUARIO_ELIMINADO, evento);
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
