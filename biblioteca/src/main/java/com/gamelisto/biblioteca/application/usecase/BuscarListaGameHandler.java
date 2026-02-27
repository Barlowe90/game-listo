package com.gamelisto.biblioteca.application.usecase;

public interface BuscarListaGameHandler {
  ListaGameResult execute(String userId, String listaId);
}
