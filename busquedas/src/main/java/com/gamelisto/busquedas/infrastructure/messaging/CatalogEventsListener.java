package com.gamelisto.busquedas.infrastructure.messaging;

import com.gamelisto.busquedas.application.usecases.IndexarJuegosDesdeEventoHandle;
import com.gamelisto.busquedas.infrastructure.messaging.dto.VideojuegoCreadoEventDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/** Listener que consume eventos del catálogo desde RabbitMQ e indexa videojuegos en OpenSearch. */
@Component
@RequiredArgsConstructor
public class CatalogEventsListener {

  private static final Logger logger = LoggerFactory.getLogger(CatalogEventsListener.class);

  private final IndexarJuegosDesdeEventoHandle indexarJuegosDesdeEvento;

  @RabbitListener(queues = "${busquedas.rabbit.queue}")
  public void onVideojuegoCreado(VideojuegoCreadoEventDto event) {
    logger.info(
        "Evento recibido de catálogo: eventId={}, gameId={}, title={}",
        event.eventId(),
        event.gameId(),
        event.title());
    indexarJuegosDesdeEvento.execute(event);
  }
}
