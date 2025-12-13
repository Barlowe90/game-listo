package com.gamelisto.usuarios_service.infrastructure.api.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.application.usecases.CambiarContrasenaUseCase;
import com.gamelisto.usuarios_service.application.usecases.CambiarCorreoUseCase;
import com.gamelisto.usuarios_service.application.usecases.CambiarEstadoUsuarioUseCase;
import com.gamelisto.usuarios_service.application.usecases.CrearUsuarioUseCase;
import com.gamelisto.usuarios_service.application.usecases.EditarPerfilUsuarioUseCase;
import com.gamelisto.usuarios_service.application.usecases.ObtenerTodosLosUsuariosUseCase;
import com.gamelisto.usuarios_service.application.usecases.ObtenerUsuarioPorId;
import com.gamelisto.usuarios_service.application.usecases.ReenviarVerificacionUseCase;
import com.gamelisto.usuarios_service.application.usecases.RestablecerContrasenaUseCase;
import com.gamelisto.usuarios_service.application.usecases.SolicitarRestablecimientoUseCase;
import com.gamelisto.usuarios_service.application.usecases.VerificarEmailUseCase;
import com.gamelisto.usuarios_service.infrastructure.api.dto.CambiarContrasenaRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.CambiarCorreoRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.CambiarEstadoUsuarioRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.CrearUsuarioRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.EditarPerfilUsuarioRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.ReenviarVerificacionRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.RestablecerContrasenaRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.SolicitarRestablecimientoRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.UsuarioResponse;
import com.gamelisto.usuarios_service.infrastructure.api.dto.VerificarEmailRequest;

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
    private final CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;
    private final VerificarEmailUseCase verificarEmailUseCase;
    private final ReenviarVerificacionUseCase reenviarVerificacionUseCase;
    private final CambiarContrasenaUseCase cambiarContraseñaUseCase;
    private final RestablecerContrasenaUseCase restablecerContrasenaUseCase;
    private final SolicitarRestablecimientoUseCase solicitarRestablecimientoUseCase;
    private final CambiarCorreoUseCase cambiarCorreoUseCase;

    public UsuariosController(
            CrearUsuarioUseCase crearUsuarioUseCase,
            EditarPerfilUsuarioUseCase editarPerfilUsuarioUseCase,
            ObtenerTodosLosUsuariosUseCase obtenerTodosLosUsuariosUseCase,
            ObtenerUsuarioPorId obtenerUsuarioPorId,
            CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase,
            VerificarEmailUseCase verificarEmailUseCase,
            ReenviarVerificacionUseCase reenviarVerificacionUseCase,
            CambiarContrasenaUseCase cambiarContraseñaUseCase,
            RestablecerContrasenaUseCase restablecerContrasenaUseCase,
            SolicitarRestablecimientoUseCase solicitarRestablecimientoUseCase,
            CambiarCorreoUseCase cambiarCorreoUseCase) {
        this.crearUsuarioUseCase = crearUsuarioUseCase;
        this.editarPerfilUsuarioUseCase = editarPerfilUsuarioUseCase;
        this.obtenerTodosLosUsuariosUseCase = obtenerTodosLosUsuariosUseCase;
        this.obtenerUsuarioPorId = obtenerUsuarioPorId;
        this.cambiarEstadoUsuarioUseCase = cambiarEstadoUsuarioUseCase;
        this.verificarEmailUseCase = verificarEmailUseCase;
        this.reenviarVerificacionUseCase = reenviarVerificacionUseCase;
        this.cambiarContraseñaUseCase = cambiarContraseñaUseCase;
        this.restablecerContrasenaUseCase = restablecerContrasenaUseCase;
        this.solicitarRestablecimientoUseCase = solicitarRestablecimientoUseCase;
        this.cambiarCorreoUseCase = cambiarCorreoUseCase;
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

    @PostMapping(value = "/auth/verify-email", consumes = "application/json")
    public ResponseEntity<Void> verificarEmail(@Valid @RequestBody VerificarEmailRequest request) {
        logger.info("ℹ️ POST /v1/usuarios/auth/verify-email - Verificando email de usuario");
        
        verificarEmailUseCase.execute(request.toCommand());

        logger.info("✅ Email de usuario verificado exitosamente");
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/auth/resend-verification", consumes = "application/json")
    public ResponseEntity<Void> reenviarVerificacion(@Valid @RequestBody ReenviarVerificacionRequest request) {
        logger.info("ℹ️ POST /v1/usuarios/auth/resend-verification - Reenviando verificación para email: {}", request.email());
        
        reenviarVerificacionUseCase.execute(request.toCommand());

        logger.info("✅ Email de verificación reenviado exitosamente");
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/auth/forgot-password", consumes = "application/json")
    public ResponseEntity<Void> solicitarRestablecimiento(@Valid @RequestBody SolicitarRestablecimientoRequest request) {
        logger.info("ℹ️ POST /v1/usuarios/auth/forgot-password - Solicitando restablecimiento para email: {}", request.email());
        
        solicitarRestablecimientoUseCase.execute(request.toCommand());

        logger.info("✅ Solicitud de restablecimiento procesada");
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "user/{id}/change-password", consumes = "application/json")
    public ResponseEntity<Void> cambiarContraseña(
            @PathVariable String id,
            @Valid @RequestBody CambiarContrasenaRequest request) {
        logger.info("ℹ️ POST /v1/usuarios/user/{}/change-password - Cambiando contraseña para usuario con ID: {}", id, id);

        cambiarContraseñaUseCase.execute(request.toCommand(id));

        logger.info("✅ Contraseña cambiada exitosamente para usuario con ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "user/{id}/change-email", consumes = "application/json")
    public ResponseEntity<Void> cambiarCorreo(
            @PathVariable String id,
            @Valid @RequestBody CambiarCorreoRequest request) {
        logger.info("ℹ️ POST /v1/usuarios/user/{}/change-email - Cambiando correo para usuario con ID: {}", id, id);
        cambiarCorreoUseCase.execute(request.toCommand(id));

        logger.info("✅ Email cambiado exitosamente para usuario con ID: {}", id);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/auth/reset-password", consumes = "application/json")
    public ResponseEntity<Void> restablecerContrasena(@Valid @RequestBody RestablecerContrasenaRequest request) {
        logger.info("ℹ️ POST /v1/usuarios/auth/reset-password - Restableciendo contraseña de usuario");

        restablecerContrasenaUseCase.execute(request.toCommand());

        logger.info("✅ Contraseña restablecida exitosamente");
        return ResponseEntity.ok().build();
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

    @PatchMapping(value = "/user/{id}/state", consumes = "application/json")
    public ResponseEntity<UsuarioResponse> cambiarEstadoUsuario(
            @PathVariable String id,
            @Valid @RequestBody CambiarEstadoUsuarioRequest request) {
        logger.info("ℹ️ PATCH /v1/usuarios/user/{id}/state - Cambiando el estado de usuario con ID: {}", id);

        UsuarioDTO usuarioDTO = cambiarEstadoUsuarioUseCase.execute(request.toCommand(id));

        UsuarioResponse response = UsuarioResponse.from(usuarioDTO);
        
        logger.info("✅ Estado de usuario cambiado exitosamente - ID: {}, Username: {}", 
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
    
}
