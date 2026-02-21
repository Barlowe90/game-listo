package com.gamelist.catalogo.domain.repositories;

public interface IGamePublisher {

  void publish(String routingKeySuffix, Object event);
}
