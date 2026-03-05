package com.gamelisto.social.infrastructure.in.api;

import com.gamelisto.social.application.usecases.UserRefResult;
import com.gamelisto.social.application.usecases.AgregarAmigoHandle;
import com.gamelisto.social.application.usecases.EliminarAmigoHandle;
import com.gamelisto.social.application.usecases.ListarAmigosEnComunHandle;
import com.gamelisto.social.application.usecases.ListarAmigosHandle;
import com.gamelisto.social.infrastructure.in.api.dto.UsuarioRefResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/social/users")
@RequiredArgsConstructor
public class SocialController {

  private static final Logger log = LoggerFactory.getLogger(SocialController.class);

  private final AgregarAmigoHandle agregarAmigo;
  private final EliminarAmigoHandle eliminarAmigo;
  private final ListarAmigosHandle listarAmigos;
  private final ListarAmigosEnComunHandle listarAmigosEnComun;

  @PostMapping("/{userId}/friends/{friendId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> addFriend(
      @PathVariable String userId, @PathVariable String friendId) {
    log.info("Agregar amistad: {} -> {}", userId, friendId);
    agregarAmigo.execute(userId, friendId);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{userId}/friends/{friendId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> removeFriend(
      @PathVariable String userId, @PathVariable String friendId) {
    log.info("Eliminar amistad: {} -> {}", userId, friendId);
    eliminarAmigo.execute(userId, friendId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{userId}/friends")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<UsuarioRefResponse>> listFriends(@PathVariable String userId) {
    log.info("Listar amigos de usuario: {}", userId);
    List<UserRefResult> friends = listarAmigos.execute(userId);
    List<UsuarioRefResponse> response =
        friends.stream()
            .map(f -> new UsuarioRefResponse(f.id(), f.username(), f.avatar()))
            .toList();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{userAId}/friends/common/{userBId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<UsuarioRefResponse>> listCommonFriends(
      @PathVariable String userAId, @PathVariable String userBId) {
    log.info("Listar amigos en común entre {} y {}", userAId, userBId);
    List<UserRefResult> commons = listarAmigosEnComun.execute(userAId, userBId);
    List<UsuarioRefResponse> response =
        commons.stream()
            .map(f -> new UsuarioRefResponse(f.id(), f.username(), f.avatar()))
            .toList();
    return ResponseEntity.ok(response);
  }
}
