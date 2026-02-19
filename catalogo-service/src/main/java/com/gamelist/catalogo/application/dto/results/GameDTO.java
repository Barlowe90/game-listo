package com.gamelist.catalogo_service.application.dto.results;

import java.util.Set;

public record GameDTO(
    Long id, String name, String summary, String coverUrl, Set<PlatformDTO> platforms) {}
