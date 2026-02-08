package com.gamelist.catalogo_service.infrastructure.messaging.publishers;

import com.gamelist.catalogo_service.application.ports.IEventPublisherPort;
import com.gamelist.catalogo_service.domain.events.CatalogGameUpserted;
import com.gamelist.catalogo_service.domain.events.CatalogSyncBatchCompleted;
import com.gamelist.catalogo_service.domain.events.CatalogSyncCompleted;
import com.gamelist.catalogo_service.domain.events.PlatformsSyncCompleted;
import com.gamelist.catalogo_service.infrastructure.messaging.config.MessagingConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventPublisherRabbitMQ implements IEventPublisherPort {

  private final RabbitTemplate rabbitTemplate;

  @Override
  public void publish(Object event) {
    String routingKey = determineRoutingKey(event);

    log.info(
        "Publicando evento: {} con routing key: {}", event.getClass().getSimpleName(), routingKey);

    try {
      rabbitTemplate.convertAndSend(MessagingConfig.CATALOG_EXCHANGE, routingKey, event);

      log.debug("Evento publicado exitosamente: {}", event);

    } catch (Exception e) {
      log.error("Error al publicar evento: {}", event.getClass().getSimpleName(), e);
      // No lanzamos excepción para no interrumpir el flujo principal
      // Los eventos son importantes pero no críticos
    }
  }

  private String determineRoutingKey(Object event) {
    return switch (event) {
      case CatalogGameUpserted e -> MessagingConfig.GAME_UPSERTED_ROUTING_KEY;
      case CatalogSyncBatchCompleted e -> MessagingConfig.SYNC_BATCH_COMPLETED_ROUTING_KEY;
      case CatalogSyncCompleted e -> MessagingConfig.SYNC_COMPLETED_ROUTING_KEY;
      case PlatformsSyncCompleted e -> MessagingConfig.PLATFORMS_SYNC_COMPLETED_ROUTING_KEY;
      default -> {
        log.warn("Tipo de evento desconocido: {}", event.getClass().getName());
        yield "catalog.event.unknown";
      }
    };
  }
}
