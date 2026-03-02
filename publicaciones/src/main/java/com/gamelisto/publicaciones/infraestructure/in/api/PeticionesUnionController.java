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
public class PeticionesUnionController {

  private static final Logger logger = LoggerFactory.getLogger(PeticionesUnionController.class);
  private final CrearSolicitudUnionHandler crearSolicitud;
  private final BuscarPeticionesUnionEnviadasHandler buscarPeticionesEnviadas;
  private final BuscarPeticionesUnionRecibidasHandler buscarPeticionesRecibidas;
  private final BuscarPeticionesUnionRecibidasEnLaPublicacionHandler
      buscarPeticionesUnionRecibidasEnLaPublicacion;
  private final AceptarORechazarPeticionHandle aceptarORechazarPeticionHandle;

  @PostMapping("/{publicacionId}/solicitud-union")
  public ResponseEntity<PeticionUnionResponse> crearSolicitudUnion(
      @PathVariable UUID publicacionId, Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());

    logger.info(
        "Crear nueva solicitud por el usuario {} para la publicacion {}", userId, publicacionId);

    PeticionUnionResult result = crearSolicitud.execute(userId, publicacionId);

    return ResponseEntity.status(HttpStatus.CREATED).body(PeticionUnionResponse.from(result));
  }

  @PatchMapping("/peticiones-union/{peticionId}")
  public ResponseEntity<PeticionUnionResponse> aceptarORechazarPeticion(
      @PathVariable UUID peticionId,
      @Valid @RequestBody PeticionUnionRequest request,
      Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());
    logger.info(
        request.estadoSolicitud() + " peticion con id {} por el usuario {} ", peticionId, userId);

    PeticionUnionResult result =
        aceptarORechazarPeticionHandle.execute(request.toCommand(peticionId, userId));

    return ResponseEntity.status(HttpStatus.OK).body(PeticionUnionResponse.from(result));
  }

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
}
