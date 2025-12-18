package com.gamelisto.usuarios_service.infrastructure.messaging.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.gamelisto.usuarios_service.infrastructure.messaging.config.RabbitMQConfig;

@Component
public class UsuariosListener {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuariosListener.class);

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handleEvent(Message message, Object payload) {
        try {
            String eventType = (String) message.getMessageProperties()
                    .getHeaders().get("eventType");
            String service = (String) message.getMessageProperties()
                    .getHeaders().get("service");
            
            logger.info("Evento recibido - Tipo: {}, Servicio origen: {}", eventType, service);
            logger.debug("Payload: {}", payload);
            
            // TODO: implementar la diferenciación de eventos escuchados
            // if ("EventoCreado".equals(eventType)) {
            //     procesarEventoCreado(payload);
            // } else if ("EventoCreado2".equals(eventType)) {
            //     procesarEventoCreado2(payload);
            // }
            
            logger.info("Evento procesado exitosamente: {}", eventType);
            
        } catch (Exception e) {
            logger.error("Error al procesar evento", e);
            throw new RuntimeException("Error al procesar evento", e);
        }
    }
    
    // TODO: implementar funciones
    // private void procesarEventoCreado(Object payload) { ... }
}
