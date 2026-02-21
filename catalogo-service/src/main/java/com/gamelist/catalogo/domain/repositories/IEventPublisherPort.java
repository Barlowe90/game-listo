package com.gamelist.catalogo.domain.repositories;

public interface IEventPublisherPort {

  void publish(String routingKeySuffix, Object event);
}
