package com.gamelisto.publicaciones.infraestructure.in.api;

import com.gamelisto.publicaciones.application.usecases.*;
import com.gamelisto.publicaciones.infraestructure.in.api.dto.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/publicaciones")
@RequiredArgsConstructor
public class PeticionesUnionGrupoController {

  private static final Logger logger =
      LoggerFactory.getLogger(PeticionesUnionGrupoController.class);
  private final BuscarPeticionesUnionEnviadasHandler buscarPeticionesEnviadas;
  private final BuscarPeticionesUnionRecibidasHandler buscarPeticionesRecibidas;
  private final BuscarPeticionesUnionRecibidasEnLaPublicacionHandler
      buscarPeticionesUnionRecibidasEnLaPublicacion;
  private final AceptarORechazarPeticionHandle aceptarORechazarPeticionHandle;
  private final BuscarGrupoJuegoHandle buscarGrupoJuegoHandle;
  private final AbandonarGrupoHandler abandonarGrupo;

  @GetMapping("/peticiones-union/enviadas")
  public ResponseEntity<List<PeticionUnionResponse>> obtenerPeticionesUnionEnviadas(
      Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());
    logger.info("Listar peticiones de union enviadas por el usuario", userId);

    List<PeticionUnionResult> result = buscarPeticionesEnviadas.execute(userId);

    List<PeticionUnionResponse> response =
        result.stream().map(PeticionUnionResponse::from).toList();

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/peticiones-union/recibidas")
  public ResponseEntity<List<PeticionUnionResponse>> obtenerPeticionUnionRecibidas(
      Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());
    logger.info("Listar peticiones de union recibidas por el usuario", userId);

    List<PeticionUnionResult> result = buscarPeticionesRecibidas.execute(userId);

    List<PeticionUnionResponse> response =
        result.stream().map(PeticionUnionResponse::from).toList();

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{publicacionId}/peticiones-union")
  public ResponseEntity<List<PeticionUnionResponse>> obtenerPeticionesUnion(
      @PathVariable UUID publicacionId, Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());
    logger.info("Listar peticiones recibidas a la publicacion {}", publicacionId);

    List<PeticionUnionResult> result =
        buscarPeticionesUnionRecibidasEnLaPublicacion.execute(userId, publicacionId);

    List<PeticionUnionResponse> response =
        result.stream().map(PeticionUnionResponse::from).toList();

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PatchMapping("/peticiones-union/{peticionId}") // aceptar/rechazar peticion
  public ResponseEntity<PeticionUnionResponse> aceptarORechazarPeticion(
      @PathVariable UUID peticionId,
      @Valid @RequestBody PeticionUnionRequest request,
      Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());
    logger.info(
        "Aceptar o rechazar peticion para la peticion {} por el usuario {} ", peticionId, userId);

    PeticionUnionResult result =
        aceptarORechazarPeticionHandle.execute(request.toCommand(peticionId, userId));

    return ResponseEntity.status(HttpStatus.OK).body(PeticionUnionResponse.from(result));
  }

  @PostMapping("/{publicacionId}/abandonar-grupo") // abandonar grupo
  public ResponseEntity<PeticionUnionResponse> abandonarGrupo(
      @PathVariable UUID publicacionId, Authentication authentication) {
    logger.info("Peticion de union para la publicacion {}", publicacionId);
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());

    abandonarGrupo.execute(publicacionId, userId);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/grupos/{grupoId}") // obtener detalles grupo
  public ResponseEntity<GrupoJuegoResponse> obtenerGrupoJuego(@PathVariable UUID grupoId) {
    logger.info("Obtener datos grupo juego con id {}", grupoId);

    GrupoJuegoResult result = buscarGrupoJuegoHandle.execute(grupoId);

    return ResponseEntity.status(HttpStatus.OK).body(GrupoJuegoResponse.from(result));
  }
}
