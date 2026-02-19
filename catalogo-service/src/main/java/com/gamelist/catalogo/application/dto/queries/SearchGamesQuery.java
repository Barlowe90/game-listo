package com.gamelist.catalogo.application.dto.queries;

/** Query para buscar juegos por nombre */
public record SearchGamesQuery(String name, int page, int size) {}
