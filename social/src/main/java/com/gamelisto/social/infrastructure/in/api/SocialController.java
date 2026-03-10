package com.gamelisto.social.infrastructure.in.api;

import com.gamelisto.social.application.usecases.*;
import com.gamelisto.social.infrastructure.in.api.dto.ResumenSocialJuegoResponse;
import com.gamelisto.social.infrastructure.in.api.dto.UsuarioRefResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/social")
@RequiredArgsConstructor
public class SocialController {

  private static final Logger log = LoggerFactory.getLogger(SocialController.class);

  private final AgregarAmigoHandle agregarAmigo;
  private final EliminarAmigoHandle eliminarAmigo;
  private final ListarAmigosHandle listarAmigos;
  private final ConsultarResumenSocialJuegoHandle consultarResumenSocialJuego;

  @PostMapping("/friends/{friendId}")
  public ResponseEntity<Void> addFriend(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID friendId) {
    log.info("Agregar amistad: {} -> {}", userId, friendId);
    agregarAmigo.execute(userId, friendId);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/friends/{friendId}")
  public ResponseEntity<Void> removeFriend(
      @AuthenticationPrincipal UUID userId, @PathVariable UUID friendId) {
    log.info("Eliminar amistad: {} -> {}", userId, friendId);
    eliminarAmigo.execute(userId, friendId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/friends")
  public ResponseEntity<List<UsuarioRefResponse>> listFriends(
      @AuthenticationPrincipal UUID userId) {
    log.info("Listar amigos de usuario: {}", userId);
    List<UserRefResult> friends = listarAmigos.execute(userId);
    List<UsuarioRefResponse> response =
        friends.stream()
            .map(f -> new UsuarioRefResponse(f.id(), f.username(), f.avatar()))
            .toList();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/games/{gameId}/summary")
  public ResponseEntity<ResumenSocialJuegoResponse> getGameSocialSummary(
      @AuthenticationPrincipal UUID userId, @PathVariable Long gameId) {
    ResumenSocialJuegoResult result = consultarResumenSocialJuego.execute(userId, gameId);
    return ResponseEntity.ok(ResumenSocialJuegoResponse.from(result));
  }
}
