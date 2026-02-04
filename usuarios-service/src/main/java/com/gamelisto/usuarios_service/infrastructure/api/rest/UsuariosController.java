package com.gamelisto.usuarios_service.infrastructure.api.rest;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.application.usecases.*;
import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios_service.infrastructure.api.dto.CambiarContrasenaRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.CambiarCorreoRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.CambiarEstadoUsuarioRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.CambiarRolUsuarioRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.EditarPerfilUsuarioRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.UsuarioResponse;
import com.gamelisto.usuarios_service.infrastructure.api.dto.VincularDiscordRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Tag(
    name = "Perfiles de Usuario",
    description = "Gestión de perfiles y datos de usuarios autenticados")
public class UsuariosController {

  private static final Logger logger = LoggerFactory.getLogger(UsuariosController.class);
  private final EditarPerfilUsuarioUseCase editarPerfilUsuarioUseCase;
  private final ObtenerTodosLosUsuariosUseCase obtenerTodosLosUsuariosUseCase;
  private final ObtenerUsuarioPorId obtenerUsuarioPorId;
  private final CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;
  private final CambiarRolUsuarioUseCase cambiarRolUsuarioUseCase;
  private final CambiarContrasenaUseCase cambiarContrasenaUseCase;
  private final CambiarCorreoUseCase cambiarCorreoUseCase;
  private final VincularDiscordUseCase vincularDiscordUseCase;
  private final DesvincularDiscordUseCase desvincularDiscordUseCase;
  private final BuscarUsuariosPorEstadoUseCase buscarUsuariosPorEstadoUseCase;
  private final BuscarUsuariosConNotificacionesActivadasUseCase
      buscarUsuariosConNotificacionesActivadasUseCase;
  private final EliminarUsuarioUseCase eliminarUsuarioUseCase;
  private final BuscarUsuariosPorNombreUseCase buscarUsuariosPorNombreUseCase;

