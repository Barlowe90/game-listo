package com.gamelisto.usuarios.infrastructure.in.api;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.usecases.*;
import com.gamelisto.usuarios.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios.infrastructure.in.api.dto.CambiarEstadoUsuarioRequest;
import com.gamelisto.usuarios.infrastructure.in.api.dto.CambiarRolUsuarioRequest;
import com.gamelisto.usuarios.infrastructure.in.api.dto.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
public class AdminController {

  private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
  private final CambiarEstadoUsuarioHandle cambiarEstadoUsuarioUseCase;
  private final CambiarRolUsuarioHandle cambiarRolUsuarioUseCase;
  private final BuscarUsuariosPorEstadoHandle buscarUsuariosPorEstadoUseCase;
  private final EliminarUsuarioHandle eliminarUsuarioUseCase;
  private final BuscarUsuariosPorNombreHandle buscarUsuariosPorNombreUseCase;
  private final ObtenerTodosLosUsuariosHandle obtenerTodosLosUsuariosUseCase;

  @PatchMapping(value = "/admin/{id}/rol", consumes = "application/json")
  public ResponseEntity<UsuarioResponse> cambiarRolUsuario(
      @PathVariable String id, @Valid @RequestBody CambiarRolUsuarioRequest request) {
    logger.info("PATCH /v1/usuarios/{}/rol - Cambiando el rol de usuario con ID: {}", id, id);

    UsuarioResult usuarioResult = cambiarRolUsuarioUseCase.execute(request.toCommand(id));

    UsuarioResponse response = UsuarioResponse.from(usuarioResult);

    logger.info(
        "Rol de usuario cambiado exitosamente - ID: {}, Username: {}, Nuevo Rol: {}",
        response.id(),
        response.username(),
        response.role());

    return ResponseEntity.ok(response);
  }

  @PatchMapping(value = "/admin/{id}/estado", consumes = "application/json")
  public ResponseEntity<UsuarioResponse> cambiarEstadoUsuario(
      @PathVariable String id, @Valid @RequestBody CambiarEstadoUsuarioRequest request) {
    logger.info("PATCH /v1/usuarios/{}/estado - Cambiando el estado de usuario con ID: {}", id, id);

    UsuarioResult usuarioResult = cambiarEstadoUsuarioUseCase.execute(request.toCommand(id));

    UsuarioResponse response = UsuarioResponse.from(usuarioResult);

    logger.info(
        "Estado de usuario cambiado exitosamente - ID: {}, Username: {}",
        response.id(),
        response.username());

    return ResponseEntity.ok(response);
  }

  @GetMapping(value = "/admin/users", produces = "application/json")
  public ResponseEntity<?> obtenerUsuarios(
      @RequestParam(required = false) String username,
      @RequestParam(required = false) EstadoUsuario estado) {

    logger.info("GET /v1/usuarios/users - username: {}, estado: {}", username, estado);

    if (username != null && !username.isBlank()) {
      UsuarioResult usuarioResult = buscarUsuariosPorNombreUseCase.execute(username);
      UsuarioResponse response = UsuarioResponse.from(usuarioResult);
      logger.info("Usuario encontrado - ID: {}, Username: {}", response.id(), response.username());
      return ResponseEntity.ok(response);
    }

    if (estado != null) {
      List<UsuarioResult> usuariosDTO = buscarUsuariosPorEstadoUseCase.execute(estado);
      List<UsuarioResponse> responses = usuariosDTO.stream().map(UsuarioResponse::from).toList();
      logger.info(
          "Usuarios por estado obtenidos - Estado: {}, Total: {}", estado, responses.size());
      return ResponseEntity.ok(responses);
    }

    List<UsuarioResult> usuariosDTO = obtenerTodosLosUsuariosUseCase.execute();
    List<UsuarioResponse> responses = usuariosDTO.stream().map(UsuarioResponse::from).toList();
    logger.info("Todos los usuarios obtenidos - Total: {}", responses.size());
    return ResponseEntity.ok(responses);
  }

  @DeleteMapping(value = "/admin/{id}")
  public ResponseEntity<Void> eliminarUsuario(@PathVariable String id) {
    logger.info("DELETE /v1/usuarios/{} - Eliminando usuario con ID: {}", id, id);

    eliminarUsuarioUseCase.execute(id);

    logger.info("Cuenta de usuario eliminada exitosamente - ID: {}", id);

    return ResponseEntity.noContent().build();
  }
}
