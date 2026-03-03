package com.gamelisto.publicaciones.application.usecases;

import java.util.UUID;

public record EditarPublicacionCommand(
    UUID publicacionId,
    UUID autorId,
    String titulo,
    String idioma,
    String experiencia,
    String estiloJuego,
    int jugadoresMaximos) {}
