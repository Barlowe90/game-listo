package com.gamelisto.biblioteca.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;
import org.springframework.boot.ApplicationRunner;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import com.gamelisto.biblioteca.infrastructure.in.messaging.RabbitMQConfig;

@TestConfiguration(proxyBeanMethods = false)
@EnableRabbit
public class TestContainersConfig {

  @SuppressWarnings("resource")
  @Bean
  @ServiceConnection
  PostgreSQLContainer<?> postgresContainer() {
    return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
        .withDatabaseName("biblioteca_test_db")
        .withUsername("test_user")
        .withPassword("test_password")
        .withReuse(true); // Reutiliza el contenedor entre ejecuciones para mayor velocidad
  }

  @SuppressWarnings("resource")
  @Bean
  @ServiceConnection
  RabbitMQContainer rabbitMQContainer() {
    return new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.13-management-alpine"))
        .withReuse(true);
  }

  // Bean de RabbitAdmin solo en contexto de tests: permitirá declarar exchanges/queues
  // automáticamente
  @Bean
  public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
    return new RabbitAdmin(connectionFactory);
  }

  // Inicializador que declara exchange/queue/bindings en el broker de Testcontainers al arrancar
  @Bean
  public ApplicationRunner rabbitInitializer(AmqpAdmin amqpAdmin) {
    return args -> {
      TopicExchange exchange = new TopicExchange(RabbitMQConfig.EXCHANGE, true, false);
      Queue queue = QueueBuilder.durable(RabbitMQConfig.QUEUE_NAME).build();

      amqpAdmin.declareExchange(exchange);
      amqpAdmin.declareQueue(queue);

      Binding bindingUsuarios =
          BindingBuilder.bind(queue).to(exchange).with(RabbitMQConfig.BINDING_USUARIOS_KEY);
      Binding bindingGames =
          BindingBuilder.bind(queue).to(exchange).with(RabbitMQConfig.BINDING_GAMES_KEY);

      amqpAdmin.declareBinding(bindingUsuarios);
      amqpAdmin.declareBinding(bindingGames);
    };
  }
}
