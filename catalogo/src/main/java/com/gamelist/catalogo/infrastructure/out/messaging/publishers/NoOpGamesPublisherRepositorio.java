package com.gamelist.catalogo.infrastructure.out.messaging.publishers;

import com.gamelist.catalogo.domain.GamePublisherRepositorio;
import com.gamelist.catalogo.domain.events.GameCreado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/** Publisher no-op para tests (sin RabbitMQ real). */
@Component
@Primary
@ConditionalOnMissingBean(value = GamesPublisherRepositorio.class)
public class NoOpGamesPublisherRepositorio implements GamePublisherRepositorio {

  private static final Logger logger = LoggerFactory.getLogger(NoOpGamesPublisherRepositorio.class);

  @Override
  public void publicarGameCreado(GameCreado evento) {
    logger.debug("No-Op: GameCreado no publicado (test)");
  }
}
