package com.gamelisto.publicaciones.application.usecases;

import java.util.UUID;

public record PeticionUnionCommand(UUID peticionUnionId, UUID userId, String estadoSolicitud) {}
