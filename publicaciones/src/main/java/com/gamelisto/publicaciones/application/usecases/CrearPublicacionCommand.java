package com.gamelisto.publicaciones.application.usecases;

import java.util.UUID;

public record CrearPublicacionCommand(
    UUID autorId,
    Long gameId,
    String titulo,
    String idioma,
    String experiencia,
    String estiloJuego,
    int jugadoresMaximos) {}
