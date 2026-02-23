package com.gamelisto.biblioteca;

import com.gamelisto.biblioteca.config.TestMessagingConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestMessagingConfig.class)
class BibliotecaApplicationTests {

  @Test
  void contextLoads() {}
}
