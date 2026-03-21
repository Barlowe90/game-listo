package com.gamelisto.publicaciones.infrastructure.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.publicaciones.application.usecases.EntradaEventosHandle;
import com.gamelisto.publicaciones.infrastructure.exceptions.InfrastructureException;
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
public class PublicacionesListener {

  private static final Logger logger = LoggerFactory.getLogger(PublicacionesListener.class);

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
      switch (eventType) {
        case "UsuarioCreado" -> {
          UsuarioCreadoEventDto dto =
              objectMapper.readValue(message.getBody(), UsuarioCreadoEventDto.class);
          logger.info("Procesando UsuarioCreado: usuarioId={}", dto.usuarioId());
          entradaEventos.procesarUsuarioCreado(
              dto.usuarioId(),
              dto.username(),
              dto.avatar(),
              dto.discordUserId(),
              dto.discordUsername());
        }
        case "UsuarioActualizado" -> {
          UsuarioCreadoEventDto dto =
              objectMapper.readValue(message.getBody(), UsuarioCreadoEventDto.class);
          logger.info("Procesando UsuarioActualizado: usuarioId={}", dto.usuarioId());
          entradaEventos.procesarUsuarioActualizado(
              dto.usuarioId(),
              dto.username(),
              dto.avatar(),
              dto.discordUserId(),
              dto.discordUsername());
        }
        case "UsuarioEliminado" -> {
          UsuarioEliminadoEventDto dto =
              objectMapper.readValue(message.getBody(), UsuarioEliminadoEventDto.class);
          logger.info("Procesando UsuarioEliminado: usuarioId={}", dto.usuarioId());
          entradaEventos.procesarUsuarioEliminado(dto.usuarioId());
        }
        case "GameCreado" -> {
          GameCreadoEventDto dto =
              objectMapper.readValue(message.getBody(), GameCreadoEventDto.class);
          logger.info("Procesando GameCreado: gameId={}, nombre={}", dto.id(), dto.name());
          entradaEventos.procesarGameCreado(dto.id(), dto.name(), dto.platforms());
        }
        default ->
            logger.debug("Evento '{}' no gestionado por publicaciones, ignorando", eventType);
      }
    } catch (Exception e) {
      throw new InfrastructureException(
          "Error al procesar evento '" + eventType + "' en publicaciones", e);
    }
  }
}
