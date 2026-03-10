package com.gamelisto.usuarios.infrastructure.in.api;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.usecases.*;
import com.gamelisto.usuarios.infrastructure.in.api.dto.CambiarContrasenaRequest;
import com.gamelisto.usuarios.infrastructure.in.api.dto.CambiarCorreoRequest;
import com.gamelisto.usuarios.infrastructure.in.api.dto.EditarPerfilUsuarioRequest;
import com.gamelisto.usuarios.infrastructure.in.api.dto.UsuarioResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
public class UsuariosController {

  private static final Logger logger = LoggerFactory.getLogger(UsuariosController.class);
  private final EditarPerfilUsuarioHandle editarPerfilUsuarioUseCase;
  private final ObtenerUsuarioPorIdHandle obtenerUsuarioPorIdUseCase;
  private final CambiarContrasenaHandle cambiarContrasenaUseCase;
  private final CambiarCorreoHandle cambiarCorreoUseCase;

  @PutMapping(value = "/password", consumes = "application/json")
  public ResponseEntity<Void> cambiarContrasena(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody CambiarContrasenaRequest request) {

    logger.info("PUT /v1/usuarios/password - Cambiando contraseña para usuario con ID: {}", userId);

    cambiarContrasenaUseCase.execute(request.toCommand(userId));

    logger.info("Contraseña cambiada exitosamente para usuario con ID: {}", userId);
    return ResponseEntity.ok().build();
  }

  @PutMapping(value = "/email", consumes = "application/json")
  public ResponseEntity<Void> cambiarCorreo(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody CambiarCorreoRequest request) {

    logger.info("PUT /v1/usuarios/email - Cambiando correo para usuario con ID: {}", userId);
    cambiarCorreoUseCase.execute(request.toCommand(userId));

    logger.info("Email cambiado exitosamente para usuario con ID: {}", userId);
    return ResponseEntity.ok().build();
  }

  @PatchMapping(consumes = "application/json")
  public ResponseEntity<UsuarioResponse> editarPerfilUsuario(
      @AuthenticationPrincipal UUID userId,
      @Valid @RequestBody EditarPerfilUsuarioRequest request) {
    logger.info("PATCH /v1/usuarios - Editando perfil de usuario con ID: {}", userId);

    UsuarioResult usuarioResult = editarPerfilUsuarioUseCase.execute(request.toCommand(userId));

    UsuarioResponse response = UsuarioResponse.from(usuarioResult);

    logger.info(
        "Perfil de usuario editado exitosamente - ID: {}, Username: {}",
        response.id(),
        response.username());

    return ResponseEntity.ok(response);
  }

  @GetMapping(value = "/{id}", produces = "application/json")
  public ResponseEntity<UsuarioResponse> obtenerUsuarioPorId(@PathVariable String id) {
    logger.info("GET /v1/usuarios/{} - Obteniendo usuario con ID: {}", id, id);

    UsuarioResult usuarioResult = obtenerUsuarioPorIdUseCase.execute(id);

    UsuarioResponse response = UsuarioResponse.from(usuarioResult);

    logger.info(
        "Usuario obtenido exitosamente - ID: {}, Username: {}", response.id(), response.username());

    return ResponseEntity.ok(response);
  }
}
