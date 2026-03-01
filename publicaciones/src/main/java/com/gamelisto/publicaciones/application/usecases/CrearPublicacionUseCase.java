package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CrearPublicacionUseCase implements CrearPublicacionHandler {

  private final PublicacionRepositorio publicacionRepositorio;

  @Override
  public PublicacionResult execute(CrearPublicacionCommand command) {
    UUID autorUuid = UUID.fromString(command.autorId());
    Long gameId = Long.parseLong(command.gameId());
    String titulo = command.titulo();
    Idioma idioma = Idioma.valueOf(command.idioma());
    Experiencia experiencia = Experiencia.valueOf(command.experiencia());
    EstiloJuego estiloJuego = EstiloJuego.valueOf(command.estiloJuego());
    int jugadoresMaximos = command.jugadoresMaximos();

    Publicacion publicacion =
        Publicacion.create(
            autorUuid, gameId, titulo, idioma, experiencia, estiloJuego, jugadoresMaximos);

    Publicacion publicacionGuardada = publicacionRepositorio.save(publicacion);

    if (publicacionGuardada == null) {
      throw new ApplicationException("No se pudo crear la publicacion");
    }

    return PublicacionResult.from(publicacionGuardada);
  }
}
