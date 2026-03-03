package com.gamelisto.publicaciones.application.usecases;

public interface CrearPublicacionHandler {
  PublicacionResult execute(CrearPublicacionCommand command);
}
