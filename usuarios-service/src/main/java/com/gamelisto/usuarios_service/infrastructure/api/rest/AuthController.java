package com.gamelisto.usuarios_service.infrastructure.api.rest;

import com.gamelisto.usuarios_service.application.dto.AuthResponseDTO;
import com.gamelisto.usuarios_service.application.dto.LoginCommand;
import com.gamelisto.usuarios_service.application.dto.LogoutCommand;
import com.gamelisto.usuarios_service.application.dto.RefreshTokenCommand;
import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.application.usecases.LoginUseCase;
import com.gamelisto.usuarios_service.application.usecases.LogoutUseCase;
import com.gamelisto.usuarios_service.application.usecases.ObtenerPerfilAutenticadoUseCase;
import com.gamelisto.usuarios_service.application.usecases.RefreshTokenUseCase;
import com.gamelisto.usuarios_service.infrastructure.api.dto.AuthResponse;
import com.gamelisto.usuarios_service.infrastructure.api.dto.LoginRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.LogoutRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.RefreshTokenRequest;
import com.gamelisto.usuarios_service.infrastructure.api.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/usuarios/auth")
@RequiredArgsConstructor
@Tag(
    name = "Autenticación",
    description = "Endpoints de autenticación JWT - Login, Refresh, Logout")
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  private final LoginUseCase loginUseCase;
  private final RefreshTokenUseCase refreshTokenUseCase;
  private final LogoutUseCase logoutUseCase;
  private final ObtenerPerfilAutenticadoUseCase obtenerPerfilAutenticadoUseCase;

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
              + "Implementa Refresh Token Rotation: el refresh token antiguo se revoca automáticamente.")
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
              + "Si se proporciona el access token, su JTI se agrega a la blacklist para invalidación inmediata.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Logout exitoso"),
        @ApiResponse(responseCode = "401", description = "Refresh token inválido"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
      })
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
              + "El userId se extrae del access token JWT (claim 'sub') por el API Gateway y se envía en el header X-User-Id.")
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
      logger.warn("❌ Request sin X-User-Id header - El Gateway debe validar el JWT primero");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    UsuarioDTO usuarioDTO = obtenerPerfilAutenticadoUseCase.execute(userId);
    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info("✅ Perfil obtenido para usuario: {} (ID: {})", usuarioDTO.username(), userId);
    return ResponseEntity.ok(response);
  }
}
