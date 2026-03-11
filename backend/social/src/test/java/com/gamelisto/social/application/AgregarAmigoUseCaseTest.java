package com.gamelisto.social.application;

import com.gamelisto.social.application.usecases.AgregarAmigoUseCase;
import com.gamelisto.social.dominio.AmistadRepositorio;
import com.gamelisto.social.dominio.exceptions.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@DisplayName("AgregarAmigoUseCase")
class AgregarAmigoUseCaseTest {
  @Mock private AmistadRepositorio repo;
  private AgregarAmigoUseCase useCase;

  @BeforeEach
  void setUp() {
    useCase = new AgregarAmigoUseCase(repo);
  }

  @Test
  @DisplayName("debe llamar a addFriendship")
  void debeLlamarAAddFriendship() {
    UUID u1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    UUID u2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    useCase.execute(u1, u2);
    verify(repo).addFriendship(u1, u2);
  }

  @Test
  @DisplayName("debe lanzar excepcion con ids iguales")
  void debeLanzarConMismosIds() {
    UUID u1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
    assertThrows(DomainException.class, () -> useCase.execute(u1, u1));
    verifyNoInteractions(repo);
  }
}
