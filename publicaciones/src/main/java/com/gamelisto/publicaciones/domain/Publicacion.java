package com.gamelisto.publicaciones.domain;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class Publicacion {
  private final UUID id;
  private final UUID autorId;
  private final Long gameId;
  private final String titulo;
  private final Idioma idioma;
  private final Experiencia experiencia;
  private final EstiloJuego estiloJuego;
  private final int jugadoresMaximos;
  private final EstadoPublicacion estadoPublicacion;

  private Publicacion(
      UUID id,
      UUID autorId,
      Long gameId,
      String titulo,
      Idioma idioma,
      Experiencia experiencia,
      EstiloJuego estiloJuego,
      int jugadoresMaximos,
      EstadoPublicacion estadoPublicacion) {
    this.id = id;
    this.autorId = autorId;
    this.gameId = gameId;
    this.titulo = titulo;
    this.idioma = idioma;
    this.experiencia = experiencia;
    this.estiloJuego = estiloJuego;
    this.jugadoresMaximos = jugadoresMaximos;
    this.estadoPublicacion = estadoPublicacion;
  }

  public static Publicacion create(
      UUID autorId,
      Long gameId,
      String titulo,
      Idioma idioma,
      Experiencia experiencia,
      EstiloJuego estiloJuego,
      int jugadoresMaximos) {
    return new Publicacion(
        UUID.randomUUID(),
        autorId,
        gameId,
        titulo,
        idioma,
        experiencia,
        estiloJuego,
        jugadoresMaximos,
        EstadoPublicacion.PUBLICADA);
  }

  public static Publicacion reconstitute(
      UUID id,
      UUID autorId,
      Long gameId,
      String titulo,
      Idioma idioma,
      Experiencia experiencia,
      EstiloJuego estiloJuego,
      int jugadoresMaximos,
      EstadoPublicacion estadoPublicacion) {
    return new Publicacion(
        id,
        autorId,
        gameId,
        titulo,
        idioma,
        experiencia,
        estiloJuego,
        jugadoresMaximos,
        estadoPublicacion);
  }
}
