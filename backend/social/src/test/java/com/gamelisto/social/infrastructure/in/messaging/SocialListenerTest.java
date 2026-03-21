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

import java.util.UUID;

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
  void debeProcesarUsuarioCreado() {
    String id = "00000000-0000-0000-0000-000000000001";
    String body =
        "{\"usuarioId\":\""
            + id
            + "\",\"username\":\"alice\",\"avatar\":\"img.png\",\"discordUserId\":\"123456789\",\"discordUsername\":\"alice.discord\"}";
    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "UsuarioCreado");
    listener.handleEvent(new Message(body.getBytes(), props));
    verify(entradaEventos)
        .procesarUsuarioCreado(
            UUID.fromString(id), "alice", "img.png", "123456789", "alice.discord");
  }

  @Test
  @DisplayName("debe procesar UsuarioActualizado")
  void debeProcesarUsuarioActualizado() {
    String id = "00000000-0000-0000-0000-000000000001";
    String body =
        "{\"usuarioId\":\""
            + id
            + "\",\"username\":\"alice\",\"avatar\":\"new.png\",\"discordUserId\":\"987654321\",\"discordUsername\":\"alice.updated\"}";
    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "UsuarioActualizado");
    listener.handleEvent(new Message(body.getBytes(), props));
    verify(entradaEventos)
        .procesarUsuarioActualizado(
            UUID.fromString(id), "alice", "new.png", "987654321", "alice.updated");
  }

  @Test
  @DisplayName("debe procesar UsuarioEliminado")
  void debeProcesarUsuarioEliminado() {
    String id = "00000000-0000-0000-0000-000000000001";
    String body = "{\"usuarioId\":\"" + id + "\"}";
    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "UsuarioEliminado");
    listener.handleEvent(new Message(body.getBytes(), props));
    verify(entradaEventos).procesarUsuarioEliminado(UUID.fromString(id));
  }

  @Test
  @DisplayName("debe procesar EstadoActualizado usando gameId del evento")
  void debeProcesarEstadoActualizadoConGameId() {
    String id = "00000000-0000-0000-0000-000000000001";
    String body = "{\"usuarioId\":\"" + id + "\",\"gameId\":451324,\"estado\":\"JUGANDO\"}";
    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "EstadoActualizado");
    listener.handleEvent(new Message(body.getBytes(), props));
    verify(entradaEventos).procesarEstadoActualizado(UUID.fromString(id), 451324L, "JUGANDO");
  }

  @Test
  @DisplayName("debe ignorar sin header eventType")
  void debeIgnorarSinHeader() {
    listener.handleEvent(new Message("{}".getBytes(), new MessageProperties()));
    verifyNoInteractions(entradaEventos);
  }
}
