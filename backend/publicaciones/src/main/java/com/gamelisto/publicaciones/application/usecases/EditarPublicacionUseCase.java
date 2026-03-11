package com.gamelisto.publicaciones.application.usecases;

import com.gamelisto.publicaciones.application.exceptions.ApplicationException;
import com.gamelisto.publicaciones.domain.EstiloJuego;
import com.gamelisto.publicaciones.domain.Publicacion;
import com.gamelisto.publicaciones.domain.PublicacionRepositorio;
import com.gamelisto.publicaciones.domain.Idioma;
import com.gamelisto.publicaciones.domain.Experiencia;
import com.gamelisto.publicaciones.domain.FranjaHoraria;
import com.gamelisto.publicaciones.domain.DiaSemana;
import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import com.gamelisto.publicaciones.domain.vo.DisponibilidadSemanal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EditarPublicacionUseCase implements EditarPublicacionHandler {

  private final PublicacionRepositorio publicacionRepositorio;

  @Override
  public PublicacionResult execute(EditarPublicacionCommand command) {
    UUID publicacionId = command.publicacionId();
    UUID autorId = command.autorId();
    String titulo = command.titulo();
    Idioma idioma = Idioma.valueOf(command.idioma());
    Experiencia experiencia = Experiencia.valueOf(command.experiencia());
    EstiloJuego estiloJuego = EstiloJuego.valueOf(command.estiloJuego());
    int jugadoresMaximos = command.jugadoresMaximos();

    Publicacion publicacion =
        publicacionRepositorio
            .findById(PublicacionId.of(publicacionId))
            .orElseThrow(() -> new ApplicationException("Publicacion no encontrada"));

    if (!publicacion.getAutorId().equals(autorId)) {
      throw new ApplicationException("Autor no propietario de la publicacion");
    }

    DisponibilidadSemanal disponibilidad = mapToDomainDisponibilidad(command.disponibilidad());

    Publicacion actualizado =
        Publicacion.reconstitute(
            publicacion.getId().value(),
            publicacion.getAutorId(),
            publicacion.getGameId(),
            titulo,
            idioma,
            experiencia,
            estiloJuego,
            jugadoresMaximos,
            disponibilidad);

    Publicacion guardado = publicacionRepositorio.save(actualizado);

    return PublicacionResult.from(guardado);
  }

  private DisponibilidadSemanal mapToDomainDisponibilidad(Map<String, Set<String>> in) {
    if (in == null) return DisponibilidadSemanal.empty();
    Map<DiaSemana, Set<FranjaHoraria>> map = new EnumMap<>(DiaSemana.class);
    in.forEach(
        (k, v) -> {
          try {
            DiaSemana dia = DiaSemana.valueOf(k);
            Set<FranjaHoraria> franjas =
                v.stream().map(FranjaHoraria::valueOf).collect(Collectors.toSet());
            map.put(dia, franjas);
          } catch (IllegalArgumentException ex) {
            // ignore invalid keys/values
          }
        });
    return DisponibilidadSemanal.of(map);
  }
}
