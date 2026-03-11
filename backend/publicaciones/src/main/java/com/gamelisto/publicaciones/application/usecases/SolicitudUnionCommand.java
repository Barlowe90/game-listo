package com.gamelisto.publicaciones.application.usecases;

import java.util.UUID;

public record SolicitudUnionCommand(UUID peticionUnionId, UUID userId, String estadoSolicitud) {}
