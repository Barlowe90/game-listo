package com.gamelisto.usuarios.infrastructure.out.messaging.listeners;

import com.gamelisto.usuarios.infrastructure.exceptions.InfrastructureException;
import com.gamelisto.usuarios.infrastructure.out.messaging.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UsuariosListener {

  private static final Logger logger = LoggerFactory.getLogger(UsuariosListener.class);

  @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
  public void handleEvent(Message message, Object payload) {
    try {
      String eventType = (String) message.getMessageProperties().getHeaders().get("eventType");
      String service = (String) message.getMessageProperties().getHeaders().get("service");

      logger.info("Evento recibido - Tipo: {}, Servicio origen: {}", eventType, service);
      logger.debug("Payload: {}", payload);

      // TODO: implementar la diferenciación de eventos escuchados y funciones

      logger.info("Evento procesado exitosamente: {}", eventType);

    } catch (Exception e) {
      throw new InfrastructureException("Error al procesar evento", e);
    }
  }
}
