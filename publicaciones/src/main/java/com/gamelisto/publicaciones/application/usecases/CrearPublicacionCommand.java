package com.gamelisto.publicaciones.application.usecases;

import java.util.UUID;

public record CrearPublicacionCommand(
    UUID autorId,
    UUID gameId,
    String titulo,
    String idioma,
    String experiencia,
    String estiloJuego,
    int jugadoresMaximos) {}
