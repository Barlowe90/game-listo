package com.gamelisto.publicaciones.application.usecases;

public interface AceptarORechazarPeticionHandle {
  SolicitudUnionResult execute(SolicitudUnionCommand command);
}
