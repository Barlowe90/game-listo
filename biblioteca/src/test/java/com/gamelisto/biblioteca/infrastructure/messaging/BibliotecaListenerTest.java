package com.gamelisto.biblioteca.infrastructure.messaging;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

import com.gamelisto.biblioteca.application.usecase.EntradaEventosHandle;
import com.gamelisto.biblioteca.config.TestContainersConfig;
import com.gamelisto.biblioteca.infrastructure.in.api.messaging.config.RabbitMQConfig;
import com.gamelisto.biblioteca.infrastructure.in.api.messaging.dto.GameCreadoEventDto;
import com.gamelisto.biblioteca.infrastructure.in.api.messaging.dto.UsuarioCreadoEventDto;
import com.gamelisto.biblioteca.infrastructure.in.api.messaging.dto.UsuarioEliminadoEventDto;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestContainersConfig.class)
@DisplayName("BibliotecaListener - Tests de integración")
class BibliotecaListenerTest {

  @Autowired private RabbitTemplate rabbitTemplate;

  // Spy para verificar que los métodos se llaman sin reemplazar el bean real
  @MockitoSpyBean private EntradaEventosHandle entradaEventos;

  @Test
  @DisplayName("Debe procesar evento UsuarioCreado y guardar UsuarioRef")
  void debeProcesarEventoUsuarioCreado() {
    // Arrange - usar DTO para que el MessageConverter preserve el tipo
    UsuarioCreadoEventDto dto =
        new UsuarioCreadoEventDto(
            "550e8400-e29b-41d4-a716-446655440000",
            "jugador1",
            "jugador1@test.com",
            "https://avatar.url/img.png",
            "USER");

    // Act
    rabbitTemplate.convertAndSend(
        RabbitMQConfig.EXCHANGE,
        "usuarios.creado",
        dto,
        message -> {
          message.getMessageProperties().setHeader("eventType", "UsuarioCreado");
          message.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
          return message;
        });

    // Assert
    await()
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                verify(entradaEventos, atLeastOnce())
                    .procesarUsuarioCreado(
                        eq("550e8400-e29b-41d4-a716-446655440000"), eq("jugador1"), any(), any()));
  }

  @Test
  @DisplayName("Debe procesar evento UsuarioEliminado y eliminar UsuarioRef")
  void debeProcesarEventoUsuarioEliminado() {
    // Arrange - DTO
    String usuarioId = "660e8400-e29b-41d4-a716-446655440001";
    UsuarioEliminadoEventDto dto = new UsuarioEliminadoEventDto(usuarioId);

    // Act
    rabbitTemplate.convertAndSend(
        RabbitMQConfig.EXCHANGE,
        "usuarios.eliminado",
        dto,
        message -> {
          message.getMessageProperties().setHeader("eventType", "UsuarioEliminado");
          message.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
          return message;
        });

    // Assert
    await()
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(
            () -> verify(entradaEventos, atLeastOnce()).procesarUsuarioEliminado(usuarioId));
  }

  @Test
  @DisplayName("Debe procesar evento GameCreado y guardar GameRef")
  void debeProcesarEventoGameCreado() {
    // Arrange - DTO
    GameCreadoEventDto dto =
        new GameCreadoEventDto("42", "The Legend of Zelda", "https://img.igdb.com/cover.jpg");

    // Act
    rabbitTemplate.convertAndSend(
        RabbitMQConfig.EXCHANGE,
        "games.creado",
        dto,
        message -> {
          message.getMessageProperties().setHeader("eventType", "GameCreado");
          message.getMessageProperties().setContentType(MessageProperties.CONTENT_TYPE_JSON);
          return message;
        });

    // Assert
    await()
        .atMost(5, TimeUnit.SECONDS)
        .untilAsserted(
            () ->
                verify(entradaEventos, atLeastOnce())
                    .procesarGameCreado(eq("42"), eq("The Legend of Zelda"), any()));
  }

  @Test
  @DisplayName("Debe ignorar eventos con eventType desconocido sin lanzar excepción")
  void debeIgnorarEventosDesconocidos() {
    // Arrange
    String payload = "{\"data\":\"algo\"}";

    MessageProperties props = new MessageProperties();
    props.setHeader("eventType", "EventoDesconocido");
    props.setContentType(MessageProperties.CONTENT_TYPE_JSON);
    org.springframework.amqp.core.Message message =
        org.springframework.amqp.core.MessageBuilder.withBody(payload.getBytes())
            .andProperties(props)
            .build();

    // Act
    rabbitTemplate.send(RabbitMQConfig.EXCHANGE, "usuarios.otro", message);

    // Assert - no se debe llamar a ningún método del handle
    await()
        .atMost(3, TimeUnit.SECONDS)
        .untilAsserted(
            () -> {
              verify(entradaEventos, never()).procesarUsuarioCreado(any(), any(), any(), any());
              verify(entradaEventos, never()).procesarGameCreado(any(), any(), any());
            });
  }
}
