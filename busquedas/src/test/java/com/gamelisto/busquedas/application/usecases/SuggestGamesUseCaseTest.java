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
    List<BuscarJuegoDoc> result = useCase.execute("e", null);

    assertThat(result).isEmpty();
    verifyNoInteractions(repositorio);
  }

  @Test
  @DisplayName("debe devolver lista vacía cuando el query es null")
  void debeDevolverListaVaciaCuandoQueryEsNull() {
    List<BuscarJuegoDoc> result = useCase.execute(null, null);

    assertThat(result).isEmpty();
    verifyNoInteractions(repositorio);
  }

  @Test
  @DisplayName("debe llamar al repositorio con el tamaño por defecto cuando no se especifica size")
  void debeUsarTamanioDefaultSiNoSeEspecifica() {
    List<BuscarJuegoDoc> docs = List.of(new BuscarJuegoDoc(1L, "Elden Ring", List.of()));
    when(repositorio.suggest("eld", 4)).thenReturn(docs);

    List<BuscarJuegoDoc> result = useCase.execute("eld", null);

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getTitle()).isEqualTo("Elden Ring");
    verify(repositorio).suggest("eld", 4);
  }

  @Test
  @DisplayName("debe limitar size al máximo permitido (20)")
  void debeLimitarSizeAlMaximo() {
    when(repositorio.suggest("eld", 20)).thenReturn(List.of());

    useCase.execute("eld", 999);

    verify(repositorio).suggest("eld", 20);
  }

  @Test
  @DisplayName("debe pasar el size solicitado si es menor que el máximo")
  void debeUsarSizeSolicitadoSiEsMenorQueMaximo() {
    when(repositorio.suggest("eld", 5)).thenReturn(List.of());

    useCase.execute("eld", 5);

    verify(repositorio).suggest("eld", 5);
  }
}
