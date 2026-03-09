package com.gamelisto.social.infrastructure.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.social.application.usecases.EntradaEventosHandle;
import com.gamelisto.social.infrastructure.out.messaging.SocialListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SocialListener - Mensajeria RabbitMQ")
class SocialListenerTest {

  @Mock private EntradaEventosHandle entradaEventos;
  private SocialListener listener;

  @BeforeEach
  void setUp() {
    listener = new SocialListener(entradaEventos, new ObjectMapper());
  }

  @Test
  @DisplayName("debe procesar UsuarioCreado")
  void debeProcesarUsuarioCreado() throws Exception {
    String body = "{\"usuarioId\":\"u1\",\"username\":\"alice\",\"avatar\":\"img.png\"}";
    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "UsuarioCreado");
    listener.handleEvent(new Message(body.getBytes(), props));
    verify(entradaEventos).procesarUsuarioCreado("u1", "alice", "img.png");
  }

  @Test
  @DisplayName("debe procesar UsuarioEliminado")
  void debeProcesarUsuarioEliminado() throws Exception {
    String body = "{\"usuarioId\":\"u1\"}";
    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "UsuarioEliminado");
    listener.handleEvent(new Message(body.getBytes(), props));
    verify(entradaEventos).procesarUsuarioEliminado("u1");
  }

  @Test
  @DisplayName("debe ignorar sin header eventType")
  void debeIgnorarSinHeader() {
    listener.handleEvent(new Message("{}".getBytes(), new MessageProperties()));
    verifyNoInteractions(entradaEventos);
  }
}
