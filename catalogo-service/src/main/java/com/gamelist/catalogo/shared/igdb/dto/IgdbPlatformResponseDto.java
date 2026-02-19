package com.gamelist.catalogo.infrastructure.igdb.dto;

public record IgdbPlatformResponseDto(
    Long id, String name, String abbreviation, String platform_logo, Integer category) {}
