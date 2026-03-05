package com.gamelisto.busquedas.infrastructure.in.messaging;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamelisto.busquedas.application.usecases.EntradaEventosHandle;

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
@DisplayName("BusquedasListener - Tests unitarios")
class BusquedasListenerTest {

  @Mock private EntradaEventosHandle entradaEventos;

  private BusquedasListener listener;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    listener = new BusquedasListener(entradaEventos, objectMapper);
  }

  @Test
  @DisplayName("Debe procesar evento GameCreado y llamar a procesarGameCreado")
  void debeProcesarEventoGameCreado() throws Exception {
    // Arrange
    GameCreadoEventDto dto = new GameCreadoEventDto(42L, "Zelda", List.of("The Legend of Zelda"));

    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "GameCreado");
    Message message =
        MessageBuilder.withBody(objectMapper.writeValueAsBytes(dto)).andProperties(props).build();

    // Act
    listener.handleEvent(message);

    // Assert
    verify(entradaEventos).procesarGameCreado(42L, "Zelda", List.of("The Legend of Zelda"));
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
    verify(entradaEventos, never()).procesarGameCreado(any(), any(), any());
  }
}
