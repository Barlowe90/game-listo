package com.gamelisto.catalogo.infrastructure.in.api.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ResolverJuegosSteamRequest(@NotNull List<Long> steamAppIds) {}
