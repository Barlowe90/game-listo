package com.gamelisto.usuarios.shared.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "GameListo - Usuarios API",
            version = "v1",
            description = "Microservicio de usuarios: autenticación y perfiles"))
public class OpenApiConfig {}
