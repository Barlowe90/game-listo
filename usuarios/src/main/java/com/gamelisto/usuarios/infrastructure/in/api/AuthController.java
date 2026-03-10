package com.gamelisto.usuarios.infrastructure.in.api;

import com.gamelisto.usuarios.application.dto.*;
import com.gamelisto.usuarios.application.usecases.*;
import com.gamelisto.usuarios.infrastructure.in.api.dto.*;
import com.gamelisto.usuarios.infrastructure.in.api.dto.AuthResponse;
import com.gamelisto.usuarios.infrastructure.in.api.dto.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
public class AuthController {

  private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

  private final LoginUseHandle loginUseCase;
  private final RefreshTokenHandle refreshTokenUseCase;
  private final LogoutHandle logoutUseCase;
  private final ObtenerPerfilAutenticadoHandle obtenerPerfilAutenticadoUseCase;
  private final CrearUsuarioHandle crearUsuarioUseCase;
  private final VerificarEmailHandle verificarEmailUseCase;
  private final ReenviarVerificacionHandle reenviarVerificacionUseCase;
  private final SolicitarRestablecimientoHandle solicitarRestablecimientoUseCase;
  private final RestablecerContrasenaHandle restablecerContrasenaUseCase;

  @PostMapping("/auth/register")
  public ResponseEntity<UsuarioResponse> registrar(
      @Valid @RequestBody CrearUsuarioRequest request) {

    logger.info("Request de registro para email: {}", request.email());

    CrearUsuarioCommand command = request.toCommand();
    UsuarioResult usuarioResult = crearUsuarioUseCase.execute(command);
    UsuarioResponse response = UsuarioResponse.from(usuarioResult);

    logger.info("Usuario registrado exitosamente: {}", usuarioResult.username());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/auth/verify-email")
  public ResponseEntity<Void> verificarEmail(@Valid @RequestBody VerificarEmailRequest request) {

    logger.info("Request de verificación de email con token: {}", request.token());

    VerificarEmailCommand command = new VerificarEmailCommand(request.token());
    verificarEmailUseCase.execute(command);

    logger.info("Email verificado exitosamente");
    return ResponseEntity.ok().build();
  }

  @PostMapping("/auth/resend-verification")
  public ResponseEntity<Void> reenviarVerificacion(
      @Valid @RequestBody ReenviarVerificacionRequest request) {

    logger.info("Request de reenvío de verificación para email: {}", request.email());

    reenviarVerificacionUseCase.execute(request.toCommand());

    logger.info("Email de verificación reenviado");
    return ResponseEntity.ok().build();
  }

  @PostMapping("/auth/forgot-password")
  public ResponseEntity<Void> solicitarRestablecimiento(
      @Valid @RequestBody SolicitarRestablecimientoRequest request) {

    logger.info("Request de restablecimiento de contraseña para email: {}", request.email());

    solicitarRestablecimientoUseCase.execute(request.toCommand());

    logger.info("Proceso de restablecimiento iniciado (si el email existe)");
    return ResponseEntity.ok().build();
  }

  @PostMapping("/auth/reset-password")
  public ResponseEntity<Void> restablecerContrasena(
      @Valid @RequestBody RestablecerContrasenaRequest request) {

    logger.info("Request de restablecimiento de contraseña con token");

    RestablecerContrasenaCommand command = request.toCommand();
    restablecerContrasenaUseCase.execute(command);

    logger.info("Contraseña restablecida exitosamente");
    return ResponseEntity.ok().build();
  }

  @PostMapping("/auth/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

    logger.info("Request de login para email: {}", request.email());

    LoginCommand command = new LoginCommand(request.email(), request.password());
    AuthResponseResult authResponseResult = loginUseCase.execute(command);

    AuthResponse response = AuthResponse.from(authResponseResult);

    logger.info("Login exitoso para usuario: {}", authResponseResult.usuario().username());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/auth/refresh")
  public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {

    logger.info("Request de refresh token");

    RefreshTokenCommand command = new RefreshTokenCommand(request.refreshToken());
    AuthResponseResult authResponseResult = refreshTokenUseCase.execute(command);

    AuthResponse response = AuthResponse.from(authResponseResult);

    logger.info("Tokens renovados para usuario: {}", authResponseResult.usuario().username());
    return ResponseEntity.ok(response);
  }

  @PostMapping("/auth/logout")
  public ResponseEntity<Void> logout(
      @Valid @RequestBody LogoutRequest request, @AuthenticationPrincipal UUID userId) {
    // TODO comprobar que es el user que hace logout
    logger.info("Request de logout userId {}", userId);

    LogoutCommand command = new LogoutCommand(request.refreshToken(), request.accessToken());
    logoutUseCase.execute(command);

    logger.info("Logout completado");
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/auth/me")
  public ResponseEntity<UsuarioResponse> getAuthenticatedProfile(
      @AuthenticationPrincipal UUID userId) {

    logger.info("Me userId: {}", userId);

    UsuarioResult usuarioResult = obtenerPerfilAutenticadoUseCase.execute(userId);
    UsuarioResponse response = UsuarioResponse.from(usuarioResult);

    logger.info("Perfil obtenido para usuario: {} (ID: {})", usuarioResult.username(), userId);
    return ResponseEntity.ok(response);
  }
}
