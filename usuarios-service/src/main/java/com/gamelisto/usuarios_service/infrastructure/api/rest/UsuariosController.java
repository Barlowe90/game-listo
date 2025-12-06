package com.gamelisto.usuarios_service.infrastructure.api.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.application.usecases.CrearUsuarioUseCase;
import com.gamelisto.usuarios_service.infrastructure.api.dto.CrearUsuarioRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.UsuarioResponse;

import jakarta.validation.Valid;

import java.net.URI;

@RestController
@RequestMapping("/v1/usuarios")
public class UsuariosController {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuariosController.class);
    
    private final CrearUsuarioUseCase crearUsuarioUseCase;

    public UsuariosController(
            CrearUsuarioUseCase crearUsuarioUseCase) {
        this.crearUsuarioUseCase = crearUsuarioUseCase;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody CrearUsuarioRequest request) {
        logger.info("POST /v1/usuarios - Creando usuario con username: {}", request.username());

        UsuarioDTO usuarioDTO = crearUsuarioUseCase.execute(request.toCommand());

        UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        
        logger.info("Usuario creado exitosamente - ID: {}, Username: {}", 
                    response.id(), response.username());

        return ResponseEntity.created(location).body(response);
    }
    
    // GET /v1/usuarios/{id}
    // GET /v1/usuarios (con filtros)
    // PATCH /v1/usuarios/{id}
    // PATCH /v1/usuarios/{id}/estado
}
