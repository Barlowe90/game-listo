package com.gamelisto.publicaciones.domain;

import com.gamelisto.publicaciones.domain.vo.PublicacionId;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@ToString
public class Publicacion {
  private final PublicacionId id;
  private final UUID autorId;
  private final Long gameId;
  private final String titulo;
  private final Idioma idioma;
  private final Experiencia experiencia;
  private final EstiloJuego estiloJuego;
  private final int jugadoresMaximos;

  private Publicacion(
      PublicacionId id,
      UUID autorId,
      Long gameId,
      String titulo,
      Idioma idioma,
      Experiencia experiencia,
      EstiloJuego estiloJuego,
      int jugadoresMaximos) {
    this.id = id;
    this.autorId = autorId;
    this.gameId = gameId;
    this.titulo = titulo;
    this.idioma = idioma;
    this.experiencia = experiencia;
    this.estiloJuego = estiloJuego;
    this.jugadoresMaximos = jugadoresMaximos;
  }

  public static Publicacion create(
      UUID autorId,
      long gameId,
      String titulo,
      Idioma idioma,
      Experiencia experiencia,
      EstiloJuego estiloJuego,
      int jugadoresMaximos) {
    return new Publicacion(
        PublicacionId.of(UUID.randomUUID()),
        autorId,
        gameId,
        titulo,
        idioma,
        experiencia,
        estiloJuego,
        jugadoresMaximos);
  }

  public static Publicacion reconstitute(
      UUID id,
      UUID autorId,
      Long gameId,
      String titulo,
      Idioma idioma,
      Experiencia experiencia,
      EstiloJuego estiloJuego,
      int jugadoresMaximos) {
    return new Publicacion(
        PublicacionId.of(id),
        autorId,
        gameId,
        titulo,
        idioma,
        experiencia,
        estiloJuego,
        jugadoresMaximos);
  }
}
