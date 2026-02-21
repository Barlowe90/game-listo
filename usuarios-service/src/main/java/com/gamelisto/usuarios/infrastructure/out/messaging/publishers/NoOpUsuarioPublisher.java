package com.gamelisto.usuarios.infrastructure.out.messaging.publishers;

import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/** Implementación No-Op del publisher de eventos para tests. */
@Component
@Primary
@ConditionalOnMissingBean(value = UsuariosPublisher.class)
public class NoOpUsuarioPublisher implements IUsuarioPublisher {

  private static final Logger logger = LoggerFactory.getLogger(NoOpUsuarioPublisher.class);

  @Override
  public void publish(String routingKeySuffix, Object event) {
    logger.debug(
        "No-Op Publisher: evento '{}' no publicado (RabbitMQ no disponible)", routingKeySuffix);
  }
}
