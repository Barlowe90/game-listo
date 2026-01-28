package com.gamelisto.usuarios_service.application.ports;

public interface IUsuarioPublisher {

  void publish(String routingKeySuffix, Object event);
}
