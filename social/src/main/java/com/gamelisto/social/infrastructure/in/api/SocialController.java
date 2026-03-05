package com.gamelisto.social.infrastructure.in.api;

import com.gamelisto.social.application.usecases.UserRefResult;
import com.gamelisto.social.application.usecases.AgregarAmigoHandle;
import com.gamelisto.social.application.usecases.EliminarAmigoHandle;
import com.gamelisto.social.application.usecases.ListarAmigosEnComunHandle;
import com.gamelisto.social.application.usecases.ListarAmigosHandle;
import com.gamelisto.social.infrastructure.exceptions.InfrastructureException;
import com.gamelisto.social.infrastructure.in.api.dto.UsuarioRefResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
      @PathVariable String userId, @PathVariable String friendId, Authentication authentication) {
    assertSameUser(userId, authentication);
    log.info("Agregar amistad: {} -> {}", userId, friendId);
    agregarAmigo.execute(userId, friendId);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{userId}/friends/{friendId}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> removeFriend(
      @PathVariable String userId, @PathVariable String friendId, Authentication authentication) {
    assertSameUser(userId, authentication);
    log.info("Eliminar amistad: {} -> {}", userId, friendId);
    eliminarAmigo.execute(userId, friendId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{userId}/friends")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<UsuarioRefResponse>> listFriends(
      @PathVariable String userId, Authentication authentication) {
    assertSameUser(userId, authentication);
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
      @PathVariable String userAId, @PathVariable String userBId, Authentication authentication) {
    assertOneIsPrincipal(userAId, userBId, authentication);
    log.info("Listar amigos en común entre {} y {}", userAId, userBId);
    List<UserRefResult> commons = listarAmigosEnComun.execute(userAId, userBId);
    List<UsuarioRefResponse> response =
        commons.stream()
            .map(f -> new UsuarioRefResponse(f.id(), f.username(), f.avatar()))
            .toList();
    return ResponseEntity.ok(response);
  }

  private String principalAsString(Authentication authentication) {
    if (authentication == null || authentication.getPrincipal() == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authentication");
    }

    Object principal = authentication.getPrincipal();

    if (principal instanceof UserDetails userDetails) {
      return userDetails.getUsername();
    }

    return principal.toString();
  }

  private void assertSameUser(String userId, Authentication authentication) {
    String principal = principalAsString(authentication);
    if (!principal.equals(userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot operate on another user");
    }
  }

  private void assertOneIsPrincipal(String userAId, String userBId, Authentication authentication) {
    String principal = principalAsString(authentication);
    if (!principal.equals(userAId) && !principal.equals(userBId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed");
    }
  }
}
