package com.gamelisto.catalogo.application.usecases;

public interface BuscarGamePorIdHandle {
  GameResult execute(BuscarGamePorIdCommand command);
}
