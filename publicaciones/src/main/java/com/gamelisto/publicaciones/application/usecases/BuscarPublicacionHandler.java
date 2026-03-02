package com.gamelisto.publicaciones.application.usecases;

public interface BuscarPublicacionHandler {
  PublicacionDetalleResult execute(String publicacionId);
}
