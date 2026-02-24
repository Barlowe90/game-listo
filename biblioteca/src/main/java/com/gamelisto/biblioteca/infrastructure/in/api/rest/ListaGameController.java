package com.gamelisto.biblioteca.infrastructure.in.api.rest;

import com.gamelisto.biblioteca.application.usecase.buscarlistagame.BuscarListaGameHandler;
import com.gamelisto.biblioteca.application.usecase.ListaGameResult;
import com.gamelisto.biblioteca.application.usecase.crearlistagame.CrearListaGameHandler;
import com.gamelisto.biblioteca.application.usecase.editarlistagame.EditarListaGameHandler;
import com.gamelisto.biblioteca.application.usecase.eliminarlista.EliminarListaGameHandler;
import com.gamelisto.biblioteca.infrastructure.in.api.rest.dto.EditarListaGameRequest;
import com.gamelisto.biblioteca.infrastructure.in.api.rest.dto.ListaGameResponse;
import com.gamelisto.biblioteca.infrastructure.in.api.rest.dto.CrearListaGameRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/biblioteca")
@RequiredArgsConstructor
public class ListaGameController {

  private static final Logger logger = LoggerFactory.getLogger(ListaGameController.class);
  private final CrearListaGameHandler crearLista;
  private final EditarListaGameHandler editarLista;
  private final EliminarListaGameHandler eliminarLista;
  private final BuscarListaGameHandler buscarLista;

  @PostMapping("/lists")
  public ResponseEntity<ListaGameResponse> crearLista(
      @Valid @RequestBody CrearListaGameRequest request) {

    logger.info("Crear nueva lista");

    ListaGameResult result = crearLista.execute(request.toCommand());

    return ResponseEntity.status(HttpStatus.CREATED).body(ListaGameResponse.from(result));
  }

  @PatchMapping("/lists/{listaId}")
  public ResponseEntity<ListaGameResponse> modificarLista(
      @PathVariable String listaId, @Valid @RequestBody EditarListaGameRequest request) {

    logger.info("Cambiar nombre lista");

    ListaGameResult result = editarLista.execute(request.toCommand(listaId));

    return ResponseEntity.status(HttpStatus.OK).body(ListaGameResponse.from(result));
  }

  @DeleteMapping("/lists/{listaId}")
  public ResponseEntity<Void> eliminarLista(@PathVariable String listaId) {

    logger.info("Eliminar lista con id {} ", listaId);

    eliminarLista.execute(listaId);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/lists/{listaId}")
  public ResponseEntity<ListaGameResponse> buscarLista(@PathVariable String listaId) {

    logger.info("Buscar lista con id {} ", listaId);

    ListaGameResult result = buscarLista.execute(listaId);

    return ResponseEntity.status(HttpStatus.OK).body(ListaGameResponse.from(result));
  }

  @DeleteMapping("/lists/{listaId}/games/{gameId}")
  public ResponseEntity<ListaGameResponse> eliminarGameFromList(
      @PathVariable String listaId, @PathVariable String gameId) {

    logger.info("Eliminar juego con id {} de la lista con id {}", gameId, listaId);

    ListaGameResult result = buscarLista.execute(listaId);

    return ResponseEntity.status(HttpStatus.OK).body(ListaGameResponse.from(result));
  }
}
