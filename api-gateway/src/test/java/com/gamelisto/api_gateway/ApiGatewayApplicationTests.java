package com.gamelisto.api_gateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ApiGatewayApplicationTests {

  @MockBean private ReactiveRedisTemplate<String, String> redisTemplate;

  @Test
  void contextLoads() {}
}
