package com.gamelisto.biblioteca.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.gamelisto.biblioteca.domain.ListaGame;
import com.gamelisto.biblioteca.domain.NombreListaGame;
import com.gamelisto.biblioteca.domain.Tipo;
import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
import com.gamelisto.biblioteca.domain.UsuarioId;
import com.gamelisto.biblioteca.domain.ListaGameItemRepositorio;
import com.gamelisto.biblioteca.domain.GameRefRepositorio;
import com.gamelisto.biblioteca.domain.GameEstadoRepositorio;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BuscarTodasLasListasUseCaseTest {

  @Mock private ListaGameRepositorio listaGameRepositorio;
  @Mock private ListaGameItemRepositorio listaGameItemRepositorio;
  @Mock private GameRefRepositorio gameRefRepositorio;
  @Mock private GameEstadoRepositorio gameEstadoRepositorio;

  @InjectMocks private BuscarTodasLasListasUseCase useCase;

  private UsuarioId usuarioId;

  @BeforeEach
  void setUp() {
    usuarioId = UsuarioId.of(UUID.randomUUID());
  }

  @Test
  @DisplayName("debeRetornarTodasLasListasDelUsuario")
  void debeRetornarTodasLasListasDelUsuario() {
    // Given
    ListaGame l1 = ListaGame.create(usuarioId, NombreListaGame.of("AAA"), Tipo.PERSONALIZADA);
    ListaGame l2 = ListaGame.create(usuarioId, NombreListaGame.of("BBB"), Tipo.PERSONALIZADA);
    when(listaGameRepositorio.findByUsuarioRefId(usuarioId)).thenReturn(List.of(l1, l2));
    assertThat(listaGameItemRepositorio).isNotNull();
    assertThat(gameRefRepositorio).isNotNull();
    assertThat(gameEstadoRepositorio).isNotNull();

    // When
    List<ListaGameResult> results = useCase.execute(usuarioId.value());

    // Then
    assertThat(results).hasSize(2);
  }
}
