package com.gamelisto.biblioteca.infrastructure.in.api.rest;

import com.gamelisto.biblioteca.application.usecase.crearlistagame.CrearListaGameCommand;
import com.gamelisto.biblioteca.application.usecase.crearlistagame.CrearListaGameResult;
import com.gamelisto.biblioteca.application.usecase.crearlistagame.CrearListaGameUseCase;
import com.gamelisto.biblioteca.infrastructure.in.api.rest.dto.ListaGameResponse;
import com.gamelisto.biblioteca.infrastructure.in.api.rest.dto.CrearListaGameRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/biblioteca")
@RequiredArgsConstructor
public class ListaGameController {

  private static final Logger logger = LoggerFactory.getLogger(ListaGameController.class);
  private final CrearListaGameUseCase crearListaUseCase;

  @PostMapping("/lists")
  public ResponseEntity<ListaGameResponse> crearLista(@RequestBody CrearListaGameRequest request) {
    CrearListaGameCommand command = request.toCommand();
    CrearListaGameResult result = crearListaUseCase.execute(command);

    return ResponseEntity.status(HttpStatus.CREATED).body(ListaGameResponse.from(result));
  }
}
