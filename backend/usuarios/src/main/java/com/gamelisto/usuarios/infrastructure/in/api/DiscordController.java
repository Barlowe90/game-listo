package com.gamelisto.usuarios.infrastructure.in.api;

import com.gamelisto.usuarios.application.dto.UsuarioResult;
import com.gamelisto.usuarios.application.usecases.discord.DesvincularDiscordHandle;
import com.gamelisto.usuarios.application.usecases.discord.VincularDiscordHandle;
import com.gamelisto.usuarios.infrastructure.in.api.dto.VincularDiscordRequest;
import com.gamelisto.usuarios.infrastructure.in.api.dto.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
public class DiscordController {

  private final VincularDiscordHandle vincularDiscordUseCase;
  private final DesvincularDiscordHandle desvincularDiscordUseCase;
  private static final Logger logger = LoggerFactory.getLogger(DiscordController.class);

  @PutMapping(value = "/discord", consumes = "application/json")
  public ResponseEntity<UsuarioResponse> vincularDiscord(
      @AuthenticationPrincipal UUID userId, @Valid @RequestBody VincularDiscordRequest request) {
    logger.info(
        "PUT /v1/usuarios/discord - Vinculando cuenta de Discord para usuario con ID: {}", userId);

    UsuarioResult usuarioResult = vincularDiscordUseCase.execute(request.toCommand(userId));

    UsuarioResponse response = UsuarioResponse.from(usuarioResult);

    logger.info(
        "Cuenta de Discord vinculada exitosamente - ID: {}, Username: {}, Discord User ID: {}",
        response.id(),
        response.username(),
        response.discordUserId());

    return ResponseEntity.ok(response);
  }

  @DeleteMapping(value = "/discord")
  public ResponseEntity<UsuarioResponse> desvincularDiscord(@AuthenticationPrincipal UUID userId) {
    logger.info(
        "DELETE /v1/usuarios/discord - Desvinculando cuenta de Discord para usuario con ID: {}",
        userId);

    UsuarioResult usuarioResult = desvincularDiscordUseCase.execute(userId);

    UsuarioResponse response = UsuarioResponse.from(usuarioResult);

    logger.info(
        "Cuenta de Discord desvinculada exitosamente - ID: {}, Username: {}",
        response.id(),
        response.username());

    return ResponseEntity.ok(response);
  }
}
