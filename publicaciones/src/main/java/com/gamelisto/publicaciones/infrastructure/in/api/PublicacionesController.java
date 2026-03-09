package com.gamelisto.publicaciones.infrastructure.in.api;

import com.gamelisto.publicaciones.application.usecases.*;
import com.gamelisto.publicaciones.infrastructure.in.api.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/publicaciones")
@RequiredArgsConstructor
public class PublicacionesController {

  private static final Logger logger = LoggerFactory.getLogger(PublicacionesController.class);
  private final CrearPublicacionHandler crearPublicacion;
  private final BuscarTodasLasPublicacionesHandler buscarTodasLasPublicaciones;
  private final BuscarPublicacionHandler buscarPublicacion;
  private final EditarPublicacionHandler editarPublicacion;
  private final EliminarPublicacionHandler eliminarPublicacion;
  private final BuscarPublicacionesUsuarioHandler buscarPublicacionesUsuario;
  private final BuscarPublicacionesPorJuegoHandler buscarPublicacionesPorJuego;

  @PostMapping
  @PreAuthorize("#userId == authentication.principal")
  public ResponseEntity<PublicacionResponse> crearPublicacion(
      @Valid @RequestBody CrearPublicacionRequest request, @AuthenticationPrincipal UUID userId) {

    logger.info("Crear nueva publicacion para el usuario {}", userId);

    PublicacionResult result = crearPublicacion.execute(request.toCommand(userId));

    return ResponseEntity.status(HttpStatus.CREATED).body(PublicacionResponse.from(result));
  }

  @PutMapping("/{publicacionId}")
  @PreAuthorize("#userId == authentication.principal")
  public ResponseEntity<PublicacionResponse> editarPublicacion(
      @PathVariable UUID publicacionId,
      @Valid @RequestBody EditarPublicacionRequest request,
      @AuthenticationPrincipal UUID userId) {
    logger.info("Editar la publicacion {}", publicacionId);

    PublicacionResult result = editarPublicacion.execute(request.toCommand(publicacionId, userId));

    return ResponseEntity.status(HttpStatus.OK).body(PublicacionResponse.from(result));
  }

  @GetMapping("/{publicacionId}")
  public ResponseEntity<PublicacionDetalleResponse> obtenerPublicacion(
      @PathVariable String publicacionId) {
    logger.info("Obtener datos de la publicacion {}", publicacionId);

    PublicacionDetalleResult result = buscarPublicacion.execute(publicacionId);

    return ResponseEntity.status(HttpStatus.OK).body(PublicacionDetalleResponse.from(result));
  }

  @GetMapping
  public ResponseEntity<List<PublicacionResponse>> obtenerTodasLasPublicaciones() {
    logger.info("Obtener todas las publicaciones");

    List<PublicacionResult> result = buscarTodasLasPublicaciones.execute();

    List<PublicacionResponse> response = result.stream().map(PublicacionResponse::from).toList();

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<PublicacionResponse>> obtenerPublicacionesCreadasPorUsuario(
      @PathVariable UUID userId) {
    List<PublicacionResult> result = buscarPublicacionesUsuario.execute(userId);

    List<PublicacionResponse> response = result.stream().map(PublicacionResponse::from).toList();

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/game/{gameId}")
  public ResponseEntity<List<PublicacionResponse>> obtenerPublicacionesPorJuego(
      @PathVariable Long gameId) {
    logger.info("Buscar las publicaciones del juego {}", gameId);

    List<PublicacionResult> result = buscarPublicacionesPorJuego.execute(gameId);

    List<PublicacionResponse> response = result.stream().map(PublicacionResponse::from).toList();

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @DeleteMapping("/{publicacionId}")
  @PreAuthorize("#userId == authentication.principal")
  public ResponseEntity<Void> eliminarPublicacion(
      @PathVariable UUID publicacionId, @AuthenticationPrincipal UUID userId) {
    logger.info("Eliminar la publicacion {}", publicacionId);

    eliminarPublicacion.execute(publicacionId, userId);

    return ResponseEntity.noContent().build();
  }
}
