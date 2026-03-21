package com.gamelisto.usuarios.infrastructure.out.messaging;

import com.gamelisto.usuarios.domain.events.UsuarioActualizado;
import com.gamelisto.usuarios.domain.events.UsuarioCreado;
import com.gamelisto.usuarios.domain.events.UsuarioEliminado;
import com.gamelisto.usuarios.domain.repositories.IUsuarioPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Publisher no-op para tests (sin RabbitMQ real). */
public class NoOpUsuarioPublisher implements IUsuarioPublisher {

  private static final Logger logger = LoggerFactory.getLogger(NoOpUsuarioPublisher.class);

  @Override
  public void publicarUsuarioActualizado(UsuarioActualizado evento) {
    logger.debug("No-Op: UsuarioActualizado no publicado (test)");
  }

  @Override
  public void publicarUsuarioCreado(UsuarioCreado evento) {
    logger.debug("No-Op: UsuarioCreado no publicado (test)");
  }

  @Override
  public void publicarUsuarioEliminado(UsuarioEliminado evento) {
    logger.debug("No-Op: UsuarioEliminado no publicado (test)");
  }
}
