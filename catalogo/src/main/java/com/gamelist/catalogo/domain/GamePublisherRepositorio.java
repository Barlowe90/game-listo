package com.gamelist.catalogo.domain;

import com.gamelist.catalogo.domain.events.GameCreado;

public interface GamePublisherRepositorio {

  void publicarGameCreado(GameCreado evento);
}
