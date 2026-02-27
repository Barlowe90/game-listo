package com.gamelisto.usuarios.infrastructure.in.api;

import com.gamelisto.usuarios.application.dto.UsuarioDTO;
import com.gamelisto.usuarios.application.usecases.*;
import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios.infrastructure.in.api.dto.CambiarContrasenaRequest;
import com.gamelisto.usuarios.infrastructure.in.api.dto.CambiarCorreoRequest;
import com.gamelisto.usuarios.infrastructure.in.api.dto.CambiarEstadoUsuarioRequest;
import com.gamelisto.usuarios.infrastructure.in.api.dto.CambiarRolUsuarioRequest;
import com.gamelisto.usuarios.infrastructure.in.api.dto.EditarPerfilUsuarioRequest;
import com.gamelisto.usuarios.infrastructure.out.dto.UsuarioResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
public class UsuariosController {

  private static final Logger logger = LoggerFactory.getLogger(UsuariosController.class);
  private final EditarPerfilUsuarioUseCase editarPerfilUsuarioUseCase;
  private final ObtenerTodosLosUsuariosUseCase obtenerTodosLosUsuariosUseCase;
  private final ObtenerUsuarioPorId obtenerUsuarioPorId;
  private final CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;
  private final CambiarRolUsuarioUseCase cambiarRolUsuarioUseCase;
  private final CambiarContrasenaUseCase cambiarContrasenaUseCase;
  private final CambiarCorreoUseCase cambiarCorreoUseCase;
  private final BuscarUsuariosPorEstadoUseCase buscarUsuariosPorEstadoUseCase;
  private final EliminarUsuarioUseCase eliminarUsuarioUseCase;
  private final BuscarUsuariosPorNombreUseCase buscarUsuariosPorNombreUseCase;

  @PutMapping(value = "/{id}/password", consumes = "application/json")
  @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal")
  public ResponseEntity<Void> cambiarContrasena(
      @PathVariable String id, @Valid @RequestBody CambiarContrasenaRequest request) {
    logger.info(
        "PUT /v1/usuarios/{}/password - Cambiando contraseña para usuario con ID: {}", id, id);

    cambiarContrasenaUseCase.execute(request.toCommand(id));

    logger.info("Contraseña cambiada exitosamente para usuario con ID: {}", id);
    return ResponseEntity.ok().build();
  }

  @PutMapping(value = "/{id}/email", consumes = "application/json")
  @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal")
  public ResponseEntity<Void> cambiarCorreo(
      @PathVariable String id, @Valid @RequestBody CambiarCorreoRequest request) {
    logger.info("PUT /v1/usuarios/{}/email - Cambiando correo para usuario con ID: {}", id, id);
    cambiarCorreoUseCase.execute(request.toCommand(id));

    logger.info("Email cambiado exitosamente para usuario con ID: {}", id);
    return ResponseEntity.ok().build();
  }

  @PatchMapping(value = "/{id}", consumes = "application/json")
  @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal")
  public ResponseEntity<UsuarioResponse> editarPerfilUsuario(
      @PathVariable String id, @Valid @RequestBody EditarPerfilUsuarioRequest request) {
    logger.info("PATCH /v1/usuarios/{} - Editando perfil de usuario con ID: {}", id, id);

    UsuarioDTO usuarioDTO = editarPerfilUsuarioUseCase.execute(request.toCommand(id));

    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info(
        "Perfil de usuario editado exitosamente - ID: {}, Username: ",
        response.id(),
        response.username());

    return ResponseEntity.ok(response);
  }

  @PatchMapping(value = "/{id}/estado", consumes = "application/json")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UsuarioResponse> cambiarEstadoUsuario(
      @PathVariable String id, @Valid @RequestBody CambiarEstadoUsuarioRequest request) {
    logger.info("PATCH /v1/usuarios/{}/estado - Cambiando el estado de usuario con ID: {}", id, id);

    UsuarioDTO usuarioDTO = cambiarEstadoUsuarioUseCase.execute(request.toCommand(id));

    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info(
        "Estado de usuario cambiado exitosamente - ID: {}, Username: {}",
        response.id(),
        response.username());

    return ResponseEntity.ok(response);
  }

  @PatchMapping(value = "/{id}/rol", consumes = "application/json")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UsuarioResponse> cambiarRolUsuario(
      @PathVariable String id, @Valid @RequestBody CambiarRolUsuarioRequest request) {
    logger.info("PATCH /v1/usuarios/{}/rol - Cambiando el rol de usuario con ID: {}", id, id);

    UsuarioDTO usuarioDTO = cambiarRolUsuarioUseCase.execute(request.toCommand(id));

    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info(
        "Rol de usuario cambiado exitosamente - ID: {}, Username: {}, Nuevo Rol: {}",
        response.id(),
        response.username(),
        response.role());

    return ResponseEntity.ok(response);
  }

  @GetMapping(value = "/{id}", produces = "application/json")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<UsuarioResponse> obtenerUsuarioPorIdEndpoint(@PathVariable String id) {
    logger.info("GET /v1/usuarios/{} - Obteniendo usuario con ID: {}", id, id);

    UsuarioDTO usuarioDTO = obtenerUsuarioPorId.execute(id);

    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info(
        "Usuario obtenido exitosamente - ID: {}, Username: {}", response.id(), response.username());

    return ResponseEntity.ok(response);
  }

  @GetMapping(value = "/users", produces = "application/json")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<?> obtenerUsuarios(
      @RequestParam(required = false) String username,
      @RequestParam(required = false) EstadoUsuario estado,
      Authentication authentication) {

    logger.info("GET /v1/usuarios/users - username: {}, estado: {}", username, estado);

    if (username != null && !username.isBlank()) {
      UsuarioDTO usuarioDTO = buscarUsuariosPorNombreUseCase.execute(username);
      UsuarioResponse response = UsuarioResponse.from(usuarioDTO);
      logger.info("Usuario encontrado - ID: {}, Username: {}", response.id(), response.username());
      return ResponseEntity.ok(response);
    }

    boolean isAdmin =
        authentication.getAuthorities().stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

    if (!isAdmin) {
      logger.warn("Acceso denegado - Usuario sin rol ADMIN intentó listar usuarios");
      return ResponseEntity.status(403).build();
    }

    if (estado != null) {
      List<UsuarioDTO> usuariosDTO = buscarUsuariosPorEstadoUseCase.execute(estado);
      List<UsuarioResponse> responses = usuariosDTO.stream().map(UsuarioResponse::from).toList();
      logger.info(
          "Usuarios por estado obtenidos - Estado: {}, Total: {}", estado, responses.size());
      return ResponseEntity.ok(responses);
    }

    List<UsuarioDTO> usuariosDTO = obtenerTodosLosUsuariosUseCase.execute();
    List<UsuarioResponse> responses = usuariosDTO.stream().map(UsuarioResponse::from).toList();
    logger.info("Todos los usuarios obtenidos - Total: {}", responses.size());
    return ResponseEntity.ok(responses);
  }

  @DeleteMapping(value = "/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> eliminarUsuario(@PathVariable String id) {
    logger.info("DELETE /v1/usuarios/{} - Eliminando usuario con ID: {}", id, id);

    eliminarUsuarioUseCase.execute(id);

    logger.info("Cuenta de usuario eliminada exitosamente - ID: {}", id);

    return ResponseEntity.noContent().build();
  }
}
