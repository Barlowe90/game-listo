package com.gamelist.catalogo.shared.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "GameListo - Catalogo API",
            version = "v1",
            description = "Microservicio Catalogo: ingesta de IGDB"))
public class OpenApiConfig {}
