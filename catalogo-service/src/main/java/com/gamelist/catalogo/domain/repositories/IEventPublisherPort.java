package com.gamelist.catalogo.domain.repositories;

public interface IEventPublisherPort {

  void publish(Object event);
}
