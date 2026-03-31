package com.gamelisto.publicaciones.infrastructure.in.messaging;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.publicaciones.application.usecases.EntradaEventosHandle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("PublicacionesListener - Tests unitarios")
class PublicacionesListenerIntegrationTest {

  @Mock private EntradaEventosHandle entradaEventos;

  private PublicacionesListener listener;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    listener = new PublicacionesListener(entradaEventos, objectMapper);
  }

  @Test
  @DisplayName("Debe procesar evento UsuarioCreado y guardar UsuarioRef")
  void debeProcesarUsuarioCreado() throws Exception {
    // Arrange
    UsuarioCreadoEventDto dto =
        new UsuarioCreadoEventDto("u-123", "ric", "/avatars/1.png", "123456789");

    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "UsuarioCreado");
    Message message =
        MessageBuilder.withBody(objectMapper.writeValueAsBytes(dto)).andProperties(props).build();

    // Act
    listener.handleEvent(message);

    // Assert
    verify(entradaEventos)
        .procesarUsuarioCreado("u-123", "ric", "/avatars/1.png", "123456789");
  }

  @Test
  @DisplayName("Debe procesar evento UsuarioActualizado y actualizar UsuarioRef")
  void debeProcesarUsuarioActualizado() throws Exception {
    // Arrange
    UsuarioCreadoEventDto dto =
        new UsuarioCreadoEventDto("u-123", "ric", "/avatars/2.png", "987654321");

    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "UsuarioActualizado");
    Message message =
        MessageBuilder.withBody(objectMapper.writeValueAsBytes(dto)).andProperties(props).build();

    // Act
    listener.handleEvent(message);

    // Assert
    verify(entradaEventos)
        .procesarUsuarioActualizado("u-123", "ric", "/avatars/2.png", "987654321");
  }

  @Test
  @DisplayName("Debe procesar evento UsuarioEliminado y eliminar UsuarioRef")
  void debeProcesarUsuarioEliminado() throws Exception {
    // Arrange
    UsuarioEliminadoEventDto dto = new UsuarioEliminadoEventDto("u-456");

    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "UsuarioEliminado");
    Message message =
        MessageBuilder.withBody(objectMapper.writeValueAsBytes(dto)).andProperties(props).build();

    // Act
    listener.handleEvent(message);

    // Assert
    verify(entradaEventos).procesarUsuarioEliminado("u-456");
  }

  @Test
  @DisplayName("Debe procesar evento GameCreado y guardar GameRef")
  void debeProcesarGameCreado() throws Exception {
    // Arrange
    GameCreadoEventDto dto = new GameCreadoEventDto(42L, "Zelda", List.of("Zelda BOTW"));

    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "GameCreado");
    Message message =
        MessageBuilder.withBody(objectMapper.writeValueAsBytes(dto)).andProperties(props).build();

    // Act
    listener.handleEvent(message);

    // Assert
    verify(entradaEventos).procesarGameCreado(42L, "Zelda", List.of("Zelda BOTW"));
  }

  @Test
  @DisplayName("Debe ignorar evento sin header eventType")
  void debeIgnorarEventoSinHeaderEventType() {
    // Arrange
    Message message =
        MessageBuilder.withBody("{\"data\":\"algo\"}".getBytes())
            .andProperties(new MessageProperties())
            .build();

    // Act
    listener.handleEvent(message);

    // Assert
    verify(entradaEventos, never()).procesarUsuarioCreado(any(), any(), any(), any());
    verify(entradaEventos, never()).procesarUsuarioActualizado(any(), any(), any(), any());
    verify(entradaEventos, never()).procesarUsuarioEliminado(any());
    verify(entradaEventos, never()).procesarGameCreado(any(), any(), any());
  }

  @Test
  @DisplayName("Debe ignorar eventos con eventType desconocido sin lanzar excepción")
  void debeIgnorarEventosDesconocidos() {
    // Arrange
    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "EventoDesconocido");
    Message message =
        MessageBuilder.withBody("{\"data\":\"algo\"}".getBytes()).andProperties(props).build();

    // Act
    listener.handleEvent(message);

    // Assert
    verify(entradaEventos, never()).procesarUsuarioCreado(any(), any(), any(), any());
    verify(entradaEventos, never()).procesarUsuarioActualizado(any(), any(), any(), any());
    verify(entradaEventos, never()).procesarGameCreado(any(), any(), any());
    verify(entradaEventos, never()).procesarUsuarioEliminado(any());
  }
}
