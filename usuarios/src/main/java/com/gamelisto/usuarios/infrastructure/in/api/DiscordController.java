package com.gamelisto.usuarios.infrastructure.in.api;

import com.gamelisto.usuarios.application.dto.UsuarioDTO;
import com.gamelisto.usuarios.application.usecases.DesvincularDiscordUseCase;
import com.gamelisto.usuarios.application.usecases.VincularDiscordUseCase;
import com.gamelisto.usuarios.infrastructure.in.api.dto.VincularDiscordRequest;
import com.gamelisto.usuarios.infrastructure.out.dto.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/v1/usuarios")
@RequiredArgsConstructor
public class DiscordController {

  private final VincularDiscordUseCase vincularDiscordUseCase;
  private final DesvincularDiscordUseCase desvincularDiscordUseCase;
  private static final Logger logger = LoggerFactory.getLogger(DiscordController.class);

  @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal")
  @PutMapping(value = "/{id}/discord", consumes = "application/json")
  public ResponseEntity<UsuarioResponse> vincularDiscord(
      @PathVariable @NonNull String id, @Valid @RequestBody VincularDiscordRequest request) {
    logger.info(
        "PUT /v1/usuarios/{}/discord - Vinculando cuenta de Discord para usuario con ID: {}",
        id,
        id);

    UsuarioDTO usuarioDTO = vincularDiscordUseCase.execute(request.toCommand(id));

    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info(
        "Cuenta de Discord vinculada exitosamente - ID: {}, Username: {}, Discord: {}",
        response.id(),
        response.username(),
        response.discordUsername());

    return ResponseEntity.ok(response);
  }

  @DeleteMapping(value = "/{id}/discord")
  @PreAuthorize("hasRole('ADMIN') or #id.toString() == authentication.principal")
  public ResponseEntity<UsuarioResponse> desvincularDiscord(@PathVariable String id) {
    logger.info(
        "DELETE /v1/usuarios/{}/discord - Desvinculando cuenta de Discord para usuario con ID: {}",
        id,
        id);

    UsuarioDTO usuarioDTO = desvincularDiscordUseCase.execute(id);

    UsuarioResponse response = UsuarioResponse.from(usuarioDTO);

    logger.info(
        "Cuenta de Discord desvinculada exitosamente - ID: {}, Username: {}",
        response.id(),
        response.username());

    return ResponseEntity.ok(response);
  }
}
