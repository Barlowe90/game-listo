package com.gamelisto.publicaciones.infrastructure.in.api;

import com.gamelisto.publicaciones.application.usecases.*;
import com.gamelisto.publicaciones.infrastructure.in.api.dto.*;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/publicaciones")
@RequiredArgsConstructor
public class GrupoJuegoController {

  private static final Logger logger = LoggerFactory.getLogger(GrupoJuegoController.class);
  private final BuscarGrupoJuegoHandle buscarGrupoJuegoHandle;
  private final AbandonarGrupoHandler abandonarGrupo;

  @PostMapping("/{publicacionId}/abandonar-grupo")
  public ResponseEntity<SolicitudUnionResponse> abandonarGrupo(
      @PathVariable UUID publicacionId, @AuthenticationPrincipal UUID userId) {
    logger.info("Abandonando grupo para la publicacion {}", publicacionId);

    abandonarGrupo.execute(publicacionId, userId);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/grupos/{grupoId}")
  public ResponseEntity<GrupoJuegoResponse> obtenerGrupoJuego(@PathVariable UUID grupoId) {
    logger.info("Obtener datos grupo juego con id {}", grupoId);

    GrupoJuegoResult result = buscarGrupoJuegoHandle.execute(grupoId);

    return ResponseEntity.status(HttpStatus.OK).body(GrupoJuegoResponse.from(result));
  }
}
