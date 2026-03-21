package com.gamelisto.biblioteca.infrastructure.in.api;

import com.gamelisto.biblioteca.application.usecase.ImportarBibliotecaSteamHandler;
import com.gamelisto.biblioteca.application.usecase.ImportarBibliotecaSteamResult;
import com.gamelisto.biblioteca.infrastructure.in.api.dto.ImportarBibliotecaSteamRequest;
import com.gamelisto.biblioteca.infrastructure.in.api.dto.ImportarBibliotecaSteamResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/biblioteca")
@RequiredArgsConstructor
public class SteamImportController {

  private static final Logger logger = LoggerFactory.getLogger(SteamImportController.class);

  private final ImportarBibliotecaSteamHandler importarBibliotecaSteam;

  @PostMapping("/imports/steam")
  public ResponseEntity<ImportarBibliotecaSteamResponse> importSteamLibrary(
      @AuthenticationPrincipal UUID userId,
      @Valid @RequestBody ImportarBibliotecaSteamRequest request) {

    logger.info("Importando biblioteca de Steam para el usuario {}", userId);

    ImportarBibliotecaSteamResult result =
        importarBibliotecaSteam.execute(request.toCommand(userId));

    return ResponseEntity.ok(ImportarBibliotecaSteamResponse.from(result));
  }
}
