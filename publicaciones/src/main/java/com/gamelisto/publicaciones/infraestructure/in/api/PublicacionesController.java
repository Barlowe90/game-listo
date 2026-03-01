package com.gamelisto.publicaciones.infraestructure.in.api;

import com.gamelisto.publicaciones.application.usecases.*;
import com.gamelisto.publicaciones.infraestructure.in.api.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
  private final PeticionUnionPublicacionHandler peticionUnionPublicacion;
  private final BuscarPeticionesUnionEnviadasHandler buscarPeticionesEnviadas;
  private final BuscarPeticionesUnionRecibidasHandler buscarPeticionesRecibidas;
  private final BuscarPeticionesUnionRecibidasEnLaPublicacionHandler
      buscarPeticionesUnionRecibidasEnLaPublicacion;
  private final AceptarORechazarPeticionHandle aceptarORechazarPeticionHandle;
  private final BuscarPublicacionesUsuarioHandler buscarPublicacionesUsuario;
  private final BuscarPublicacionesPorJuegoHandler buscarPublicacionesPorJuego;
  private final BuscarGrupoJuegoHandle buscarGrupoJuegoHandle;
  private final AbandonarGrupoHandler abandonarGrupo;

  @PostMapping
  public ResponseEntity<PublicacionResponse> crearPublicacion(
      @Valid @RequestBody CrearPublicacionRequest request, Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());

    logger.info("Crear nueva publicacion para el usuario {}", userId);

    PublicacionResult result = crearPublicacion.execute(request.toCommand(userId));

    return ResponseEntity.status(HttpStatus.CREATED).body(PublicacionResponse.from(result));
  }

  @GetMapping
  public ResponseEntity<List<PublicacionResponse>> obtenerTodasLasPublicaciones() {
    logger.info("Obtener todas las publicaciones");

    List<PublicacionResult> result = buscarTodasLasPublicaciones.execute();

    List<PublicacionResponse> response = result.stream().map(PublicacionResponse::from).toList();

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/{publicacionId}")
  public ResponseEntity<PublicacionDetalleResponse> obtenerPubliacion(
      @PathVariable String publicacionId) {
    logger.info("Obtener datos de la publicacion {}", publicacionId);

    PublicacionDetalleResult result = buscarPublicacion.execute(publicacionId);

    return ResponseEntity.status(HttpStatus.OK).body(PublicacionDetalleResponse.from(result));
  }

  @PutMapping("/{publicacionId}")
  public ResponseEntity<PublicacionResponse> editarPublicacion(
      @PathVariable UUID publicacionId,
      @Valid @RequestBody EditarPublicacionRequest request,
      Authentication authentication) {
    logger.info("Editar la publicacion {}", publicacionId);
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());

    PublicacionResult result = editarPublicacion.execute(request.toCommand(publicacionId, userId));

    return ResponseEntity.status(HttpStatus.OK).body(PublicacionResponse.from(result));
  }

  @DeleteMapping("/{publicacionId}")
  public ResponseEntity<Void> eliminarPublicacion(
      @PathVariable UUID publicacionId, Authentication authentication) {
    logger.info("Eliminar la publicacion {}", publicacionId);
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());

    eliminarPublicacion.execute(publicacionId, userId);

    return ResponseEntity.noContent().build();
  }

  @PostMapping("/{publicacionId}/peticion-union")
  public ResponseEntity<PeticionUnionResponse> crearPeticionUnion(
      @PathVariable UUID publicacionId, Authentication authentication) {
    logger.info("Peticion de union para la publicacion {}", publicacionId);
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());

    PeticionUnionResult result = peticionUnionPublicacion.execute(publicacionId, userId);

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

  @GetMapping("/user") // listar publicaciones creadas por usuario
  public ResponseEntity<List<PublicacionResponse>> obtenerPublicacionesUsuario(
      Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getPrincipal().toString());
    logger.info("Buscar las publicaciones del usuario {}", userId);

    List<PublicacionResult> result = buscarPublicacionesUsuario.execute(userId);

    List<PublicacionResponse> response = result.stream().map(PublicacionResponse::from).toList();

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/game/{gameId}") // listar publicaciones de un juego
  public ResponseEntity<List<PublicacionResponse>> obtenerPublicacionesPorJuego(
      @PathVariable Long gameId) {
    logger.info("Buscar las publicaciones del juego {}", gameId);

    List<PublicacionResult> result = buscarPublicacionesPorJuego.execute(gameId);

    List<PublicacionResponse> response = result.stream().map(PublicacionResponse::from).toList();

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @GetMapping("/grupos/{grupoId}") // obtener detalles grupo
  public ResponseEntity<GrupoJuegoResponse> obtenerGrupoJuego(@PathVariable UUID grupoId) {
    logger.info("Obtener datos grupo juego con id {}", grupoId);

    GrupoJuegoResult result = buscarGrupoJuegoHandle.execute(grupoId);

    return ResponseEntity.status(HttpStatus.OK).body(GrupoJuegoResponse.from(result));
  }
}
