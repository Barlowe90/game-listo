package com.gamelisto.social.infrastructure.in.api.dto;

import com.gamelisto.social.application.usecases.ResumenSocialJuegoResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResumenSocialJuegoResponse {
  private long amigosDeseadoCount;
  private long amigosJugandoCount;
  private List<UsuarioRefResponse> amigosDeseadoPreview;
  private List<UsuarioRefResponse> amigosJugandoPreview;

  public static ResumenSocialJuegoResponse from(ResumenSocialJuegoResult r) {
    List<UsuarioRefResponse> wishlisted =
        r.amigosDeseadoPreview().stream()
            .map(u -> new UsuarioRefResponse(u.id(), u.username(), u.avatar()))
            .toList();

    List<UsuarioRefResponse> playing =
        r.amigosJugandoPreview().stream()
            .map(u -> new UsuarioRefResponse(u.id(), u.username(), u.avatar()))
            .toList();

    return new ResumenSocialJuegoResponse(
        r.amigosDeseadoCount(), r.amigosJugandoCount(), wishlisted, playing);
  }
}
