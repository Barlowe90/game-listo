package com.gamelist.catalogo.application.dto.results;

import java.util.List;

public record IgdbGameDTO(
    Long id, String name, String summary, String coverUrl, List<Long> platformIds) {}
