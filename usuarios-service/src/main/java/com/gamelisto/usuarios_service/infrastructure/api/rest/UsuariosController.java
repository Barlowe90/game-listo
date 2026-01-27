package com.gamelisto.usuarios_service.infrastructure.api.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.application.usecases.BuscarUsuariosConNotificacionesActivadasUseCase;
import com.gamelisto.usuarios_service.application.usecases.BuscarUsuariosPorEstadoUseCase;
import com.gamelisto.usuarios_service.application.usecases.CambiarContrasenaUseCase;
import com.gamelisto.usuarios_service.application.usecases.CambiarCorreoUseCase;
import com.gamelisto.usuarios_service.application.usecases.CambiarEstadoUsuarioUseCase;
import com.gamelisto.usuarios_service.application.usecases.CrearUsuarioUseCase;
import com.gamelisto.usuarios_service.application.usecases.DesvincularDiscordUseCase;
import com.gamelisto.usuarios_service.application.usecases.EditarPerfilUsuarioUseCase;
import com.gamelisto.usuarios_service.application.usecases.EliminarUsuarioUseCase;
import com.gamelisto.usuarios_service.application.usecases.ObtenerTodosLosUsuariosUseCase;
import com.gamelisto.usuarios_service.application.usecases.ObtenerUsuarioPorId;
import com.gamelisto.usuarios_service.application.usecases.ReenviarVerificacionUseCase;
import com.gamelisto.usuarios_service.application.usecases.RestablecerContrasenaUseCase;
import com.gamelisto.usuarios_service.application.usecases.SolicitarRestablecimientoUseCase;
import com.gamelisto.usuarios_service.application.usecases.VerificarEmailUseCase;
import com.gamelisto.usuarios_service.application.usecases.VincularDiscordUseCase;
import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;
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
import com.gamelisto.usuarios_service.infrastructure.api.dto.VincularDiscordRequest;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/v1/usuarios")
@Tag(name = "Usuarios", description = "API de gestión de usuarios - Perfiles, autenticación y configuración")
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
        private final VincularDiscordUseCase vincularDiscordUseCase;
        private final DesvincularDiscordUseCase desvincularDiscordUseCase;
        private final BuscarUsuariosPorEstadoUseCase buscarUsuariosPorEstadoUseCase;
        private final BuscarUsuariosConNotificacionesActivadasUseCase buscarUsuariosConNotificacionesActivadasUseCase;
        private final EliminarUsuarioUseCase eliminarUsuarioUseCase;

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
                        CambiarCorreoUseCase cambiarCorreoUseCase,
                        VincularDiscordUseCase vincularDiscordUseCase,
                        DesvincularDiscordUseCase desvincularDiscordUseCase,
                        BuscarUsuariosPorEstadoUseCase buscarUsuariosPorEstadoUseCase,
                        BuscarUsuariosConNotificacionesActivadasUseCase buscarUsuariosConNotificacionesActivadasUseCase,
                        EliminarUsuarioUseCase eliminarUsuarioUseCase) {
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
                this.vincularDiscordUseCase = vincularDiscordUseCase;
                this.desvincularDiscordUseCase = desvincularDiscordUseCase;
                this.buscarUsuariosPorEstadoUseCase = buscarUsuariosPorEstadoUseCase;
                this.buscarUsuariosConNotificacionesActivadasUseCase = buscarUsuariosConNotificacionesActivadasUseCase;
                this.eliminarUsuarioUseCase = eliminarUsuarioUseCase;
        }

        @Operation(summary = "Health check", description = "Verifica que el microservicio de usuarios esté funcionando correctamente")
        @ApiResponse(responseCode = "200", description = "Servicio funcionando correctamente")
        @GetMapping(value = "/health")
        public void health() {
                logger.info("✅ Microservicio usuarios funcionando correctamente.");
        }

        @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario en el sistema. El email requiere verificación posterior. Estado inicial: PENDIENTE_DE_VERIFICACION.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente", content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Datos inválidos (username/email ya existe, formato inválido)"),
                        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
        @PostMapping(value = "/auth/register", consumes = "application/json")
        public ResponseEntity<UsuarioResponse> crearUsuario(
                        @Parameter(description = "Datos del nuevo usuario", required = true) @Valid @RequestBody CrearUsuarioRequest request) {
                logger.info("ℹ️ POST /v1/usuarios/auth/register - Creando usuario con username: {}",
                                request.username());

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

        @Operation(summary = "Verificar email de usuario", description = "Valida el token de verificación enviado al email. El token expira en 24 horas. Cambia el estado a ACTIVO.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Email verificado exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Token inválido o expirado"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        @PostMapping(value = "/auth/verify-email", consumes = "application/json")
        public ResponseEntity<Void> verificarEmail(
                        @Parameter(description = "Token de verificación del email", required = true) @Valid @RequestBody VerificarEmailRequest request) {
                logger.info("ℹ️ POST /v1/usuarios/auth/verify-email - Verificando email de usuario");

                verificarEmailUseCase.execute(request.toCommand());

                logger.info("✅ Email de usuario verificado exitosamente");
                return ResponseEntity.ok().build();
        }

        @Operation(summary = "Reenviar email de verificación", description = "Genera un nuevo token de verificación y lo envía al email del usuario")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Email de verificación reenviado"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                        @ApiResponse(responseCode = "400", description = "Usuario ya verificado")
        })
        @PostMapping(value = "/auth/resend-verification", consumes = "application/json")
        public ResponseEntity<Void> reenviarVerificacion(
                        @Parameter(description = "Email del usuario", required = true) @Valid @RequestBody ReenviarVerificacionRequest request) {
                logger.info("ℹ️ POST /v1/usuarios/auth/resend-verification - Reenviando verificación para email: {}",
                                request.email());

                reenviarVerificacionUseCase.execute(request.toCommand());

                logger.info("✅ Email de verificación reenviado exitosamente");
                return ResponseEntity.ok().build();
        }

        @Operation(summary = "Solicitar restablecimiento de contraseña", description = "Genera un token de restablecimiento y lo envía al email del usuario. El token expira en 24 horas.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Email de restablecimiento enviado"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        @PostMapping(value = "/auth/forgot-password", consumes = "application/json")
        public ResponseEntity<Void> solicitarRestablecimiento(
                        @Parameter(description = "Email del usuario", required = true) @Valid @RequestBody SolicitarRestablecimientoRequest request) {
                logger.info("ℹ️ POST /v1/usuarios/auth/forgot-password - Solicitando restablecimiento para email: {}",
                                request.email());

                solicitarRestablecimientoUseCase.execute(request.toCommand());

                logger.info("✅ Solicitud de restablecimiento procesada");
                return ResponseEntity.ok().build();
        }

        @Operation(summary = "Cambiar contraseña (usuario autenticado)", description = "Permite al usuario cambiar su contraseña proporcionando la actual y la nueva")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Contraseña cambiada exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Contraseña actual incorrecta"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        @PostMapping(value = "user/{id}/change-password", consumes = "application/json")
        public ResponseEntity<Void> cambiarContraseña(
                        @Parameter(description = "ID del usuario", required = true) @PathVariable String id,
                        @Parameter(description = "Contraseña actual y nueva", required = true) @Valid @RequestBody CambiarContrasenaRequest request) {
                logger.info("ℹ️ POST /v1/usuarios/user/{}/change-password - Cambiando contraseña para usuario con ID: {}",
                                id,
                                id);

                cambiarContraseñaUseCase.execute(request.toCommand(id));

                logger.info("✅ Contraseña cambiada exitosamente para usuario con ID: {}", id);
                return ResponseEntity.ok().build();
        }

        @Operation(summary = "Cambiar email del usuario", description = "Actualiza el email del usuario. Requiere nueva verificación.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Email cambiado exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Email ya registrado"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        @PostMapping(value = "user/{id}/change-email", consumes = "application/json")
        public ResponseEntity<Void> cambiarCorreo(
                        @Parameter(description = "ID del usuario", required = true) @PathVariable String id,
                        @Parameter(description = "Nuevo email", required = true) @Valid @RequestBody CambiarCorreoRequest request) {
                logger.info("ℹ️ POST /v1/usuarios/user/{}/change-email - Cambiando correo para usuario con ID: {}", id,
                                id);
                cambiarCorreoUseCase.execute(request.toCommand(id));

                logger.info("✅ Email cambiado exitosamente para usuario con ID: {}", id);
                return ResponseEntity.ok().build();
        }

        @Operation(summary = "Restablecer contraseña con token", description = "Valida el token de restablecimiento y establece la nueva contraseña")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Contraseña restablecida exitosamente"),
                        @ApiResponse(responseCode = "400", description = "Token inválido o expirado"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        @PostMapping(value = "/auth/reset-password", consumes = "application/json")
        public ResponseEntity<Void> restablecerContrasena(
                        @Parameter(description = "Token y nueva contraseña", required = true) @Valid @RequestBody RestablecerContrasenaRequest request) {
                logger.info("ℹ️ POST /v1/usuarios/auth/reset-password - Restableciendo contraseña de usuario");

                restablecerContrasenaUseCase.execute(request.toCommand());

                logger.info("✅ Contraseña restablecida exitosamente");
                return ResponseEntity.ok().build();
        }

        @Operation(summary = "Vincular cuenta de Discord", description = "Asocia una cuenta de Discord al perfil del usuario")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cuenta de Discord vinculada exitosamente", content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
                        @ApiResponse(responseCode = "400", description = "Discord ya vinculado a otro usuario")
        })
        @PostMapping(value = "/user/{id}/discord", consumes = "application/json")
        public ResponseEntity<UsuarioResponse> vincularDiscord(
                        @Parameter(description = "ID del usuario", required = true) @PathVariable @NonNull String id,
                        @Parameter(description = "Datos de Discord (discordUserId, discordUsername)", required = true) @Valid @RequestBody VincularDiscordRequest request) {
                logger.info("ℹ️ POST /v1/usuarios/user/{}/discord - Vinculando cuenta de Discord para usuario con ID: {}",
                                id, id);

                UsuarioDTO usuarioDTO = vincularDiscordUseCase.execute(request.toCommand(id));

                UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

                logger.info("✅ Cuenta de Discord vinculada exitosamente - ID: {}, Username: {}, Discord: {}",
                                response.id(), response.username(), response.discordUsername());

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Editar perfil de usuario", description = "Actualiza el perfil del usuario (username, avatar, idioma, notificaciones)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente", content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Username ya existe"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        @PatchMapping(value = "/user/{id}", consumes = "application/json")
        public ResponseEntity<UsuarioResponse> editarPerfilUsuario(
                        @Parameter(description = "ID del usuario", required = true) @PathVariable String id,
                        @Parameter(description = "Datos del perfil a actualizar", required = true) @Valid @RequestBody EditarPerfilUsuarioRequest request) {
                logger.info("ℹ️ PATCH /v1/usuarios/user/{id} - Editando perfil de usuario con ID: {}", id);

                UsuarioDTO usuarioDTO = editarPerfilUsuarioUseCase.execute(request.toCommand(id));

                UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

                logger.info("✅ Perfil de usuario editado exitosamente - ID: {}, Username: {}",
                                response.id(), response.username());

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Cambiar estado de usuario (Admin)", description = "Actualiza el estado del usuario: PENDIENTE_DE_VERIFICACION, ACTIVO, SUSPENDIDO, ELIMINADO")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Estado cambiado exitosamente", content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        @PatchMapping(value = "/user/{id}/state", consumes = "application/json")
        public ResponseEntity<UsuarioResponse> cambiarEstadoUsuario(
                        @Parameter(description = "ID del usuario", required = true) @PathVariable String id,
                        @Parameter(description = "Nuevo estado", required = true) @Valid @RequestBody CambiarEstadoUsuarioRequest request) {
                logger.info("ℹ️ PATCH /v1/usuarios/user/{id}/state - Cambiando el estado de usuario con ID: {}", id);

                UsuarioDTO usuarioDTO = cambiarEstadoUsuarioUseCase.execute(request.toCommand(id));

                UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

                logger.info("✅ Estado de usuario cambiado exitosamente - ID: {}, Username: {}",
                                response.id(), response.username());

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Obtener usuario por ID", description = "Recupera los datos completos de un usuario mediante su identificador único")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        @GetMapping(value = "/user/{id}", produces = "application/json")
        public ResponseEntity<UsuarioResponse> obtenerUsuarioPorIdEndpoint(
                        @Parameter(description = "ID del usuario", required = true) @PathVariable String id) {
                logger.info("ℹ️ GET /v1/usuarios/user/{} - Obteniendo usuario con ID: {}", id, id);

                UsuarioDTO usuarioDTO = obtenerUsuarioPorId.execute(id);

                UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

                logger.info("✅ Usuario obtenido exitosamente - ID: {}, Username: {}",
                                response.id(), response.username());

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Listar todos los usuarios", description = "Recupera la lista completa de usuarios registrados en el sistema")
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente")
        @GetMapping(value = "/users", produces = "application/json")
        public ResponseEntity<List<UsuarioResponse>> obtenerUsuarios() {
                logger.info("ℹ️ GET /v1/usuarios/users - Obteniendo lista de usuarios");

                List<UsuarioDTO> usuariosDTO = obtenerTodosLosUsuariosUseCase.execute();

                List<UsuarioResponse> responses = usuariosDTO.stream()
                                .map(UsuarioResponse::from)
                                .toList();

                logger.info("✅ Lista de usuarios obtenida exitosamente - Total usuarios: {}", responses.size());

                return ResponseEntity.ok(responses);
        }

        @Operation(summary = "Buscar usuarios por estado", description = "Filtra usuarios según su estado: PENDIENTE_DE_VERIFICACION, ACTIVO, SUSPENDIDO, ELIMINADO")
        @ApiResponse(responseCode = "200", description = "Lista de usuarios filtrada por estado")
        @GetMapping(value = "/users", produces = "application/json", params = "estado")
        public ResponseEntity<List<UsuarioResponse>> obtenerUsuariosPorEstado(
                        @Parameter(description = "Estado del usuario", required = true) @RequestParam("estado") EstadoUsuario estadoUsuario) {
                logger.info("ℹ️ GET /v1/usuarios/users?estado={} - Obteniendo lista de usuarios por estado: {}",
                                estadoUsuario,
                                estadoUsuario);

                List<UsuarioDTO> usuariosDTO = buscarUsuariosPorEstadoUseCase.execute(estadoUsuario);

                List<UsuarioResponse> responses = usuariosDTO.stream()
                                .map(UsuarioResponse::from)
                                .toList();

                logger.info("✅ Lista de usuarios por estado obtenida exitosamente - Estado: {}, Total usuarios: {}",
                                estadoUsuario, responses.size());

                return ResponseEntity.ok(responses);
        }

        @Operation(summary = "Listar usuarios con notificaciones activadas", description = "Recupera usuarios activos que tienen las notificaciones habilitadas")
        @ApiResponse(responseCode = "200", description = "Lista de usuarios con notificaciones activadas")
        @GetMapping(value = "/users/notifications-enabled", produces = "application/json")
        public ResponseEntity<List<UsuarioResponse>> obtenerUsuariosConNotificacionesActivadas() {
                logger.info(
                                "ℹ️ GET /v1/usuarios/users/notifications-enabled - Obteniendo lista de usuarios activos con notificaciones activadas");

                List<UsuarioDTO> usuariosDTO = buscarUsuariosConNotificacionesActivadasUseCase.execute();

                List<UsuarioResponse> responses = usuariosDTO.stream()
                                .map(UsuarioResponse::from)
                                .toList();

                logger.info("✅ Lista de usuarios con notificaciones activadas obtenida exitosamente - Total: {}",
                                responses.size());

                return ResponseEntity.ok(responses);
        }

        @Operation(summary = "Desvincular cuenta de Discord", description = "Elimina la asociación entre el usuario y su cuenta de Discord")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Cuenta de Discord desvinculada exitosamente", content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        @DeleteMapping(value = "/user/{id}/discord")
        public ResponseEntity<UsuarioResponse> desvincularDiscord(
                        @Parameter(description = "ID del usuario", required = true) @PathVariable String id) {
                logger.info(
                                "ℹ️ DELETE /v1/usuarios/user/{}/discord - Desvinculando cuenta de Discord para usuario con ID: {}",
                                id, id);

                UsuarioDTO usuarioDTO = desvincularDiscordUseCase.execute(id);

                UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

                logger.info("✅ Cuenta de Discord desvinculada exitosamente - ID: {}, Username: {}",
                                response.id(), response.username());

                return ResponseEntity.ok(response);
        }

        @Operation(summary = "Eliminar usuario", description = "Elimina permanentemente un usuario del sistema (hard delete)")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
                        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
        })
        @DeleteMapping(value = "/user/{id}")
        public ResponseEntity<Void> eliminarUsuario(
                        @Parameter(description = "ID del usuario", required = true) @PathVariable String id) {
                logger.info("ℹ️ DELETE /v1/usuarios/user/{} - Eliminando usuario con ID: {}", id, id);

                eliminarUsuarioUseCase.execute(id);

                logger.info("✅ Cuenta de usuario eliminada exitosamente - ID: {}", id);

                return ResponseEntity.noContent().build();
        }

}
