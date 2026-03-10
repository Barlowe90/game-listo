package com.gamelisto.catalogo.application.usecases;

public interface BuscarGameDetailPorIdHandle {
  GameDetailResult execute(BuscarGameDetailPorIdCommand command);
}
