package com.gamelisto.biblioteca.infrastructure.in.api.dto;

import com.gamelisto.biblioteca.application.usecase.ListaGameResult;

import java.util.List;

public record ListaGameResponse(
    String id,
    String usuarioRefId,
    String nombre,
    String tipo,
    List<ListaGameItemResponse> juegos) {

  public static ListaGameResponse from(ListaGameResult r) {
    List<ListaGameItemResponse> items =
        r.juegos() == null
            ? List.of()
            : r.juegos().stream()
                .map(i -> new ListaGameItemResponse(i.gameId(), i.nombre(), i.cover(), i.estado()))
                .toList();

    return new ListaGameResponse(r.id(), r.usuarioRefId(), r.nombre(), r.tipo(), items);
  }
}
