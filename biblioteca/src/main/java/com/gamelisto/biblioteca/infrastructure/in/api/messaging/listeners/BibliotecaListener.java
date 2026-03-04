package com.gamelisto.biblioteca.infrastructure.in.api.messaging.publishers;

import com.gamelisto.biblioteca.application.usecase.EntradaEventosHandle;
import com.gamelisto.biblioteca.infrastructure.exceptions.InfrastructureException;
import com.gamelisto.biblioteca.infrastructure.in.api.messaging.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BibliotecaListener {

  private static final Logger logger = LoggerFactory.getLogger(BibliotecaListener.class);

  private static final String IDENTIFICADOR_USUARIO_CREADO_EVENTO = "usuario-creado";
  private static final String IDENTIFICADOR_USUARIO_ELIMINADO_EVENTO = "usuario-eliminado";
  private static final String IDENTIFICADOR_GAME_CREADO_EVENTO = "juego-creado";

  private final EntradaEventosHandle entradaEventos;

  @Override
  public void publicarUsuarioCreado(UsuarioCreado evento) {
    publicar(RabbitMQConfig.RK_USUARIO_CREADO, evento);
  }

  @Override
  public void publicarUsuarioEliminado(UsuarioEliminado evento) {
    publicar(RabbitMQConfig.RK_USUARIO_ELIMINADO, evento);
  }

  private void publicar(String routingKey, Object evento) {
    try {
      rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, routingKey, evento);
      logger.info("Evento publicado: {} → {}", evento.getClass().getSimpleName(), routingKey);
    } catch (Exception e) {
      throw new InfrastructureException("Error al publicar evento en RabbitMQ", e);
    }
  }
}
