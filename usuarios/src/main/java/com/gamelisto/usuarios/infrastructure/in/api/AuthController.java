package com.gamelisto.usuarios.infrastructure.in.api;

import com.gamelisto.usuarios.application.dto.*;
import com.gamelisto.usuarios.application.usecases.*;
import com.gamelisto.usuarios.infrastructure.in.api.dto.*;
import com.gamelisto.usuarios.infrastructure.out.dto.AuthResponse;
import com.gamelisto.usuarios.infrastructure.out.dto.UsuarioResponse;
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
  @PostMapping("/register")
  public ResponseEntity<UsuarioResponse> registrar(
      @Valid @RequestBody CrearUsuarioRequest request) {

    logger.info("Request de registro para email: {}", request.email());

    CrearUsuarioCommand command = request.toCommand();
    UsuarioDTO usuarioDTO = crearUsuarioUseCase.execute(command);
    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info("Usuario registrado exitosamente: {}", usuarioDTO.username());
    // TODO que pueda entrar a su perfil, con el aviso de que tiene que verificar email.
    // TODO permisos para cambiar correo por si se ha equivocado
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/verify-email")
  public ResponseEntity<Void> verificarEmail(@Valid @RequestBody VerificarEmailRequest request) {

    logger.info("Request de verificación de email con token: {}", request.token());

    VerificarEmailCommand command = new VerificarEmailCommand(request.token());
    verificarEmailUseCase.execute(command);

    logger.info("Email verificado exitosamente");
    return ResponseEntity.ok().build();
  }

  @PostMapping("/resend-verification")
  public ResponseEntity<Void> reenviarVerificacion(
      @Valid @RequestBody ReenviarVerificacionRequest request) {

    logger.info("Request de reenvío de verificación para email: {}", request.email());

    reenviarVerificacionUseCase.execute(request.toCommand());

    logger.info("Email de verificación reenviado");
    return ResponseEntity.ok().build();
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<Void> solicitarRestablecimiento(
      @Valid @RequestBody SolicitarRestablecimientoRequest request) {

    logger.info("Request de restablecimiento de contraseña para email: {}", request.email());

    solicitarRestablecimientoUseCase.execute(request.toCommand());

    logger.info("Proceso de restablecimiento iniciado (si el email existe)");
    return ResponseEntity.ok().build();
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Void> restablecerContrasena(
      @Valid @RequestBody RestablecerContrasenaRequest request) {

    logger.info("Request de restablecimiento de contraseña con token");

    RestablecerContrasenaCommand command = request.toCommand();
    restablecerContrasenaUseCase.execute(command);

    logger.info("Contraseña restablecida exitosamente");
    return ResponseEntity.ok().build();
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

    logger.info("Request de login para email: {}", request.email());

    LoginCommand command = new LoginCommand(request.email(), request.password());
    AuthResponseDTO authResponseDTO = loginUseCase.execute(command);

    AuthResponse response = AuthResponse.from(authResponseDTO);

    logger.info("Login exitoso para usuario: {}", authResponseDTO.usuario().username());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {

    logger.info("Request de refresh token");

    RefreshTokenCommand command = new RefreshTokenCommand(request.refreshToken());
    AuthResponseDTO authResponseDTO = refreshTokenUseCase.execute(command);

    AuthResponse response = AuthResponse.from(authResponseDTO);

    logger.info("Tokens renovados para usuario: {}", authResponseDTO.usuario().username());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@Valid @RequestBody LogoutRequest request) {

    logger.info("Request de logout");

    LogoutCommand command = new LogoutCommand(request.refreshToken(), request.accessToken());
    logoutUseCase.execute(command);

    logger.info("Logout completado");
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UsuarioResponse> getAuthenticatedProfile(
      @RequestHeader(value = "X-User-Id", required = false) String userId) {

    logger.info("📥 Request de perfil autenticado - X-User-Id: {}", userId);

    // El Gateway valida el JWT y envia el userId en el header X-User-Id
    // Si el header no esta presente, significa que:
    // 1. Se esta accediendo directamente al servicio (sin pasar por el Gateway)
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
