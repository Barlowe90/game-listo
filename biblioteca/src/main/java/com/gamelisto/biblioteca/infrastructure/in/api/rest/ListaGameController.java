package com.gamelisto.biblioteca.infrastructure.in.api.rest;

import com.gamelisto.biblioteca.application.usecase.crearlistagame.CrearListaGameCommand;
import com.gamelisto.biblioteca.application.usecase.crearlistagame.CrearListaGameHandler;
import com.gamelisto.biblioteca.application.usecase.crearlistagame.CrearListaGameResult;
import com.gamelisto.biblioteca.application.usecase.editarlistagame.EditarListaGameCommand;
import com.gamelisto.biblioteca.application.usecase.editarlistagame.EditarListaGameHandler;
import com.gamelisto.biblioteca.application.usecase.editarlistagame.EditarListaGameResult;
import com.gamelisto.biblioteca.infrastructure.in.api.rest.dto.EditarListaGameRequest;
import com.gamelisto.biblioteca.infrastructure.in.api.rest.dto.ListaGameResponse;
import com.gamelisto.biblioteca.infrastructure.in.api.rest.dto.CrearListaGameRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
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

  // TODO preguntar a Diego si esta bien así o es verboso
  @PostMapping("/lists")
  public ResponseEntity<ListaGameResponse> crearLista(
      @Valid @RequestBody CrearListaGameRequest request) {

    logger.info("Crear nueva lista");

    CrearListaGameCommand command = enviarInfoRequestToCommand(request);

    CrearListaGameResult result = llamarCasoDeUso(command);

    return ResponseEntity.status(HttpStatus.CREATED).body(ListaGameResponse.from(result));
  }

  private CrearListaGameResult llamarCasoDeUso(CrearListaGameCommand command) {
    return crearLista.execute(command);
  }

  private static @NonNull CrearListaGameCommand enviarInfoRequestToCommand(
      CrearListaGameRequest request) {
    return request.toCommand();
  }

  @PatchMapping("/lists/{idLista}")
  public ResponseEntity<ListaGameResponse> modificarLista(
      @PathVariable String idLista, @Valid @RequestBody EditarListaGameRequest request) {
    logger.info("Cambiar nombre lista");
    EditarListaGameCommand command = request.toCommand();
    EditarListaGameResult result = editarLista.execute(command);
    return ResponseEntity.status(HttpStatus.OK).body(ListaGameResponse.from(result));
  }
}
