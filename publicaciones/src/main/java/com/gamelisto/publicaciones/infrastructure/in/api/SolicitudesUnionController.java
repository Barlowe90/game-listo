package com.gamelisto.publicaciones.infrastructure.in.api;

import com.gamelisto.publicaciones.application.usecases.*;
import com.gamelisto.publicaciones.infrastructure.in.api.dto.*;
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
public class SolicitudesUnionController {

  private static final Logger logger = LoggerFactory.getLogger(SolicitudesUnionController.class);
  private final CrearSolicitudUnionHandler crearSolicitud;
  private final BuscarSolicitudesUnionEnviadasHandler buscarSolicitudesEnviadas;
  private final BuscarSolicitudesUnionRecibidasHandler buscarSolicitudesRecibidas;
  private final BuscarSolicitudesUnionRecibidasEnLaPublicacionHandler
      buscarSolicitudesUnionRecibidasEnLaPublicacion;
  private final AceptarORechazarPeticionHandle aceptarORechazarPeticionHandle;

  @PostMapping("/{publicacionId}/solicitud-union")
  public ResponseEntity<SolicitudUnionResponse> crearSolicitudUnion(
      @PathVariable UUID publicacionId, Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());

    logger.info(
        "Crear nueva solicitud por el usuario {} para la publicacion {}", userId, publicacionId);

    SolicitudUnionResult result = crearSolicitud.execute(publicacionId, userId);

    return ResponseEntity.status(HttpStatus.CREATED).body(SolicitudUnionResponse.from(result));
  }

  @PatchMapping("/solicitudes-union/{solicitudId}")
  public ResponseEntity<SolicitudUnionResponse> aceptarORechazarSolicitud(
      @PathVariable UUID solicitudId,
      @Valid @RequestBody SolicitudUnionRequest request,
      Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());
    logger.info(
        "{} solicitud con id {} por el usuario {} ",
        request.estadoSolicitud(),
        solicitudId,
        userId);

    SolicitudUnionResult result =
        aceptarORechazarPeticionHandle.execute(request.toCommand(solicitudId, userId));

    return ResponseEntity.status(HttpStatus.OK).body(SolicitudUnionResponse.from(result));
  }

  @GetMapping("/solicitudes-union/enviadas")
  public ResponseEntity<List<SolicitudUnionResponse>> obtenerSolicitudesUnionEnviadas(
      Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());
    logger.info("Listar solicitudes de union enviadas por el usuario {}", userId);

    List<SolicitudUnionResult> result = buscarSolicitudesEnviadas.execute(userId);

    List<SolicitudUnionResponse> response =
        result.stream().map(SolicitudUnionResponse::from).toList();

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/solicitudes-union/recibidas")
  public ResponseEntity<List<SolicitudUnionResponse>> obtenerSolicitudesUnionRecibidas(
      Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());
    logger.info("Listar solicitudes de union recibidas por el usuario {}", userId);

    List<SolicitudUnionResult> result = buscarSolicitudesRecibidas.execute(userId);

    List<SolicitudUnionResponse> response =
        result.stream().map(SolicitudUnionResponse::from).toList();

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{publicacionId}/solicitudes-union")
  public ResponseEntity<List<SolicitudUnionResponse>> obtenerSolicitudesUnion(
      @PathVariable UUID publicacionId, Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());
    logger.info("Listar solicitudes recibidas a la publicacion {}", publicacionId);

    List<SolicitudUnionResult> result =
        buscarSolicitudesUnionRecibidasEnLaPublicacion.execute(userId, publicacionId);

    List<SolicitudUnionResponse> response =
        result.stream().map(SolicitudUnionResponse::from).toList();

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