  @Operation(
      summary = "Health check",
      description = "Verifica que el microservicio de usuarios esté funcionando correctamente",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente"),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN")
      })
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping(value = "/health")
  public void health() {
    logger.info("✅ Microservicio usuarios funcionando correctamente.");
  }

  @Operation(
      summary = "Cambiar contraseña (usuario autenticado)",
      description =
          "Permite al usuario cambiar su contraseña proporcionando la actual y la nueva. "
              + "Los usuarios pueden cambiar su propia contraseña, o un ADMIN puede cambiar la contraseña de cualquier usuario.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta"),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido"),
        @ApiResponse(
            responseCode = "403",
            description =
                "Acceso denegado - Solo el propietario o ADMIN pueden cambiar la contraseña"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
      })
  @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal")
  @PutMapping(value = "/{id}/password", consumes = "application/json")
  public ResponseEntity<Void> cambiarContrasena(
      @Parameter(description = "ID del usuario", required = true) @PathVariable String id,
      @Parameter(description = "Contraseña actual y nueva", required = true) @Valid @RequestBody
          CambiarContrasenaRequest request) {
    logger.info(
        "ℹ️ PUT /v1/usuarios/{}/password - Cambiando contraseña para usuario con ID: {}", id, id);

    cambiarContrasenaUseCase.execute(request.toCommand(id));

    logger.info("✅ Contraseña cambiada exitosamente para usuario con ID: {}", id);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Cambiar email del usuario",
      description =
          "Actualiza el email del usuario. Requiere nueva verificación. "
              + "Los usuarios pueden cambiar su propio email, o un ADMIN puede cambiar el email de cualquier usuario.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Email cambiado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Email ya registrado"),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido"),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Solo el propietario o ADMIN pueden cambiar el email"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
      })
  @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal")
  @PutMapping(value = "/{id}/email", consumes = "application/json")
  public ResponseEntity<Void> cambiarCorreo(
      @Parameter(description = "ID del usuario", required = true) @PathVariable String id,
      @Parameter(description = "Nuevo email", required = true) @Valid @RequestBody
          CambiarCorreoRequest request) {
    logger.info("ℹ️ PUT /v1/usuarios/{}/email - Cambiando correo para usuario con ID: {}", id, id);
    cambiarCorreoUseCase.execute(request.toCommand(id));

    logger.info("✅ Email cambiado exitosamente para usuario con ID: {}", id);
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Vincular cuenta de Discord",
      description =
          "Asocia una cuenta de Discord al perfil del usuario. "
              + "Los usuarios pueden vincular su propia cuenta, o un ADMIN puede vincular cualquier cuenta.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cuenta de Discord vinculada exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido"),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Solo el propietario o ADMIN pueden vincular Discord"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "400", description = "Discord ya vinculado a otro usuario")
      })
  @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal")
  @PutMapping(value = "/{id}/discord", consumes = "application/json")
  public ResponseEntity<UsuarioResponse> vincularDiscord(
      @Parameter(description = "ID del usuario", required = true) @PathVariable @NonNull String id,
      @Parameter(description = "Datos de Discord (discordUserId, discordUsername)", required = true)
          @Valid
          @RequestBody
          VincularDiscordRequest request) {
    logger.info(
        "ℹ️ PUT /v1/usuarios/{}/discord - Vinculando cuenta de Discord para usuario con ID: {}",
        id,
        id);

    UsuarioDTO usuarioDTO = vincularDiscordUseCase.execute(request.toCommand(id));

    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info(
        "✅ Cuenta de Discord vinculada exitosamente - ID: {}, Username: {}, Discord: {}",
        response.id(),
        response.username(),
        response.discordUsername());

    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Editar perfil de usuario",
      description =
          "Actualiza el perfil del usuario (username, avatar, idioma, notificaciones). "
              + "Los usuarios pueden editar su propio perfil, o un ADMIN puede editar cualquier perfil.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Perfil actualizado exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(responseCode = "400", description = "Username ya existe"),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido"),
        @ApiResponse(
            responseCode = "403",
            description = "Acceso denegado - Solo el propietario o ADMIN pueden editar el perfil"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
      })
  @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal")
  @PatchMapping(value = "/{id}", consumes = "application/json")
  public ResponseEntity<UsuarioResponse> editarPerfilUsuario(
      @Parameter(description = "ID del usuario", required = true) @PathVariable String id,
      @Parameter(description = "Datos del perfil a actualizar", required = true) @Valid @RequestBody
          EditarPerfilUsuarioRequest request) {
    logger.info("ℹ️ PATCH /v1/usuarios/{} - Editando perfil de usuario con ID: {}", id, id);

    UsuarioDTO usuarioDTO = editarPerfilUsuarioUseCase.execute(request.toCommand(id));

    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info(
        "✅ Perfil de usuario editado exitosamente - ID: {}, Username: {}",
        response.id(),
        response.username());

    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Cambiar estado de usuario (Admin)",
      description =
          "Actualiza el estado del usuario: PENDIENTE_DE_VERIFICACION, ACTIVO, SUSPENDIDO, ELIMINADO. "
              + "Requiere rol ADMIN.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estado cambiado exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
      })
  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping(value = "/{id}/estado", consumes = "application/json")
  public ResponseEntity<UsuarioResponse> cambiarEstadoUsuario(
      @Parameter(description = "ID del usuario", required = true) @PathVariable String id,
      @Parameter(description = "Nuevo estado", required = true) @Valid @RequestBody
          CambiarEstadoUsuarioRequest request) {
    logger.info(
        "ℹ️ PATCH /v1/usuarios/{}/estado - Cambiando el estado de usuario con ID: {}", id, id);

    UsuarioDTO usuarioDTO = cambiarEstadoUsuarioUseCase.execute(request.toCommand(id));

    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info(
        "✅ Estado de usuario cambiado exitosamente - ID: {}, Username: {}",
        response.id(),
        response.username());

    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Cambiar rol de usuario (Admin)",
      description = "Actualiza el rol del usuario: USER, ADMIN. Requiere rol ADMIN.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Rol cambiado exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN")
      })
  @PreAuthorize("hasRole('ADMIN')")
  @PatchMapping(value = "/{id}/rol", consumes = "application/json")
  public ResponseEntity<UsuarioResponse> cambiarRolUsuario(
      @Parameter(description = "ID del usuario", required = true) @PathVariable String id,
      @Parameter(description = "Nuevo rol", required = true) @Valid @RequestBody
          CambiarRolUsuarioRequest request) {
    logger.info("ℹ️ PATCH /v1/usuarios/{}/rol - Cambiando el rol de usuario con ID: {}", id, id);

    UsuarioDTO usuarioDTO = cambiarRolUsuarioUseCase.execute(request.toCommand(id));

    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info(
        "✅ Rol de usuario cambiado exitosamente - ID: {}, Username: {}, Nuevo Rol: {}",
        response.id(),
        response.username(),
        response.role());

    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Obtener usuario por ID",
      description =
          "Recupera los datos completos de un usuario mediante su identificador único. Requiere rol ADMIN.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
      })
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping(value = "/{id}", produces = "application/json")
  public ResponseEntity<UsuarioResponse> obtenerUsuarioPorIdEndpoint(
      @Parameter(description = "ID del usuario", required = true) @PathVariable String id) {
    logger.info("ℹ️ GET /v1/usuarios/{} - Obteniendo usuario con ID: {}", id, id);

    UsuarioDTO usuarioDTO = obtenerUsuarioPorId.execute(id);

    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info(
        "✅ Usuario obtenido exitosamente - ID: {}, Username: {}",
        response.id(),
        response.username());

    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Listar todos los usuarios",
      description =
          "Recupera la lista completa de usuarios registrados en el sistema. Requiere rol ADMIN.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN")
      })
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping(value = "/users", produces = "application/json")
  public ResponseEntity<List<UsuarioResponse>> obtenerTodosLosUsuarios() {
    logger.info("ℹ️ GET /v1/usuarios/users - Obteniendo lista de todos los usuarios");

    List<UsuarioDTO> usuariosDTO = obtenerTodosLosUsuariosUseCase.execute();

    List<UsuarioResponse> responses = usuariosDTO.stream().map(UsuarioResponse::from).toList();

    logger.info("✅ Lista de usuarios obtenida exitosamente - Total usuarios: {}", responses.size());

    return ResponseEntity.ok(responses);
  }

  @Operation(
      summary = "Buscar usuario por username",
      description = "Busca un usuario específico por su nombre de usuario. Requiere autenticación.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario encontrado",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
      })
  @PreAuthorize("isAuthenticated()")
  @GetMapping(value = "/users/search", produces = "application/json")
  public ResponseEntity<UsuarioResponse> buscarUsuarioPorUsername(
      @Parameter(description = "Username del usuario", required = true) @RequestParam("username")
          String username) {
    logger.info(
        "ℹ️ GET /v1/usuarios/users/search?username={} - Buscando usuario con username: {}",
        username,
        username);

    UsuarioDTO usuarioDTO = buscarUsuariosPorNombreUseCase.execute(username);
    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info("✅ Usuario encontrado - ID: {}, Username: {}", response.id(), response.username());
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Buscar usuarios por estado",
      description =
          "Filtra usuarios según su estado: PENDIENTE_DE_VERIFICACION, ACTIVO, SUSPENDIDO, ELIMINADO. "
              + "Requiere rol ADMIN.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios filtrada por estado"),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN")
      })
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping(value = "/users/estado", produces = "application/json")
  public ResponseEntity<List<UsuarioResponse>> obtenerUsuariosPorEstado(
      @Parameter(description = "Estado del usuario", required = true) @RequestParam("estado")
          EstadoUsuario estadoUsuario) {
    logger.info(
        "ℹ️ GET /v1/usuarios/users/estado?estado={} - Obteniendo lista de usuarios por estado: {}",
        estadoUsuario,
        estadoUsuario);

    List<UsuarioDTO> usuariosDTO = buscarUsuariosPorEstadoUseCase.execute(estadoUsuario);

    List<UsuarioResponse> responses = usuariosDTO.stream().map(UsuarioResponse::from).toList();

    logger.info(
        "✅ Lista de usuarios por estado obtenida exitosamente - Estado: {}, Total usuarios: {}",
        estadoUsuario,
        responses.size());

    return ResponseEntity.ok(responses);
  }

  @Operation(
      summary = "Listar usuarios con notificaciones activadas",
      description =
          "Recupera usuarios activos que tienen las notificaciones habilitadas. Requiere rol ADMIN.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de usuarios con notificaciones activadas"),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN")
      })
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping(value = "/users/notifications-enabled", produces = "application/json")
  public ResponseEntity<List<UsuarioResponse>> obtenerUsuariosConNotificacionesActivadas() {
    logger.info(
        "ℹ️ GET /v1/usuarios/users/notifications-enabled - Obteniendo lista de usuarios activos con notificaciones activadas");

    List<UsuarioDTO> usuariosDTO = buscarUsuariosConNotificacionesActivadasUseCase.execute();

    List<UsuarioResponse> responses = usuariosDTO.stream().map(UsuarioResponse::from).toList();

    logger.info(
        "✅ Lista de usuarios con notificaciones activadas obtenida exitosamente - Total: {}",
        responses.size());

    return ResponseEntity.ok(responses);
  }

  @Operation(
      summary = "Desvincular cuenta de Discord",
      description =
          "Elimina la asociación entre el usuario y su cuenta de Discord. "
              + "Los usuarios pueden desvincular su propia cuenta, o un ADMIN puede desvincular cualquier cuenta.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Cuenta de Discord desvinculada exitosamente",
            content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido"),
        @ApiResponse(
            responseCode = "403",
            description =
                "Acceso denegado - Solo el propietario o ADMIN pueden desvincular Discord"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
      })
  @DeleteMapping(value = "/{id}/discord")
  @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal")
  public ResponseEntity<UsuarioResponse> desvincularDiscord(
      @Parameter(description = "ID del usuario", required = true) @PathVariable String id) {
    logger.info(
        "ℹ️ DELETE /v1/usuarios/{}/discord - Desvinculando cuenta de Discord para usuario con ID: {}",
        id,
        id);

    UsuarioDTO usuarioDTO = desvincularDiscordUseCase.execute(id);

    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info(
        "✅ Cuenta de Discord desvinculada exitosamente - ID: {}, Username: {}",
        response.id(),
        response.username());

    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Eliminar usuario",
      description =
          "Elimina permanentemente un usuario del sistema (hard delete). Requiere rol ADMIN.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token ausente o inválido"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado - Requiere rol ADMIN"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
      })
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<Void> eliminarUsuario(
      @Parameter(description = "ID del usuario", required = true) @PathVariable String id) {
    logger.info("ℹ️ DELETE /v1/usuarios/{} - Eliminando usuario con ID: {}", id, id);

    eliminarUsuarioUseCase.execute(id);

    logger.info("✅ Cuenta de usuario eliminada exitosamente - ID: {}", id);

    return ResponseEntity.noContent().build();
  }
}
