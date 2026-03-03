package com.gamelist.catalogo.domain;

public interface GamePublisherRepositorio {

  void publish(String routingKeySuffix, Object event);
}
