package com.gamelist.catalogo.infrastructure.out.messaging.publishers;

import com.gamelist.catalogo.domain.GamePublisherRepositorio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/** para test. */
@Component
@Primary
@ConditionalOnMissingBean(value = GamesPublisherRepositorio.class)
public class NoOpGamesPublisherRepositorio implements GamePublisherRepositorio {

  private static final Logger logger = LoggerFactory.getLogger(NoOpGamesPublisherRepositorio.class);

  @Override
  public void publish(String routingKeySuffix, Object event) {
    logger.info(
        "No-Op Publisher: evento '{}' no publicado (RabbitMQ no disponible)", routingKeySuffix);
  }
}
