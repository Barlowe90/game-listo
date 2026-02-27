package com.gamelisto.biblioteca.infrastructure.in.api;

import com.gamelisto.biblioteca.application.usecase.CrearGameEstadoHandler;
import com.gamelisto.biblioteca.application.usecase.RateGameEstadoHandler;
import com.gamelisto.biblioteca.infrastructure.in.api.dto.CrearGameEstadoRequest;
import com.gamelisto.biblioteca.infrastructure.in.api.dto.RateGameEstadoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/biblioteca")
@RequiredArgsConstructor
public class GameEstadoController {

  private static final Logger logger = LoggerFactory.getLogger(GameEstadoController.class);
  private final RateGameEstadoHandler rateGameEstado;
  private final CrearGameEstadoHandler crearGameEstado;

  @PreAuthorize("hasRole('ADMIN') or #userId.toString() == authentication.principal")
  @PostMapping("/user/{userId}/games/{gameRefId}/state")
  public ResponseEntity<Void> crearGameEstado(
      @PathVariable String userId,
      @PathVariable String gameRefId,
      @Valid @RequestBody CrearGameEstadoRequest request) {

    logger.info("Crear Game Estado para el juego con id {}", gameRefId);

    crearGameEstado.execute(request.toCommand(userId, gameRefId));

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PreAuthorize("hasRole('ADMIN') or #userId.toString() == authentication.principal")
  @PostMapping("/user/{userId}/games/{gameRefId}/rate")
  public ResponseEntity<Void> rateGameEstado(
      @PathVariable String userId,
      @PathVariable String gameRefId,
      @Valid @RequestBody RateGameEstadoRequest request) {

    logger.info("Puntuar juego con id {}", gameRefId);

    rateGameEstado.execute(request.toCommand(userId, gameRefId));

    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
