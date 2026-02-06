package com.gamelist.catalogo_service.infrastructure.api.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/catalogo")
@Tag(name = "Catalogo de videojuegos", description = "Ingesta y gestion de videojuegos de IGDB")
public class CatalogoContoller {

  @GetMapping("/health")
  public String health() {
    return "Catalogo Service is running!";
  }

  @GetMapping("/igdb/sync")
  public void sincronizarDatosConIGDB() {
    String body =
        """
            fields id, name, summary, cover.url;
            sort id asc;
            limit 500;
            offset 0;
        """;
  }
}
