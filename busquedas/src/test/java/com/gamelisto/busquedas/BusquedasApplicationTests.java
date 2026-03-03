package com.gamelisto.busquedas;

import org.junit.jupiter.api.Test;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class BusquedasApplicationTests {

  /** Evitar que Spring intente conectarse a OpenSearch al arrancar el contexto de test. */
  @MockitoBean OpenSearchClient openSearchClient;

  @Test
  void contextLoads() {}
}
