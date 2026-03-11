package com.gamelisto.busquedas.application.usecases;

import com.gamelisto.busquedas.domain.BuscarJuegoDoc;
import com.gamelisto.busquedas.domain.BuscarJuegoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SugerirJuegosUseCase")
class SuggestGamesUseCaseTest {

  @Mock private BuscarJuegoRepositorio repositorio;

  @InjectMocks private SugerirJuegosUseCase useCase;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(useCase, "minChars", 2);
    ReflectionTestUtils.setField(useCase, "defaultSize", 4);
  }

  @Test
  @DisplayName("debe devolver lista vacía cuando el query es menor que minChars")
  void debeDevolverListaVaciaCuandoQueryEsMuyCorto() {
    List<BuscarJuegoDoc> result = useCase.execute("e", 4);

    assertThat(result).isEmpty();
    verifyNoInteractions(repositorio);
  }

  @Test
  @DisplayName("debe devolver lista vacía cuando el query es null")
  void debeDevolverListaVaciaCuandoQueryEsNull() {
    List<BuscarJuegoDoc> result = useCase.execute(null, 4);

    assertThat(result).isEmpty();
    verifyNoInteractions(repositorio);
  }
}
