package com.gamelisto.usuarios.domain.repositories;

public interface IUsuarioPublisher {

  void publish(String routingKeySuffix, Object event);
}
