package com.gamelisto.publicaciones.application.usecases;

public interface EditarPublicacionHandler {
  PublicacionResult execute(EditarPublicacionCommand command);
}
