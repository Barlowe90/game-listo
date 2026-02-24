package com.gamelisto.biblioteca.infrastructure.in.api.rest;

import com.gamelisto.biblioteca.application.usecase.crearlistagame.CrearListaGameCommand;
import com.gamelisto.biblioteca.application.usecase.crearlistagame.CrearListaGameHandler;
import com.gamelisto.biblioteca.application.usecase.crearlistagame.CrearListaGameResult;
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

  @PostMapping("/lists")
  public ResponseEntity<ListaGameResponse> crearLista(
      @Valid @RequestBody CrearListaGameRequest request) {
    logger.info("Crear nueva lista");
    CrearListaGameCommand command = request.toCommand();
    CrearListaGameResult result = crearLista.execute(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(ListaGameResponse.from(result));
  }

  @PatchMapping("/lists/{idLista}")
  public ResponseEntity<ListaGameResponse> modificarLista(@Valid @RequestBody ){

  }
}
