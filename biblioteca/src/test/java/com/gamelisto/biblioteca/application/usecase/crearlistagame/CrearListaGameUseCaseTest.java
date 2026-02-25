package com.gamelisto.biblioteca.application.usecase.crearlistagame;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.gamelisto.biblioteca.application.exceptions.ApplicationException;
import com.gamelisto.biblioteca.application.usecase.CrearListaGameCommand;
import com.gamelisto.biblioteca.application.usecase.CrearListaGameUseCase;
import com.gamelisto.biblioteca.application.usecase.ListaGameResult;
import com.gamelisto.biblioteca.domain.ListaGame;
import com.gamelisto.biblioteca.domain.NombreListaGame;
import com.gamelisto.biblioteca.domain.Tipo;
import com.gamelisto.biblioteca.domain.ListaGameRepositorio;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrearListaGameUseCaseTest {

  @Mock private ListaGameRepositorio listaGameRepositorio;

  @InjectMocks private CrearListaGameUseCase useCase;

  private UUID usuarioId;

  @BeforeEach
  void setUp() {
    usuarioId = UUID.randomUUID();
  }

  @Test
  @DisplayName("debeCrearListaPersonalizada")
  void debeCrearListaPersonalizada() {
    // Given
    CrearListaGameCommand command =
        new CrearListaGameCommand(usuarioId.toString(), "Favoritos", "PERSONALIZADA");

    when(listaGameRepositorio.save(any(ListaGame.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // When
    ListaGameResult result = useCase.execute(command);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.nombre()).isEqualTo(NombreListaGame.of("Favoritos").value());
    assertThat(result.tipo()).isEqualTo(Tipo.PERSONALIZADA.name());
  }

  @Test
  @DisplayName("debeFallarSiNoEsPersonalizada")
  void debeFallarSiNoEsPersonalizada() {
    // Given
    CrearListaGameCommand command =
        new CrearListaGameCommand(usuarioId.toString(), "Oficial", "OFICIAL");

    // When & Then
    assertThatThrownBy(() -> useCase.execute(command))
        .isInstanceOf(ApplicationException.class)
        .hasMessageContaining("Solo se puede crear listas personalizadas");
  }
}
