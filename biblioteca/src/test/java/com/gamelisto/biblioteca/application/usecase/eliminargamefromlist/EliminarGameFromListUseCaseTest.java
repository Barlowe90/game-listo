package com.gamelisto.biblioteca.application.usecase.eliminargamefromlist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.application.usecase.EliminarGameFromListUseCase;
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
class EliminarGameFromListUseCaseTest {

  @Mock private ListaGameRepositorio listaGameRepositorio;
  @Mock private GameEstadoRepositorio gameEstadoRepositorio;

  @InjectMocks private EliminarGameFromListUseCase useCase;

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
  @DisplayName("debeEliminarGameEstadoDeLaLista")
  void debeEliminarGameEstadoDeLaLista() {
    // Given: reconstitute con el mismo id
    ListaGame lista =
        ListaGame.reconstitute(
            ListaGameId.of(listaId), usuarioId, NombreListaGame.of("Lista"), Tipo.PERSONALIZADA);
    GameEstado gameEstado =
        GameEstado.reconstitute(gameEstadoId, usuarioId, UUID.randomUUID(), Estado.DESEADO, 4.0);

    lista.addGameEstado(gameEstado);

    when(listaGameRepositorio.findById(ListaGameId.of(listaId))).thenReturn(Optional.of(lista));
    when(gameEstadoRepositorio.findByGameRefId(gameEstadoId)).thenReturn(Optional.of(gameEstado));
    when(listaGameRepositorio.save(any(ListaGame.class))).thenAnswer(i -> i.getArgument(0));

    // When
    ListaGameResult result =
        useCase.execute(usuarioId.toString(), listaId.toString(), gameEstadoId.toString());

    // Then: comprobar id y que el repositorio devolvió lista con id esperado
    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(listaId.toString());
  }

  @Test
  @DisplayName("debeLanzarExcepcionSiNoExisteGameEstado")
  void debeLanzarExcepcionSiNoExisteGameEstado() {
    // Given
    ListaGame lista =
        ListaGame.reconstitute(
            ListaGameId.of(listaId), usuarioId, NombreListaGame.of("Lista"), Tipo.PERSONALIZADA);
    when(listaGameRepositorio.findById(ListaGameId.of(listaId))).thenReturn(Optional.of(lista));
    when(gameEstadoRepositorio.findByGameRefId(gameEstadoId)).thenReturn(Optional.empty());

    // When & Then
    assertThatThrownBy(
            () ->
                useCase.execute(usuarioId.toString(), listaId.toString(), gameEstadoId.toString()))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("No se encuentra el gameEstado");
  }
}
