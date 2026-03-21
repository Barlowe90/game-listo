package com.gamelisto.biblioteca.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.GameId;
import com.gamelisto.biblioteca.domain.ListaGame;
import com.gamelisto.biblioteca.domain.ListaGameId;
import com.gamelisto.biblioteca.domain.ListaGameItemRepositorio;
import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
import com.gamelisto.biblioteca.domain.NombreListaGame;
import com.gamelisto.biblioteca.domain.Tipo;
import com.gamelisto.biblioteca.domain.UsuarioId;
import com.gamelisto.biblioteca.domain.UsuarioRef;
import com.gamelisto.biblioteca.domain.UsuariosRefRepositorio;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ImportarBibliotecaSteamUseCaseTest {

  @Mock private ListaGameRepositorio listaGameRepositorio;
  @Mock private ListaGameItemRepositorio listaGameItemRepositorio;
  @Mock private UsuariosRefRepositorio usuariosRefRepositorio;
  @Mock private SteamOwnedGamesPort steamOwnedGamesPort;
  @Mock private CatalogoSteamResolver catalogoSteamResolver;

  @InjectMocks private ImportarBibliotecaSteamUseCase useCase;

  @Test
  void should_create_steam_list_and_import_only_resolved_games() {
    UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    ListaGame steamList =
        ListaGame.reconstitute(
            ListaGameId.of(UUID.randomUUID()),
            userId,
            NombreListaGame.of("Steam"),
            Tipo.PERSONALIZADA);

    when(usuariosRefRepositorio.findById(userId))
        .thenReturn(Optional.of(UsuarioRef.reconstitute(userId, "user", "avatar")));
    when(listaGameRepositorio.findByUsuarioRefId(userId)).thenReturn(List.of());
    when(listaGameRepositorio.save(any())).thenReturn(steamList);
    when(steamOwnedGamesPort.findOwnedGames("76561198000000000"))
        .thenReturn(
            List.of(
                new SteamOwnedGame(620L, "Portal 2"),
                new SteamOwnedGame(730L, "Counter-Strike 2"),
                new SteamOwnedGame(999999L, "Juego desconocido")));
    when(catalogoSteamResolver.resolveGamesBySteamAppIds(List.of(620L, 730L, 999999L)))
        .thenReturn(Map.of(620L, 10L, 730L, 20L));
    when(listaGameItemRepositorio.findGameIdsByListaId(steamList.getId()))
        .thenReturn(List.of(GameId.of(20L)));

    ImportarBibliotecaSteamResult result =
        useCase.execute(new ImportarBibliotecaSteamCommand(userUuid, "76561198000000000"));

    verify(listaGameItemRepositorio).add(steamList.getId(), GameId.of(10L));
    verify(listaGameItemRepositorio, never()).add(steamList.getId(), GameId.of(20L));
    assertEquals(3, result.steamOwnedCount());
    assertEquals(2, result.resolvedCount());
    assertEquals(1, result.addedCount());
    assertEquals(1, result.alreadyPresentCount());
    assertEquals(1, result.unresolvedCount());
    assertEquals("Steam", result.listaNombre());
  }

  @Test
  void should_reuse_existing_steam_list_when_present() {
    UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);
    ListaGame steamList =
        ListaGame.reconstitute(
            ListaGameId.of(UUID.randomUUID()),
            userId,
            NombreListaGame.of("Steam"),
            Tipo.PERSONALIZADA);

    when(usuariosRefRepositorio.findById(userId))
        .thenReturn(Optional.of(UsuarioRef.reconstitute(userId, "user", "avatar")));
    when(listaGameRepositorio.findByUsuarioRefId(userId)).thenReturn(List.of(steamList));
    when(steamOwnedGamesPort.findOwnedGames("76561198000000000")).thenReturn(List.of());
    when(listaGameItemRepositorio.findGameIdsByListaId(steamList.getId())).thenReturn(List.of());

    useCase.execute(new ImportarBibliotecaSteamCommand(userUuid, "76561198000000000"));

    verify(listaGameRepositorio, never()).save(any());
  }

  @Test
  void should_throw_when_user_is_not_synced() {
    UUID userUuid = UUID.randomUUID();
    UsuarioId userId = UsuarioId.of(userUuid);

    when(usuariosRefRepositorio.findById(userId)).thenReturn(Optional.empty());

    assertThrows(
        ApplicationException.class,
        () -> useCase.execute(new ImportarBibliotecaSteamCommand(userUuid, "76561198000000000")));
  }
}
