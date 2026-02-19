package com.gamelist.catalogo.shared.igdb.dto;

public record IgdbGameResponseDto(
    Long id, String name, String summary, IgdbCoverDto cover, java.util.List<Long> platforms) {}
