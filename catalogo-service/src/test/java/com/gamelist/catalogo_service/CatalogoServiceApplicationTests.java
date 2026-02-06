package com.gamelist.catalogo_service;

import com.gamelist.catalogo_service.test.config.TestcontainersConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestcontainersConfig.class)
class CatalogoServiceApplicationTests {

  @Test
  void contextLoads() {}
}
