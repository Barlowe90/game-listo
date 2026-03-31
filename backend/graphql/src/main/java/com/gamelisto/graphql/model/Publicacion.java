package com.gamelisto.graphql.model;

import java.util.List;
import java.util.Map;

public record Publicacion(
    String id,
    String autorId,
    String gameId,
    String titulo,
    String idioma,
    String experiencia,
    String estiloJuego,
    Integer jugadoresMaximos,
    String grupoId,
    // Using a simple Map<String, List<String>> or a nested record to reflect Disponibilidad
    Map<String, List<String>> disponibilidad
) {}
