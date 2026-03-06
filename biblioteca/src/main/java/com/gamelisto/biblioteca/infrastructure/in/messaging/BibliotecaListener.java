package com.gamelisto.biblioteca.infrastructure.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.biblioteca.application.usecase.EntradaEventosHandle;
import com.gamelisto.biblioteca.infrastructure.exceptions.InfrastructureException;
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
public class BibliotecaListener {

  private static final Logger logger = LoggerFactory.getLogger(BibliotecaListener.class);

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
          UsuarioCreadoEventDto dto = read(message, UsuarioCreadoEventDto.class);
          logger.info("Procesando UsuarioCreado: usuarioId={}", dto.usuarioId());
          entradaEventos.procesarUsuarioCreado(dto.usuarioId(), dto.username(), dto.avatar());
        }
        case "UsuarioEliminado" -> {
          UsuarioEliminadoEventDto dto = read(message, UsuarioEliminadoEventDto.class);
          logger.info("Procesando UsuarioEliminado: usuarioId={}", dto.usuarioId());
          entradaEventos.procesarUsuarioEliminado(dto.usuarioId());
        }
        case "GameCreado" -> {
          GameCreadoEventDto dto = read(message, GameCreadoEventDto.class);
          logger.info("Procesando GameCreado: gameId={}, nombre={}", dto.id(), dto.name());
          entradaEventos.procesarGameCreado(dto.id(), dto.name(), dto.portada());
        }
        default -> logger.debug("Evento '{}' no gestionado por biblioteca, ignorando", eventType);
      }
    } catch (Exception e) {
      throw new InfrastructureException(
          "Error al procesar evento '" + eventType + "' en biblioteca", e);
    }
  }

  private <T> T read(Message message, Class<T> targetType) throws Exception {
    return objectMapper.readValue(message.getBody(), targetType);
  }
}
