package com.gamelisto.catalogo.infrastructure.out.dto;

import com.gamelisto.catalogo.application.dto.out.PlatformDTO;

/** Response DTO para información de una plataforma. */
public record PlatformResponse(
    Long id,
    String name,
    String abbreviation,
    String alternativeName,
    String logoURL,
    String tipo) {

  public static PlatformResponse from(PlatformDTO dto) {
    return new PlatformResponse(
        dto.id(), dto.name(), dto.abbreviation(), dto.alternativeName(), dto.logoURL(), dto.tipo());
  }
}
