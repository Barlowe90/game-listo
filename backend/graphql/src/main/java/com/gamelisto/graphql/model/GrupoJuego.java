package com.gamelisto.graphql.model;

import java.util.List;

public record GrupoJuego(
    String id,
    String publicacionId,
    String fechaCreacion,
    List<UsuarioRef> participantes
) {}
