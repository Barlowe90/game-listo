package com.gamelisto.usuarios;

import com.gamelisto.usuarios.config.TestMessagingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestMessagingConfig.class)
class UsuariosServiceApplicationTests {

  @Test
  void contextLoads() {}
}
