package com.gamelisto.biblioteca.domain.eventos;

public interface IBibliotecaPublisher {

  void publicarEstadoActualizado(EstadoActualizado evento);
}
