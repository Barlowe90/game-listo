package com.gamelist.catalogo_service.infrastructure.igdb.dto;

public record IgdbPlatformResponseDto(
    Long id, String name, String abbreviation, String platform_logo, Integer category) {}
