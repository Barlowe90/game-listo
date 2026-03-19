package com.gamelisto.catalogo.domain;

import java.util.List;

public record GameCardSummary(
    Long id, String name, String coverUrl, List<String> platforms, List<String> gameModes) {}
