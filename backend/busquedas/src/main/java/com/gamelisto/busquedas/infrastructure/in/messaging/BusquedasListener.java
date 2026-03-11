package com.gamelisto.busquedas.infrastructure.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.busquedas.application.usecases.EntradaEventosHandle;
import com.gamelisto.busquedas.infrastructure.exceptions.InfrastructureException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "messaging.rabbitmq.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class BusquedasListener {

  private static final Logger logger = LoggerFactory.getLogger(BusquedasListener.class);

  private final EntradaEventosHandle entradaEventos;
  private final ObjectMapper objectMapper;

  @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
  public void handleEvent(Message message) {
    String eventType = (String) message.getMessageProperties().getHeaders().get("eventType");

    if (eventType == null) {
      logger.warn("Evento recibido sin header 'eventType', ignorando mensaje");
      return;
    }

    try {
      if ("GameCreado".equals(eventType)) {
        GameCreadoEventDto dto =
            objectMapper.readValue(message.getBody(), GameCreadoEventDto.class);
        List<String> alt = dto.alternativeNames() != null ? dto.alternativeNames() : List.of();
        logger.info(
            "Procesando GameCreado: gameId={}, nombre={}, nombres alternativos={}",
            dto.id(),
            dto.name(),
            alt);
        entradaEventos.procesarGameCreado(dto.id(), dto.name(), alt);
      } else {
        logger.debug("Evento '{}' no gestionado por busquedas, ignorando", eventType);
      }
    } catch (Exception e) {
      throw new InfrastructureException(
          "Error al procesar evento '" + eventType + "' en busquedas", e);
    }
  }
}
