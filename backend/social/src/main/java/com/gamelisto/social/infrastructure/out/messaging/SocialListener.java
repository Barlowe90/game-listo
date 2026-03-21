package com.gamelisto.social.infrastructure.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.social.application.usecases.EntradaEventosHandle;
import com.gamelisto.social.infrastructure.exceptions.InfrastructureException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "messaging.rabbitmq.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class SocialListener {
  private static final Logger log = LoggerFactory.getLogger(SocialListener.class);
  private final EntradaEventosHandle entradaEventos;
  private final ObjectMapper objectMapper;

  @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
  public void handleEvent(Message message) {
    String eventType = (String) message.getMessageProperties().getHeaders().get("eventType");
    if (eventType == null) {
      log.warn("Evento recibido sin header eventType, ignorando mensaje");
      return;
    }
    try {
      switch (eventType) {
        case "UsuarioCreado" -> {
          UsuarioCreadoEventDto dto =
              objectMapper.readValue(message.getBody(), UsuarioCreadoEventDto.class);
          log.info("Procesando UsuarioCreado: usuarioId={}", dto.usuarioId());
          entradaEventos.procesarUsuarioCreado(dto.usuarioId(), dto.username(), dto.avatar());
        }
        case "UsuarioActualizado" -> {
          UsuarioCreadoEventDto dto =
              objectMapper.readValue(message.getBody(), UsuarioCreadoEventDto.class);
          log.info("Procesando UsuarioActualizado: usuarioId={}", dto.usuarioId());
          entradaEventos.procesarUsuarioActualizado(dto.usuarioId(), dto.username(), dto.avatar());
        }
        case "UsuarioEliminado" -> {
          UsuarioEliminadoEventDto dto =
              objectMapper.readValue(message.getBody(), UsuarioEliminadoEventDto.class);
          log.info("Procesando UsuarioEliminado: usuarioId={}", dto.usuarioId());
          entradaEventos.procesarUsuarioEliminado(dto.usuarioId());
        }
        case "EstadoActualizado" -> {
          EstadoActualizadoEventDto dto =
              objectMapper.readValue(message.getBody(), EstadoActualizadoEventDto.class);
          log.info(
              "Procesando EstadoActualizado: usuarioId={}, gameRefId={}, estado={}",
              dto.usuarioId(),
              dto.gameRef(),
              dto.estado());
          entradaEventos.procesarEstadoActualizado(dto.usuarioId(), dto.gameRef(), dto.estado());
        }
        default -> log.debug("Evento '{}' no gestionado por social, ignorando", eventType);
      }
    } catch (Exception e) {
      throw new InfrastructureException(
          "Error al procesar evento '" + eventType + "' en social", e);
    }
  }
}
