package com.gamelisto.gateway.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;
import java.time.Duration;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimitFilter - tests básicos")
class RateLimitFilterTest {

  private static final String URL = "http://localhost:8080/v1/usuarios/perfil";

  @Mock private ReactiveRedisTemplate<String, String> redisTemplate;
  @Mock private ReactiveValueOperations<String, String> valueOps;
  @Mock private GatewayFilterChain chain;

  private RateLimitFilter filter;

  @BeforeEach
  void setUp() {
    filter = new RateLimitFilter(redisTemplate);
  }

  @Test
  @DisplayName("Permite request cuando está por debajo del límite")
  void permiteBajoElLimite() {
    InetSocketAddress address = new InetSocketAddress("192.168.1.100", 8080);

    ServerWebExchange exchange =
        MockServerWebExchange.from(MockServerHttpRequest.get(URL).remoteAddress(address).build());

    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.increment("rate_limit:192.168.1.100")).thenReturn(Mono.just(1L));
    when(redisTemplate.expire(eq("rate_limit:192.168.1.100"), any(Duration.class)))
        .thenReturn(Mono.just(true));
    when(chain.filter(exchange)).thenReturn(Mono.empty());

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    verify(chain, times(1)).filter(exchange);
  }

  @Test
  @DisplayName("Fail-open: si Redis falla, permite la petición")
  void failOpenSiRedisFalla() {
    InetSocketAddress address = new InetSocketAddress("192.168.1.100", 8080);

    ServerWebExchange exchange =
        MockServerWebExchange.from(MockServerHttpRequest.get(URL).remoteAddress(address).build());

    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.increment(anyString()))
        .thenReturn(Mono.error(new RuntimeException("Redis connection failed")));
    when(chain.filter(exchange)).thenReturn(Mono.empty());

    StepVerifier.create(filter.filter(exchange, chain)).verifyComplete();

    verify(chain, times(1)).filter(exchange);
  }
}
