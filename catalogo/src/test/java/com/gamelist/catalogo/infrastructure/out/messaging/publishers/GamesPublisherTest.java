package com.gamelist.catalogo.infrastructure.out.messaging.publishers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.gamelist.catalogo.domain.events.GameCreado;
import com.gamelist.catalogo.infrastructure.out.messaging.config.RabbitMQConfig;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

@ExtendWith(MockitoExtension.class)
@DisplayName("GamesPublisherRepositorio - Tests")
class GamesPublisherRepositorioTest {

  @Mock private RabbitTemplate rabbitTemplate;

  @InjectMocks private GamesPublisherRepositorio publisher;

  @Captor private ArgumentCaptor<String> exchangeCaptor;
  @Captor private ArgumentCaptor<String> routingKeyCaptor;
  @Captor private ArgumentCaptor<Object> eventCaptor;

  private GameCreado sampleEvent;

  @BeforeEach
  void setUp() {
    // Construimos un evento compacto usando el factory 'of' con valores mínimos
    sampleEvent =
        GameCreado.of(
            "game-123",
            "Sample Game",
            "Sample summary",
            "cover.png",
            List.of("PC"), // platforms
            "FULL", // gameType
            "RELEASED", // gameStatus
            List.of(), // alternativeNames
            List.of(), // dlcs
            List.of(), // expandedGames
            List.of(), // expansionIds
            List.of(), // externalGames
            List.of(), // franchises
            List.of(), // gameModes
            List.of(), // genres
            List.of(), // involvedCompanies
            List.of(), // keywords
            List.of(), // multiplayerModeIds
            null, // parentGameId
            List.of(), // playerPerspectives
            List.of(), // remakeIds
            List.of(), // remasterIds
            List.of(), // similarGames
            List.of() // themes
            );
  }

  @Test
  @DisplayName("Debe publicar GameCreado con exchange y routing key correctos")
  void debePublicarGameCreadoConExchangeYRk() {
    // When
    publisher.publicarGameCreado(sampleEvent);

    // Then
    verify(rabbitTemplate)
        .convertAndSend(
            exchangeCaptor.capture(), routingKeyCaptor.capture(), eventCaptor.capture());

    assertThat(exchangeCaptor.getValue()).isEqualTo(RabbitMQConfig.EXCHANGE);
    assertThat(routingKeyCaptor.getValue()).isEqualTo(RabbitMQConfig.RK_GAME_CREADO);
    assertThat(eventCaptor.getValue()).isEqualTo(sampleEvent);
  }

  @Test
  @DisplayName("Debe lanzar InfrastructureException cuando RabbitTemplate falla")
  void debeLanzarExcepcionCuandoRabbitTemplateFalla() {
    // Given
    doThrow(new RuntimeException("RabbitMQ connection error"))
        .when(rabbitTemplate)
        .convertAndSend(
            eq(RabbitMQConfig.EXCHANGE), eq(RabbitMQConfig.RK_GAME_CREADO), any(Object.class));

    // When & Then
    assertThatThrownBy(() -> publisher.publicarGameCreado(sampleEvent))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Error al publicar evento");
  }
}
