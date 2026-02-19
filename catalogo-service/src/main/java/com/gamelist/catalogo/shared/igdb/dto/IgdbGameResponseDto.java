package com.gamelist.catalogo.infrastructure.igdb.dto;

public record IgdbGameResponseDto(
    Long id, String name, String summary, IgdbCoverDto cover, java.util.List<Long> platforms) {}
