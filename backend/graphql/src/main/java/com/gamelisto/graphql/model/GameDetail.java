package com.gamelisto.graphql.model;

import java.util.List;

public record GameDetail(
    Game game,
    GameMedia media,
    List<Game> relatedGames,
    List<Publicacion> publicaciones,
    List<GrupoJuego> grupos
) {}
