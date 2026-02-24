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

  @PatchMapping("/lists/{idLista}")
  public ResponseEntity<ListaGameResponse> modificarLista(
      @PathVariable String idLista, @Valid @RequestBody EditarListaGameRequest request) {

    logger.info("Cambiar nombre lista");

    ListaGameResult result = editarLista.execute(request.toCommand(idLista));

    return ResponseEntity.status(HttpStatus.OK).body(ListaGameResponse.from(result));
  }

  @DeleteMapping("/lists/{idLista}")
  public ResponseEntity<Void> eliminarLista(@PathVariable String idLista) {

    logger.info("Eliminar lista con id {} ", idLista);

    eliminarLista.execute(idLista);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/lists/{idLista}")
  public ResponseEntity<ListaGameResponse> buscarLista(@PathVariable String idLista) {

    logger.info("Buscar lista con id {} ", idLista);

    ListaGameResult result = buscarLista.execute(idLista);

    return ResponseEntity.status(HttpStatus.OK).body(ListaGameResponse.from(result));
  }
}
