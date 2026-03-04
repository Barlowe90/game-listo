package com.gamelisto.publicaciones.infrastructure.in.messaging;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import com.gamelisto.publicaciones.application.usecases.EntradaEventosHandle;
import com.gamelisto.publicaciones.infrastructure.in.messaging.config.RabbitMQConfig;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

@SpringBootTest
@ActiveProfiles("test")
@Import({TestRabbitConfig.class, TestMocksConfig.class})
class PublicacionesListenerIntegrationTest {

  @Autowired private RabbitTemplate rabbitTemplate;

  @Autowired private EntradaEventosHandle entradaEventos;

  @Autowired private TestRabbitConfig rabbitConfig;

  @Test
  void debeProcesarUsuarioCreado() {
    // Skip test when Docker is not available
    Assumptions.assumeTrue(
        org.testcontainers.DockerClientFactory.instance().isDockerAvailable(),
        "Docker no disponible, saltando test");

    // Construir payload acorde a UsuarioCreadoEventDto
    String payload =
        "{\"usuarioId\":\"u-123\",\"username\":\"ric\",\"email\":\"ric@example.com\",\"avatar\":\"/avatars/1.png\",\"role\":\"USER\"}";

    // Enviar al exchange con routing key que coincida con bindingUsuarios
    rabbitTemplate.convertAndSend(
        RabbitMQConfig.EXCHANGE,
        "usuarios.created",
        payload,
        m -> {
          m.getMessageProperties().setHeader("eventType", "UsuarioCreado");
          return m;
        });

    // Esperar hasta que el mock sea invocado
    Awaitility.await()
        .atMost(Duration.ofSeconds(5))
        .untilAsserted(
            () ->
                verify(entradaEventos, times(1))
                    .procesarUsuarioCreado("u-123", "ric", "/avatars/1.png"));
  }

  @Test
  void debeIgnorarEventoSinHeaderEventType() {
    Assumptions.assumeTrue(
        org.testcontainers.DockerClientFactory.instance().isDockerAvailable(),
        "Docker no disponible, saltando test");

    String payload = "{\"usuarioId\":\"u-999\",\"username\":\"noheader\"}";

    rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "usuarios.created", payload);

    // Esperar un poco y verificar que no haya interacción con el use case
    Awaitility.await()
        .atMost(Duration.ofSeconds(2))
        .untilAsserted(
            () ->
                verify(entradaEventos, times(0)).procesarUsuarioCreado("u-999", "noheader", null));
  }
}
