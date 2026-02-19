package com.gamelisto.usuarios.infrastructure.in.api.rest;

import com.gamelisto.usuarios.application.dto.*;
import com.gamelisto.usuarios.application.usecases.*;
import com.gamelisto.usuarios.infrastructure.in.api.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/usuarios/auth")
@RequiredArgsConstructor
@Tag(
    name = "Autenticación",
    description = "Registro, login, tokens, verificación de email y recuperación de cuenta")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  private final LoginUseCase loginUseCase;
  private final RefreshTokenUseCase refreshTokenUseCase;
  private final LogoutUseCase logoutUseCase;
  private final ObtenerPerfilAutenticadoUseCase obtenerPerfilAutenticadoUseCase;
  private final CrearUsuarioUseCase crearUsuarioUseCase;
  private final VerificarEmailUseCase verificarEmailUseCase;
  private final ReenviarVerificacionUseCase reenviarVerificacionUseCase;
  private final SolicitarRestablecimientoUseCase solicitarRestablecimientoUseCase;
  private final RestablecerContrasenaUseCase restablecerContrasenaUseCase;

  // TODO mover a usuarios (Create CRUD)
  @Operation(
      summary = "Registrar nuevo usuario",
      description =
          "Crea una nueva cuenta de usuario en estado PENDIENTE_DE_VERIFICACION. "
              + "Envía un email con token de verificación (válido 24h).")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "201",
            description = "Usuario creado exitosamente",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos o email/username ya registrados")
      })
  @PostMapping("/register")
  public ResponseEntity<UsuarioResponse> registrar(
      @Parameter(description = "Datos del nuevo usuario", required = true) @Valid @RequestBody
          CrearUsuarioRequest request) {

    logger.info("Request de registro para email: {}", request.email());

    CrearUsuarioCommand command = request.toCommand();
    UsuarioDTO usuarioDTO = crearUsuarioUseCase.execute(command);
    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info("Usuario registrado exitosamente: {}", usuarioDTO.username());
    // que pueda entrar a su perfil, con el aviso de que tiene que verificar email.
    // permisos para cambiar correo por si se ha equivocado
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(
      summary = "Verificar email",
      description =
          "Activa la cuenta del usuario mediante el token enviado por email. "
              + "Cambia el estado de PENDIENTE_DE_VERIFICACION a ACTIVO.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Email verificado exitosamente"),
        @ApiResponse(
            responseCode = "400",
            description = "Token inválido, expirado o usuario ya verificado")
      })
  @PostMapping("/verify-email")
  public ResponseEntity<Void> verificarEmail(
      @Parameter(description = "Token de verificación", required = true) @Valid @RequestBody
          VerificarEmailRequest request) {

    logger.info("Request de verificación de email con token: {}", request.token());

    VerificarEmailCommand command = new VerificarEmailCommand(request.token());
    verificarEmailUseCase.execute(command);

    logger.info("Email verificado exitosamente");
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Reenviar email de verificación",
      description =
          "Genera y envía un nuevo token de verificación al email del usuario. "
              + "Solo funciona si el usuario está en estado PENDIENTE_DE_VERIFICACION.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Email de verificación reenviado"),
        @ApiResponse(
            responseCode = "400",
            description = "Email no registrado o usuario ya verificado")
      })
  @PostMapping("/resend-verification")
  public ResponseEntity<Void> reenviarVerificacion(
      @Parameter(description = "Email del usuario", required = true) @Valid @RequestBody
          ReenviarVerificacionRequest request) {

    logger.info("Request de reenvío de verificación para email: {}", request.email());

    reenviarVerificacionUseCase.execute(request.toCommand());

    logger.info("Email de verificación reenviado");
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Solicitar restablecimiento de contraseña",
      description =
          "Genera y envía un token de restablecimiento al email (válido 1h). "
              + "Si el email no existe, retorna 200 OK por seguridad (no revela si el email está registrado).")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Si el email existe, se envió el token de restablecimiento")
      })
  @PostMapping("/forgot-password")
  public ResponseEntity<Void> solicitarRestablecimiento(
      @Parameter(description = "Email del usuario", required = true) @Valid @RequestBody
          SolicitarRestablecimientoRequest request) {

    logger.info("Request de restablecimiento de contraseña para email: {}", request.email());

    solicitarRestablecimientoUseCase.execute(request.toCommand());

    logger.info("Proceso de restablecimiento iniciado (si el email existe)");
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Restablecer contraseña",
      description =
          "Cambia la contraseña del usuario usando el token de restablecimiento. "
              + "El token se invalida después de usarse.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Contraseña restablecida exitosamente"),
        @ApiResponse(responseCode = "400", description = "Token inválido o expirado")
      })
  @PostMapping("/reset-password")
  public ResponseEntity<Void> restablecerContrasena(
      @Parameter(description = "Token y nueva contraseña", required = true) @Valid @RequestBody
          RestablecerContrasenaRequest request) {

    logger.info("Request de restablecimiento de contraseña con token");

    RestablecerContrasenaCommand command = request.toCommand();
    restablecerContrasenaUseCase.execute(command);

    logger.info("Contraseña restablecida exitosamente");
    return ResponseEntity.ok().build();
  }

  @Operation(
      summary = "Login de usuario",
      description =
          "Autentica al usuario con email y contraseña. Retorna access token JWT (15 min) y refresh token UUID (7 días).")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login exitoso",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales inválidas o usuario no activo"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
      })
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(
      @Parameter(description = "Credenciales de login", required = true) @Valid @RequestBody
          LoginRequest request) {

    logger.info("Request de login para email: {}", request.email());

    LoginCommand command = new LoginCommand(request.email(), request.password());
    AuthResponseDTO authResponseDTO = loginUseCase.execute(command);

    AuthResponse response = AuthResponse.from(authResponseDTO);

    logger.info("Login exitoso para usuario: {}", authResponseDTO.usuario().username());
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Renovar access token",
      description =
          "Genera un nuevo access token usando un refresh token válido. "
              + "Implementa Refresh Token Rotation: el refresh token antiguo se revoca automáticamente. "
              + "Requiere autenticación.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Tokens renovados exitosamente",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "Refresh token inválido, revocado o expirado"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
      })
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(
      @Parameter(description = "Refresh token válido", required = true) @Valid @RequestBody
          RefreshTokenRequest request) {

    logger.info("Request de refresh token");

    RefreshTokenCommand command = new RefreshTokenCommand(request.refreshToken());
    AuthResponseDTO authResponseDTO = refreshTokenUseCase.execute(command);

    AuthResponse response = AuthResponse.from(authResponseDTO);

    logger.info("Tokens renovados para usuario: {}", authResponseDTO.usuario().username());
    return ResponseEntity.ok(response);
  }

  @Operation(
      summary = "Logout de usuario",
      description =
          "Cierra la sesión revocando el refresh token. "
              + "Si se proporciona el access token, su JTI se agrega a la blacklist para invalidación inmediata. "
              + "Requiere autenticación.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Logout exitoso"),
        @ApiResponse(responseCode = "401", description = "Refresh token inválido"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
      })
  @PreAuthorize("isAuthenticated()")
  @PostMapping("/logout")
  public ResponseEntity<Void> logout(
      @Parameter(description = "Tokens a revocar", required = true) @Valid @RequestBody
          LogoutRequest request) {

    logger.info("Request de logout");

    LogoutCommand command = new LogoutCommand(request.refreshToken(), request.accessToken());
    logoutUseCase.execute(command);

    logger.info("Logout completado");
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Obtener perfil autenticado",
      description =
          "Retorna los datos del usuario autenticado. "
              + "El userId se extrae del access token JWT (claim 'sub') por el API Gateway y se envía en el header X-User-Id. "
              + "Requiere autenticación.",
      security = @SecurityRequirement(name = "bearerAuth"))
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Perfil obtenido exitosamente",
            content =
                @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = UsuarioResponse.class))),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Header X-User-Id ausente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
      })
  @PreAuthorize("isAuthenticated()")
  @GetMapping("/me")
  public ResponseEntity<UsuarioResponse> getAuthenticatedProfile(
      @Parameter(
              description = "User ID extraído del JWT por el Gateway",
              required = true,
              hidden = true)
          @RequestHeader(value = "X-User-Id", required = false)
          String userId) {

    logger.info("📥 Request de perfil autenticado - X-User-Id: {}", userId);

    // El Gateway valida el JWT y envía el userId en el header X-User-Id
    // Si el header no está presente, significa que:
    // 1. Se está accediendo directamente al servicio (sin pasar por el Gateway)
    // 2. El Gateway no pudo validar el token
    if (userId == null || userId.isBlank()) {
      logger.warn("Request sin X-User-Id header - El Gateway debe validar el JWT primero");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    UsuarioDTO usuarioDTO = obtenerPerfilAutenticadoUseCase.execute(userId);
    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info("Perfil obtenido para usuario: {} (ID: {})", usuarioDTO.username(), userId);
    return ResponseEntity.ok(response);
  }
}
