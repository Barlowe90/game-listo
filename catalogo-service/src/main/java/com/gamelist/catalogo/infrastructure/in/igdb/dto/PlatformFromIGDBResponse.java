package com.gamelist.catalogo.infrastructure.in.igdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlatformFromIGDBResponse(
    Long id,
    String name,
    String abbreviation,
    @JsonProperty("alternative_name") String alternativeName,
    @JsonProperty("platform_logo") IgdbCoverRequest platformLogo,
    @JsonProperty("platform_type") IgdbNameRequest platformType) {}
