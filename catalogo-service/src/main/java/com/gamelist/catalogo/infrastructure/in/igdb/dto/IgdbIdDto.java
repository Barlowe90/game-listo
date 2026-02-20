package com.gamelist.catalogo.infrastructure.in.igdb.dto;

/** DTO para objetos IGDB que solo tienen id (e.g. dlcs.id, expansions.id, similar_games.id) */
public record IgdbIdDto(Long id) {}
