package com.gamelist.catalogo_service.infrastructure.igdb.dto;

public record IgdbGameResponseDto(
    Long id, String name, String summary, IgdbCoverDto cover, java.util.List<Long> platforms) {}
