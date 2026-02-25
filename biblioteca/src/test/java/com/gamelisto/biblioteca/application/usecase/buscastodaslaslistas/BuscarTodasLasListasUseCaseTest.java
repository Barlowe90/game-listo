package com.gamelisto.biblioteca.application.usecase.buscastodaslaslistas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.gamelisto.biblioteca.application.usecase.BuscarTodasLasListasUseCase;
import com.gamelisto.biblioteca.application.usecase.ListaGameResult;
import com.gamelisto.biblioteca.domain.ListaGame;
import com.gamelisto.biblioteca.domain.NombreListaGame;
import com.gamelisto.biblioteca.domain.Tipo;
import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
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

  @InjectMocks private BuscarTodasLasListasUseCase useCase;

  private UUID usuarioId;

  @BeforeEach
  void setUp() {
    usuarioId = UUID.randomUUID();
  }

  @Test
  @DisplayName("debeRetornarTodasLasListasDelUsuario")
  void debeRetornarTodasLasListasDelUsuario() {
    // Given
    ListaGame l1 = ListaGame.create(usuarioId, NombreListaGame.of("A"), Tipo.PERSONALIZADA);
    ListaGame l2 = ListaGame.create(usuarioId, NombreListaGame.of("B"), Tipo.PERSONALIZADA);
    when(listaGameRepositorio.findByUsuarioRefId(usuarioId)).thenReturn(List.of(l1, l2));

    // When
    List<ListaGameResult> results = useCase.execute(usuarioId.toString());

    // Then
    assertThat(results).hasSize(2);
  }
}
