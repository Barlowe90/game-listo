package com.gamelisto.biblioteca.shared;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "GameListo - Biblioteca API",
            version = "v1",
            description = "Microservicio biblioteca: gestion de listas y estados del videojuego"))
public class OpenApiConfig {}
