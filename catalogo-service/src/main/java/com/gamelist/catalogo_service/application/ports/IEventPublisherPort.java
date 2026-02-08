package com.gamelist.catalogo_service.application.ports;

public interface IEventPublisherPort {

  void publish(Object event);
}
