package com.gamelisto.publicaciones.application.usecases;

public interface AceptarORechazarPeticionHandle {
  PeticionUnionResult execute(PeticionUnionCommand command);
}
