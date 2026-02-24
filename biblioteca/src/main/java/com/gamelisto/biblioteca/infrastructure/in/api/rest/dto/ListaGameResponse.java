package com.gamelisto.biblioteca.infrastructure.in.api.rest.dto;

import com.gamelisto.biblioteca.application.usecase.crearlistagame.CrearListaGameResult;

public record ListaGameResponse(String id, String usuarioRefId, String nombre, String tipo) {
  public static ListaGameResponse from(CrearListaGameResult r) {
    return new ListaGameResponse(r.id(), r.usuarioRefId(), r.nombre(), r.tipo());
  }
}
