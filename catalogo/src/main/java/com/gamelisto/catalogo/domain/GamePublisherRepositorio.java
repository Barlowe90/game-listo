package com.gamelisto.catalogo.domain;

import com.gamelisto.catalogo.domain.events.GameCreado;

public interface GamePublisherRepositorio {

  void publicarGameCreado(GameCreado evento);
}
