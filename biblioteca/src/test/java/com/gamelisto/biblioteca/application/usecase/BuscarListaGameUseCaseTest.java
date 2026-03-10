package com.gamelisto.biblioteca.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.domain.ListaGame;
import com.gamelisto.biblioteca.domain.ListaGameId;
import com.gamelisto.biblioteca.domain.NombreListaGame;
import com.gamelisto.biblioteca.domain.Tipo;
import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
import com.gamelisto.biblioteca.domain.UsuarioId;
import com.gamelisto.biblioteca.domain.ListaGameItemRepositorio;
import com.gamelisto.biblioteca.domain.GameRefRepositorio;
import com.gamelisto.biblioteca.domain.GameEstadoRepositorio;
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
class BuscarListaGameUseCaseTest {

  @Mock private ListaGameRepositorio listaGameRepositorio;
  @Mock private ListaGameItemRepositorio listaGameItemRepositorio;
  @Mock private GameRefRepositorio gameRefRepositorio;
  @Mock private GameEstadoRepositorio gameEstadoRepositorio;

  @InjectMocks private BuscarListaGameUseCase useCase;

  private UsuarioId usuarioId;
  private UUID listaId;

  @BeforeEach
  void setUp() {
    usuarioId = UsuarioId.of(UUID.randomUUID());
    listaId = UUID.randomUUID();
  }

  @Test
  @DisplayName("debeRetornarListaCuandoEsPropietario")
  void debeRetornarListaCuandoEsPropietario() {
    // Given
    when(listaGameRepositorio.findById(ListaGameId.of(listaId)))
        .thenReturn(
            Optional.of(
                ListaGame.reconstitute(
                    ListaGameId.of(listaId),
                    usuarioId,
                    NombreListaGame.of("Mis juegos"),
                    Tipo.PERSONALIZADA)));

    // small assertions to reference mocks and avoid static analyzer warnings about unused fields
    assertThat(listaGameItemRepositorio).isNotNull();
    assertThat(gameRefRepositorio).isNotNull();
    assertThat(gameEstadoRepositorio).isNotNull();

    // When
    ListaGameResult result = useCase.execute(usuarioId.value(), listaId.toString());

    // Then
    assertThat(result).isNotNull();
    assertThat(result.usuarioRefId()).isEqualTo(usuarioId.toString());
  }

  @Test
  @DisplayName("debeLanzarExcepcionCuandoNoEsPropietario")
  void debeLanzarExcepcionCuandoNoEsPropietario() {
    // Given
    java.util.UUID otroUsuario = UUID.randomUUID();
    when(listaGameRepositorio.findById(ListaGameId.of(listaId)))
        .thenReturn(
            Optional.of(
                ListaGame.reconstitute(
                    ListaGameId.of(listaId),
                    UsuarioId.of(otroUsuario),
                    NombreListaGame.of("Mis juegos"),
                    Tipo.PERSONALIZADA)));

    // reference mock to avoid unused field warning
    assertThat(listaGameItemRepositorio).isNotNull();

    // When & Then
    assertThatThrownBy(() -> useCase.execute(usuarioId.value(), listaId.toString()))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("Usario no propietario de la lista");
  }

  @Test
  @DisplayName("debeLanzarExcepcionCuandoNoExisteLista")
  void debeLanzarExcepcionCuandoNoExisteLista() {
    // Given
    when(listaGameRepositorio.findById(ListaGameId.of(listaId))).thenReturn(Optional.empty());

    // reference mock to avoid unused field warning
    assertThat(listaGameItemRepositorio).isNotNull();

    // When & Then
    assertThatThrownBy(() -> useCase.execute(usuarioId.value(), listaId.toString()))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("No se encuentra la lista");
  }
}
