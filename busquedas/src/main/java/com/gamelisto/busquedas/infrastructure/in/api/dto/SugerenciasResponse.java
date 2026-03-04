package com.gamelisto.busquedas.infrastructure.api.dto;

import java.util.List;

public record SugerenciasResponse(String query, List<SugerirItemResponse> results) {}
