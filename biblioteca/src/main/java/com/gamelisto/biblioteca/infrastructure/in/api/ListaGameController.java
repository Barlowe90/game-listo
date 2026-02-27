package com.gamelisto.biblioteca.infrastructure.in.api;

import com.gamelisto.biblioteca.application.usecase.AddGameToListHandler;
import com.gamelisto.biblioteca.application.usecase.BuscarListaGameHandler;
import com.gamelisto.biblioteca.application.usecase.ListaGameResult;
import com.gamelisto.biblioteca.application.usecase.BuscarTodasLasListasGameHandler;
import com.gamelisto.biblioteca.application.usecase.CrearListaGameHandler;
import com.gamelisto.biblioteca.application.usecase.EditarListaGameHandler;
import com.gamelisto.biblioteca.application.usecase.EliminarGameFromListHandler;
import com.gamelisto.biblioteca.application.usecase.EliminarListaGameHandler;
import com.gamelisto.biblioteca.infrastructure.in.api.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/biblioteca")
@RequiredArgsConstructor
public class ListaGameController {

  private static final Logger logger = LoggerFactory.getLogger(ListaGameController.class);
  private final CrearListaGameHandler crearLista;
  private final EditarListaGameHandler editarLista;
  private final EliminarListaGameHandler eliminarLista;
  private final BuscarListaGameHandler buscarLista;
  private final BuscarTodasLasListasGameHandler buscarTodasLasListas;
  private final AddGameToListHandler addGameToList;
  private final EliminarGameFromListHandler eliminarGameFromList;

  @PreAuthorize("isAuthenticated()")
  @PostMapping("/lists")
  public ResponseEntity<ListaGameResponse> crearLista(
      @Valid @RequestBody CrearListaGameRequest request, Authentication authentication) {
    String userId = (String) authentication.getPrincipal();

    logger.info("Crear nueva lista para el usuario {}", userId);

    ListaGameResult result = crearLista.execute(request.toCommand(userId));

    return ResponseEntity.status(HttpStatus.CREATED).body(ListaGameResponse.from(result));
  }

  @PreAuthorize("hasRole('ADMIN') or #userId.toString() == authentication.principal")
  @PatchMapping("/user/{userId}/lists/{listaId}")
  public ResponseEntity<ListaGameResponse> modificarLista(
      @PathVariable String listaId,
      @PathVariable String userId,
      @Valid @RequestBody EditarListaGameRequest request) {

    logger.info("Cambiar nombre lista con id {} del usuario {}", listaId, userId);

    ListaGameResult result = editarLista.execute(request.toCommand(userId, listaId));

    return ResponseEntity.status(HttpStatus.OK).body(ListaGameResponse.from(result));
  }

  @PreAuthorize("hasRole('ADMIN') or #userId.toString() == authentication.principal")
  @DeleteMapping("/user/{userId}/lists/{listaId}")
  public ResponseEntity<Void> eliminarLista(
      @PathVariable String userId, @PathVariable String listaId) {

    logger.info("Eliminar lista con id {} del usuario {}", listaId, userId);

    eliminarLista.execute(userId, listaId);

    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN') or #userId.toString() == authentication.principal")
  @GetMapping("/user/{userId}/lists/{listaId}")
  public ResponseEntity<ListaGameResponse> buscarLista(
      @PathVariable String userId, @PathVariable String listaId) {

    logger.info("Buscar lista con id {} del usuario {}", listaId, userId);

    ListaGameResult result = buscarLista.execute(userId, listaId);

    return ResponseEntity.status(HttpStatus.OK).body(ListaGameResponse.from(result));
  }

  @PreAuthorize("hasRole('ADMIN') or #userId.toString() == authentication.principal")
  @GetMapping("/user/{userId}/lists")
  public ResponseEntity<List<ListaGameResponse>> buscarTodasLasListas(@PathVariable String userId) {

    logger.info("Buscar todas las listas del usuario {}", userId);

    List<ListaGameResult> result = buscarTodasLasListas.execute(userId);

    List<ListaGameResponse> response = result.stream().map(ListaGameResponse::from).toList();

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  @PreAuthorize("hasRole('ADMIN') or #userId.toString() == authentication.principal")
  @PostMapping("/user/{userId}/lists/{listaId}/games/{gameRefId}")
  public ResponseEntity<Void> addGameToList(
      @PathVariable String userId, @PathVariable String listaId, @PathVariable String gameRefId) {

    logger.info("Añadir juego con id {} a la lista con id {}", gameRefId, listaId);

    addGameToList.execute(userId, listaId, gameRefId);

    return ResponseEntity.noContent().build();
  }

  @PreAuthorize("hasRole('ADMIN') or #userId.toString() == authentication.principal")
  @DeleteMapping("/user/{userId}/lists/{listaId}/games/{gameRefId}")
  public ResponseEntity<Void> eliminarGameFromList(
      @PathVariable String userId, @PathVariable String listaId, @PathVariable String gameRefId) {

    logger.info("Eliminar juego con id {} de la lista con id {}", gameRefId, listaId);

    eliminarGameFromList.execute(userId, listaId, gameRefId);

    return ResponseEntity.noContent().build();
  }
}
