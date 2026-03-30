package com.gamelisto.biblioteca.infrastructure.in.api;

import com.gamelisto.biblioteca.application.usecase.BuscarGameEstadosPorGameIdHandler;
import com.gamelisto.biblioteca.application.usecase.CrearGameEstadoHandler;
import com.gamelisto.biblioteca.application.usecase.EliminarGameEstadoHandler;
import com.gamelisto.biblioteca.application.usecase.RateGameEstadoHandler;
import com.gamelisto.biblioteca.infrastructure.in.api.dto.CrearGameEstadoRequest;
import com.gamelisto.biblioteca.infrastructure.in.api.dto.GameEstadoResponse;
import com.gamelisto.biblioteca.infrastructure.in.api.dto.RateGameEstadoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/biblioteca")
@RequiredArgsConstructor
public class GameEstadoController {

  private static final Logger logger = LoggerFactory.getLogger(GameEstadoController.class);
  private final RateGameEstadoHandler rateGameEstado;
  private final CrearGameEstadoHandler crearGameEstado;
  private final EliminarGameEstadoHandler eliminarGameEstado;
  private final BuscarGameEstadosPorGameIdHandler buscarGameEstadosPorGameId;

  @PostMapping("/games/{gameRefId}/state")
  public ResponseEntity<Void> crearGameEstado(
      @AuthenticationPrincipal UUID userId,
      @PathVariable String gameRefId,
      @Valid @RequestBody CrearGameEstadoRequest request) {

    logger.info("Crear Game Estado para el juego con id {}", gameRefId);

    crearGameEstado.execute(request.toCommand(userId, gameRefId));

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @DeleteMapping("/games/{gameRefId}/state")
  public ResponseEntity<Void> eliminarGameEstado(
      @AuthenticationPrincipal UUID userId, @PathVariable String gameRefId) {

    logger.info("Eliminar Game Estado para el juego con id {}", gameRefId);

    eliminarGameEstado.execute(userId, gameRefId);

    return ResponseEntity.noContent().build();
  }

  @PostMapping("/games/{gameRefId}/rate")
  public ResponseEntity<Void> rateGameEstado(
      @AuthenticationPrincipal UUID userId,
      @PathVariable String gameRefId,
      @Valid @RequestBody RateGameEstadoRequest request) {

    logger.info("Puntuar juego con id {}", gameRefId);

    rateGameEstado.execute(request.toCommand(userId, gameRefId));

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @GetMapping("/games/{gameRefId}")
  public ResponseEntity<List<GameEstadoResponse>> getGameEstadosByGameRefId(
      @PathVariable Long gameRefId) {
    logger.info("Obtener todos los GameEstado para el gameRefId {}", gameRefId);

    List<GameEstadoResponse> response =
        buscarGameEstadosPorGameId.execute(gameRefId).stream()
            .map(GameEstadoResponse::from)
            .toList();

    return ResponseEntity.ok(response);
  }
}
