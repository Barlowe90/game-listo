package com.gamelisto.biblioteca.application.usecase.addgametolist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.application.usecase.AddGameToListUseCase;
import com.gamelisto.biblioteca.application.usecase.ListaGameResult;
import com.gamelisto.biblioteca.domain.Estado;
import com.gamelisto.biblioteca.domain.GameEstado;
import com.gamelisto.biblioteca.domain.ListaGame;
import com.gamelisto.biblioteca.domain.ListaGameId;
import com.gamelisto.biblioteca.domain.NombreListaGame;
import com.gamelisto.biblioteca.domain.Tipo;
import com.gamelisto.biblioteca.domain.GameEstadoRepositorio;
import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddGameToListUseCaseTest {

  @Mock private ListaGameRepositorio listaGameRepositorio;
  @Mock private GameEstadoRepositorio gameEstadoRepositorio;

  @InjectMocks private AddGameToListUseCase useCase;

  private UUID usuarioId;
  private UUID listaId;
  private UUID gameEstadoId;

  @BeforeEach
  void setUp() {
    usuarioId = UUID.randomUUID();
    listaId = UUID.randomUUID();
    gameEstadoId = UUID.randomUUID();
  }

  @Test
  @DisplayName("debeAñadirGameEstadoALaListaCuandoEsPropietario")
  void debeAñadirGameEstadoALaListaCuandoEsPropietario() {
    // Given: reconstitute lista con el mismo id que usamos en el mock
    ListaGame lista =
        ListaGame.reconstitute(
            ListaGameId.of(listaId), usuarioId, NombreListaGame.of("Lista"), Tipo.PERSONALIZADA);
    GameEstado gameEstado =
        GameEstado.reconstitute(gameEstadoId, usuarioId, UUID.randomUUID(), Estado.DESEADO, 4.0);

    when(listaGameRepositorio.findById(ListaGameId.of(listaId))).thenReturn(Optional.of(lista));
    when(gameEstadoRepositorio.findById(gameEstadoId)).thenReturn(Optional.of(gameEstado));
    when(listaGameRepositorio.save(any(ListaGame.class))).thenAnswer(i -> i.getArgument(0));

    // When
    ListaGameResult result =
        useCase.execute(usuarioId.toString(), listaId.toString(), gameEstadoId.toString());

    // Then: el resultado debe contener el id de la lista
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(listaId.toString());
    assertThat(result.usuarioRefId()).isEqualTo(usuarioId.toString());
  }

  @Test
  @DisplayName("debeLanzarExcepcionSiListaNoPerteneceAlUsuario")
  void debeLanzarExcepcionSiListaNoPerteneceAlUsuario() {
    // Given
    UUID otroUsuario = UUID.randomUUID();
    ListaGame lista =
        ListaGame.reconstitute(
            ListaGameId.of(listaId), otroUsuario, NombreListaGame.of("Lista"), Tipo.PERSONALIZADA);
    when(listaGameRepositorio.findById(ListaGameId.of(listaId))).thenReturn(Optional.of(lista));
    when(gameEstadoRepositorio.findById(gameEstadoId))
        .thenReturn(
            Optional.of(GameEstado.create(usuarioId, UUID.randomUUID(), Estado.DESEADO, 3.0)));

    // When & Then
    assertThatThrownBy(
            () ->
                useCase.execute(usuarioId.toString(), listaId.toString(), gameEstadoId.toString()))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("Usuario no propietario de la lista");
  }

  @Test
  @DisplayName("debeLanzarExcepcionSiNoExisteGameEstado")
  void debeLanzarExcepcionSiNoExisteGameEstado() {
    // Given
    ListaGame lista =
        ListaGame.reconstitute(
            ListaGameId.of(listaId), usuarioId, NombreListaGame.of("Lista"), Tipo.PERSONALIZADA);
    when(listaGameRepositorio.findById(ListaGameId.of(listaId))).thenReturn(Optional.of(lista));
    when(gameEstadoRepositorio.findById(gameEstadoId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(
            () ->
                useCase.execute(usuarioId.toString(), listaId.toString(), gameEstadoId.toString()))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("No se encuentra el game estado");
  }
}
