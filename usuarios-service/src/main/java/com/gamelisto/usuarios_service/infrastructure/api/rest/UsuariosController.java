package com.gamelisto.usuarios_service.infrastructure.api.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.application.usecases.CrearUsuarioUseCase;
import com.gamelisto.usuarios_service.application.usecases.EditarPerfilUsuarioUseCase;
import com.gamelisto.usuarios_service.application.usecases.EliminarUsuarioUseCase;
import com.gamelisto.usuarios_service.application.usecases.ObtenerTodosLosUsuariosUseCase;
import com.gamelisto.usuarios_service.application.usecases.ObtenerUsuarioPorId;
import com.gamelisto.usuarios_service.infrastructure.api.dto.CrearUsuarioRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.EditarPerfilUsuarioRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.UsuarioResponse;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
public class UsuariosController {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuariosController.class);
    private final CrearUsuarioUseCase crearUsuarioUseCase;
    private final EditarPerfilUsuarioUseCase editarPerfilUsuarioUseCase;
    private final ObtenerTodosLosUsuariosUseCase obtenerTodosLosUsuariosUseCase;
    private final ObtenerUsuarioPorId obtenerUsuarioPorId;
    private final EliminarUsuarioUseCase eliminarUsuarioUseCase;

    public UsuariosController(
            CrearUsuarioUseCase crearUsuarioUseCase,
            EditarPerfilUsuarioUseCase editarPerfilUsuarioUseCase,
            ObtenerTodosLosUsuariosUseCase obtenerTodosLosUsuariosUseCase,
            ObtenerUsuarioPorId obtenerUsuarioPorId,
            EliminarUsuarioUseCase eliminarUsuarioUseCase) {
        this.crearUsuarioUseCase = crearUsuarioUseCase;
        this.editarPerfilUsuarioUseCase = editarPerfilUsuarioUseCase;
        this.obtenerTodosLosUsuariosUseCase = obtenerTodosLosUsuariosUseCase;
        this.obtenerUsuarioPorId = obtenerUsuarioPorId;
        this.eliminarUsuarioUseCase = eliminarUsuarioUseCase;
    }

    @GetMapping(value = "/health")
    public void health(){
        logger.info("✅ Microservicio usuarios funcionando correctamente.");
    }

    @PostMapping(value = "/auth/register", consumes = "application/json")
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody CrearUsuarioRequest request) {
        logger.info("ℹ️ POST /v1/usuarios/auth/register - Creando usuario con username: {}", request.username());

        UsuarioDTO usuarioDTO = crearUsuarioUseCase.execute(request.toCommand());

        UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();
        
        logger.info("✅ Usuario creado exitosamente - ID: {}, Username: {}", 
                    response.id(), response.username());

        return ResponseEntity.created(location).body(response);
    }

    @PatchMapping(value = "/user/{id}", consumes = "application/json")
    public ResponseEntity<UsuarioResponse> editarPerfilUsuario(
            @PathVariable String id,
            @Valid @RequestBody EditarPerfilUsuarioRequest request) {
        logger.info("ℹ️ PATCH /v1/usuarios/user/{id} - Editando perfil de usuario con ID: {}", id);

        UsuarioDTO usuarioDTO = editarPerfilUsuarioUseCase.execute(request.toCommand(id));

        UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

        logger.info("✅ Perfil de usuario editado exitosamente - ID: {}, Username: {}", 
                    response.id(), response.username());

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/user/{id}", produces = "application/json")
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorIdEndpoint(@PathVariable String id) {
        logger.info("ℹ️ GET /v1/usuarios/user/{} - Obteniendo usuario con ID: {}", id, id);

        UsuarioDTO usuarioDTO = obtenerUsuarioPorId.execute(id);

        UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

        logger.info("✅ Usuario obtenido exitosamente - ID: {}, Username: {}", 
                    response.id(), response.username());

        return ResponseEntity.ok(response);
    }
    
    @GetMapping(value = "/users", produces = "application/json")
    public List<UsuarioResponse> obtenerUsuarios() {
        logger.info("ℹ️ GET /v1/usuarios/users - Obteniendo lista de usuarios");

        List<UsuarioDTO> usuariosDTO = obtenerTodosLosUsuariosUseCase.execute();

        List<UsuarioResponse> responses = usuariosDTO.stream()
                .map(UsuarioResponse::from)
                .toList();

        logger.info("✅ Lista de usuarios obtenida exitosamente - Total usuarios: {}", responses.size());

        return responses;
    }

    @DeleteMapping(value = "/user/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String id) {
        logger.info("ℹ️ DELETE /v1/usuarios/user/{} - Eliminando usuario con ID: {}", id, id);

        eliminarUsuarioUseCase.execute(id);

        logger.info("✅ Usuario eliminado exitosamente - ID: {}", id);

        return ResponseEntity.noContent().build();
    }    
    
}
