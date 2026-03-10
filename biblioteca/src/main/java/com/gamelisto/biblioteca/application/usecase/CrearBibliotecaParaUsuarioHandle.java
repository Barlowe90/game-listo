package com.gamelisto.biblioteca.application.usecase;

import java.util.UUID;

public interface CrearBibliotecaParaUsuarioHandle {
  void execute(UUID userId);
}
