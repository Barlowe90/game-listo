package com.gamelisto.usuarios_service.infrastructure.messaging.publishers;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import com.gamelisto.usuarios_service.application.ports.IUsuarioPublisher;
import com.gamelisto.usuarios_service.infrastructure.messaging.config.RabbitMQConfig;

@Component
@ConditionalOnBean(RabbitTemplate.class)
public class UsuariosPublisher implements IUsuarioPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuariosPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public UsuariosPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(String routingKeySuffix, Object event) {
        try {
            String routingKey = RabbitMQConfig.ROUTING_KEY_PREFIX + "." + routingKeySuffix;
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                routingKey,
                event,
                message -> {
                    message.getMessageProperties().setHeader("eventType", event.getClass().getSimpleName());
                    message.getMessageProperties().setHeader("publishedAt", Instant.now().toString());
                    message.getMessageProperties().setHeader("service", "usuarios-service");
                    return message;
                }
            );
            
            logger.info("Evento publicado: {} a routing key: {}", 
                       event.getClass().getSimpleName(), routingKey);
            
        } catch (Exception e) {
            logger.error("Error al publicar evento: {}", event.getClass().getSimpleName(), e);
            throw new RuntimeException("Error al publicar evento", e);
        }
    }
}
