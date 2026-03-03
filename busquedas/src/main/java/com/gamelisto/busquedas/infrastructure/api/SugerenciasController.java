package com.gamelisto.busquedas.infrastructure.api;

import com.gamelisto.busquedas.application.usecases.SugerirJuegosHandle;
import com.gamelisto.busquedas.infrastructure.api.dto.SugerirItemResponse;
import com.gamelisto.busquedas.infrastructure.api.dto.SugerenciasResponse;
import com.gamelisto.busquedas.domain.BuscarJuegoDoc;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/busquedas")
@RequiredArgsConstructor
public class SugerenciasController {

  private static final Logger logger = LoggerFactory.getLogger(SugerenciasController.class);
  private final SugerirJuegosHandle sugerirJuegos;

  @GetMapping("/sugerencia")
  public ResponseEntity<SugerenciasResponse> suggest(
      @RequestParam(required = true) String q, @RequestParam(required = false) Integer size) {

    logger.debug("Buscando sugerencias para query='{}', size={}", q, size);

    List<BuscarJuegoDoc> docs = sugerirJuegos.execute(q, size);
    List<SugerirItemResponse> items =
        docs.stream().map(doc -> new SugerirItemResponse(doc.getGameId(), doc.getTitle())).toList();

    return ResponseEntity.ok(new SugerenciasResponse(q, items));
  }
}
