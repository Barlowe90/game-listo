package com.gamelisto.busquedas.infrastructure.in.messaging.listeners;

import com.gamelisto.publicaciones.application.usecases.EntradaEventosHandle;
import com.gamelisto.publicaciones.infrastructure.exceptions.InfrastructureException;
import com.gamelisto.publicaciones.infrastructure.in.messaging.config.RabbitMQConfig;
import com.gamelisto.publicaciones.infrastructure.in.messaging.dto.GameCreadoEventDto;
import com.gamelisto.publicaciones.infrastructure.in.messaging.dto.UsuarioCreadoEventDto;
import com.gamelisto.publicaciones.infrastructure.in.messaging.dto.UsuarioEliminadoEventDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SmartMessageConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublicacionesListener {

  private static final Logger logger = LoggerFactory.getLogger(PublicacionesListener.class);

  private final EntradaEventosHandle entradaEventos;
  private final MessageConverter messageConverter;

  @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
  public void handleEvent(Message message) {
    String eventType = (String) message.getMessageProperties().getHeaders().get("eventType");

    if (eventType == null) {
      logger.warn("Evento recibido sin header 'eventType', ignorando mensaje");
      return;
    }

    try {
      switch (eventType) {
        case "UsuarioCreado" -> {
          UsuarioCreadoEventDto dto = convertPayload(message, UsuarioCreadoEventDto.class);
          logger.info("Procesando UsuarioCreado: usuarioId={}", dto.usuarioId());
          entradaEventos.procesarUsuarioCreado(dto.usuarioId(), dto.username(), dto.avatar());
        }
        case "UsuarioEliminado" -> {
          UsuarioEliminadoEventDto dto = convertPayload(message, UsuarioEliminadoEventDto.class);
          logger.info("Procesando UsuarioEliminado: usuarioId={}", dto.usuarioId());
          entradaEventos.procesarUsuarioEliminado(dto.usuarioId());
        }
        case "GameCreado" -> {
          GameCreadoEventDto dto = convertPayload(message, GameCreadoEventDto.class);
          logger.info("Procesando GameCreado: gameId={}, nombre={}", dto.id(), dto.name());
          entradaEventos.procesarGameCreado(dto.id(), dto.name(), dto.plataforma());
        }
        default ->
            logger.debug("Evento '{}' no gestionado por publicaciones, ignorando", eventType);
      }
    } catch (Exception e) {
      throw new InfrastructureException(
          "Error al procesar evento '" + eventType + "' en publicaciones", e);
    }
  }

  @SuppressWarnings("unchecked")
  private <T> T convertPayload(Message message, Class<T> targetType) {
    if (messageConverter instanceof SmartMessageConverter smartConverter) {
      return (T)
          smartConverter.fromMessage(message, ParameterizedTypeReference.forType(targetType));
    }
    return (T) messageConverter.fromMessage(message);
  }
}
