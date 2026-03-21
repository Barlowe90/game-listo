package com.gamelisto.biblioteca.application.usecase;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.GameId;
import com.gamelisto.biblioteca.domain.ListaGame;
import com.gamelisto.biblioteca.domain.ListaGameId;
import com.gamelisto.biblioteca.domain.ListaGameItemRepositorio;
import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
import com.gamelisto.biblioteca.domain.NombreListaGame;
import com.gamelisto.biblioteca.domain.Tipo;
import com.gamelisto.biblioteca.domain.UsuarioId;
import com.gamelisto.biblioteca.domain.UsuariosRefRepositorio;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImportarBibliotecaSteamUseCase implements ImportarBibliotecaSteamHandler {

  private static final String STEAM_LIST_NAME = "Steam";
  private static final int RESOLVE_CHUNK_SIZE = 250;

  private final ListaGameRepositorio listaGameRepositorio;
  private final ListaGameItemRepositorio listaGameItemRepositorio;
  private final UsuariosRefRepositorio usuariosRefRepositorio;
  private final SteamOwnedGamesPort steamOwnedGamesPort;
  private final CatalogoSteamResolver catalogoSteamResolver;

  @Override
  @Transactional
  public ImportarBibliotecaSteamResult execute(ImportarBibliotecaSteamCommand command) {
    UsuarioId userId = UsuarioId.of(command.userId());
    validarUsuarioSincronizado(userId);

    ListaGame steamList = findOrCreateSteamList(userId);
    List<SteamOwnedGame> uniqueOwnedGames =
        deduplicateOwnedGames(steamOwnedGamesPort.findOwnedGames(command.steamId64()));
    Map<Long, Long> resolvedGamesByAppId = resolveGames(uniqueOwnedGames);
    Set<Long> existingGameIds = loadExistingGameIds(steamList.getId());

    int addedCount = 0;
    int alreadyPresentCount = 0;

    for (SteamOwnedGame ownedGame : uniqueOwnedGames) {
      Long gameId = resolvedGamesByAppId.get(ownedGame.steamAppId());
      if (gameId == null) {
        continue;
      }

      if (existingGameIds.add(gameId)) {
        listaGameItemRepositorio.add(steamList.getId(), GameId.of(gameId));
        addedCount++;
      } else {
        alreadyPresentCount++;
      }
    }

    int steamOwnedCount = uniqueOwnedGames.size();
    int resolvedCount = resolvedGamesByAppId.size();

    return new ImportarBibliotecaSteamResult(
        steamList.getId().value().toString(),
        steamList.getNombreLista().value(),
        steamOwnedCount,
        resolvedCount,
        addedCount,
        alreadyPresentCount,
        Math.max(steamOwnedCount - resolvedCount, 0));
  }

  private void validarUsuarioSincronizado(UsuarioId userId) {
    if (usuariosRefRepositorio.findById(userId).isEmpty()) {
      throw new ApplicationException("Usuario no sincronizado");
    }
  }

  private ListaGame findOrCreateSteamList(UsuarioId userId) {
    return listaGameRepositorio.findByUsuarioRefId(userId).stream()
        .filter(lista -> lista.getTipo() == Tipo.PERSONALIZADA)
        .filter(lista -> lista.getNombreLista().value().equalsIgnoreCase(STEAM_LIST_NAME))
        .findFirst()
        .orElseGet(() -> crearSteamList(userId));
  }

  private ListaGame crearSteamList(UsuarioId userId) {
    ListaGame steamList =
        ListaGame.create(userId, NombreListaGame.of(STEAM_LIST_NAME), Tipo.PERSONALIZADA);
    return listaGameRepositorio.save(steamList);
  }

  private static List<SteamOwnedGame> deduplicateOwnedGames(List<SteamOwnedGame> ownedGames) {
    Map<Long, SteamOwnedGame> uniqueGamesByAppId = new LinkedHashMap<>();
    for (SteamOwnedGame ownedGame : ownedGames) {
      if (ownedGame == null || ownedGame.steamAppId() == null) {
        continue;
      }
      uniqueGamesByAppId.putIfAbsent(ownedGame.steamAppId(), ownedGame);
    }
    return List.copyOf(uniqueGamesByAppId.values());
  }

  private Map<Long, Long> resolveGames(List<SteamOwnedGame> ownedGames) {
    List<Long> steamAppIds = ownedGames.stream().map(SteamOwnedGame::steamAppId).toList();
    Map<Long, Long> resolvedGames = new LinkedHashMap<>();

    for (int start = 0; start < steamAppIds.size(); start += RESOLVE_CHUNK_SIZE) {
      int end = Math.min(start + RESOLVE_CHUNK_SIZE, steamAppIds.size());
      resolvedGames.putAll(
          catalogoSteamResolver.resolveGamesBySteamAppIds(steamAppIds.subList(start, end)));
    }

    return resolvedGames;
  }

  private Set<Long> loadExistingGameIds(ListaGameId listaId) {
    return new LinkedHashSet<>(
        listaGameItemRepositorio.findGameIdsByListaId(listaId).stream().map(GameId::value).toList());
  }
}
