package com.gamelisto.biblioteca.infrastructure.messaging;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.biblioteca.application.usecase.EntradaEventosHandle;
import com.gamelisto.biblioteca.infrastructure.in.messaging.BibliotecaListener;
import com.gamelisto.biblioteca.infrastructure.in.messaging.GameCreadoEventDto;
import com.gamelisto.biblioteca.infrastructure.in.messaging.UsuarioCreadoEventDto;
import com.gamelisto.biblioteca.infrastructure.in.messaging.UsuarioEliminadoEventDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;

@ExtendWith(MockitoExtension.class)
@DisplayName("BibliotecaListener - Tests unitarios")
class BibliotecaListenerTest {

  @Mock private EntradaEventosHandle entradaEventos;

  private BibliotecaListener listener;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    listener = new BibliotecaListener(entradaEventos, objectMapper);
  }

  @Test
  @DisplayName("Debe procesar evento UsuarioCreado y guardar UsuarioRef")
  void debeProcesarEventoUsuarioCreado() throws Exception {
    // Arrange
    UsuarioCreadoEventDto dto =
        new UsuarioCreadoEventDto(
            "550e8400-e29b-41d4-a716-446655440000",
            "jugador1",
            "https://avatar.url/img.png",
            "USER");

    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "UsuarioCreado");
    Message message =
        MessageBuilder.withBody(objectMapper.writeValueAsBytes(dto)).andProperties(props).build();

    // Act
    listener.handleEvent(message);

    // Assert
    verify(entradaEventos)
        .procesarUsuarioCreado(
            "550e8400-e29b-41d4-a716-446655440000",
            "jugador1",
            "USER",
            "https://avatar.url/img.png");
  }

  @Test
  @DisplayName("Debe procesar evento UsuarioEliminado y eliminar UsuarioRef")
  void debeProcesarEventoUsuarioEliminado() throws Exception {
    // Arrange
    String usuarioId = "660e8400-e29b-41d4-a716-446655440001";
    UsuarioEliminadoEventDto dto = new UsuarioEliminadoEventDto(usuarioId);

    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "UsuarioEliminado");
    Message message =
        MessageBuilder.withBody(objectMapper.writeValueAsBytes(dto)).andProperties(props).build();

    // Act
    listener.handleEvent(message);

    // Assert
    verify(entradaEventos).procesarUsuarioEliminado(usuarioId);
  }

  @Test
  @DisplayName("Debe procesar evento GameCreado y guardar GameRef")
  void debeProcesarEventoGameCreado() throws Exception {
    // Arrange
    GameCreadoEventDto dto = new GameCreadoEventDto(42L, "Zelda", "The Legend of Zelda");

    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "GameCreado");
    Message message =
        MessageBuilder.withBody(objectMapper.writeValueAsBytes(dto)).andProperties(props).build();

    // Act
    listener.handleEvent(message);

    // Assert
    verify(entradaEventos).procesarGameCreado(42L, "Zelda", "The Legend of Zelda");
  }

  @Test
  @DisplayName("Debe ignorar eventos con eventType desconocido sin lanzar excepción")
  void debeIgnorarEventosDesconocidos() throws Exception {
    // Arrange
    String payload = "{\"data\":\"algo\"}";
    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "EventoDesconocido");
    Message message = MessageBuilder.withBody(payload.getBytes()).andProperties(props).build();

    // Act
    listener.handleEvent(message);

    // Assert - no se debe llamar a ningún método del handle
    verify(entradaEventos, never()).procesarUsuarioCreado(any(), any(), any(), any());
    verify(entradaEventos, never()).procesarGameCreado(any(), any(), any());
    verify(entradaEventos, never()).procesarUsuarioEliminado(any());
  }
}
