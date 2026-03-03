package com.gamelisto.busquedas.infrastructure.api.dto;

import java.util.List;

public record SuggestResponse(String query, List<SuggestItem> results) {}
