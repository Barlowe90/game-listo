package com.gamelisto.api_gateway.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.InetSocketAddress;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RateLimitFilter - Control de tráfico por IP")
class RateLimitFilterTest {

  public static final String HTTP_LOCALHOST_8090_V_1_USUARIOS_PERFIL =
      "http://localhost:8090/v1/usuarios/perfil";
  public static final String DIRECCION_IP = "192.168.1.100";
  public static final String RATE_LIMIT_10_0_0_5 = "rate_limit:10.0.0.5";
  public static final String RATE_LIMIT_UNKNOWN = "rate_limit:unknown";
  public static final String RATE_LIMIT_192_168_1_100 = "rate_limit:192.168.1.100";
  @Mock private ReactiveRedisTemplate<String, String> redisTemplate;
  @Mock private ReactiveValueOperations<String, String> valueOps;
  @Mock private GatewayFilterChain chain;

  private RateLimitFilter filter;

  @BeforeEach
  void setUp() {
    filter = new RateLimitFilter(redisTemplate);
  }

  @Test
  @DisplayName("Debe permitir request si límite no se ha excedido (primera petición)")
  void debePermitirRequestSiLimiteNoExcedidoPrimeraPeticion() {
    // Arrange
    InetSocketAddress address = new InetSocketAddress(DIRECCION_IP, 8080);
    MockServerHttpRequest request =
        MockServerHttpRequest.get(HTTP_LOCALHOST_8090_V_1_USUARIOS_PERFIL)
            .remoteAddress(address)
            .build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.increment(eq(RATE_LIMIT_192_168_1_100))).thenReturn(Mono.just(1L));
    when(redisTemplate.expire(eq(RATE_LIMIT_192_168_1_100), any(Duration.class)))
        .thenReturn(Mono.just(true));
    when(chain.filter(exchange)).thenReturn(Mono.empty());

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    verify(valueOps, times(1)).increment(RATE_LIMIT_192_168_1_100);
    verify(redisTemplate, times(1)).expire(eq(RATE_LIMIT_192_168_1_100), any(Duration.class));
    verify(chain, times(1)).filter(exchange);
  }

  @Test
  @DisplayName("Debe permitir request si límite no se ha excedido (petición 50)")
  void debePermitirRequestSiLimiteNoExcedidoPeticionIntermedia() {
    // Arrange
    InetSocketAddress address = new InetSocketAddress(DIRECCION_IP, 8080);
    MockServerHttpRequest request =
        MockServerHttpRequest.get(HTTP_LOCALHOST_8090_V_1_USUARIOS_PERFIL)
            .remoteAddress(address)
            .build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.increment(eq(RATE_LIMIT_192_168_1_100))).thenReturn(Mono.just(50L));
    when(chain.filter(exchange)).thenReturn(Mono.empty());

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    verify(chain, times(1)).filter(exchange);
    verify(redisTemplate, never()).expire(any(), any(Duration.class)); // Solo en count=1
  }

  @Test
  @DisplayName("Debe bloquear request con 429 Too Many Requests si se excede límite (101 req)")
  void debeBloquerRequestSiSeExcedeLimite() {
    // Arrange
    InetSocketAddress address = new InetSocketAddress(DIRECCION_IP, 8080);
    MockServerHttpRequest request =
        MockServerHttpRequest.get(HTTP_LOCALHOST_8090_V_1_USUARIOS_PERFIL)
            .remoteAddress(address)
            .build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.increment(eq(RATE_LIMIT_192_168_1_100))).thenReturn(Mono.just(101L));

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    verify(chain, never()).filter(any());
  }

  @Test
  @DisplayName("Debe incrementar contador en Redis por IP correctamente")
  void debeIncrementarContadorEnRedisPorIP() {
    // Arrange
    InetSocketAddress address = new InetSocketAddress("10.0.0.5", 8080);
    MockServerHttpRequest request =
        MockServerHttpRequest.get(HTTP_LOCALHOST_8090_V_1_USUARIOS_PERFIL)
            .remoteAddress(address)
            .build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.increment(eq(RATE_LIMIT_10_0_0_5))).thenReturn(Mono.just(1L));
    when(redisTemplate.expire(eq(RATE_LIMIT_10_0_0_5), any(Duration.class)))
        .thenReturn(Mono.just(true));
    when(chain.filter(exchange)).thenReturn(Mono.empty());

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    verify(valueOps, times(1)).increment(RATE_LIMIT_10_0_0_5);
  }

  @Test
  @DisplayName("Debe respetar TTL de 1 minuto en Redis")
  void debeRespetarTTLDeUnMinuto() {
    // Arrange
    InetSocketAddress address = new InetSocketAddress(DIRECCION_IP, 8080);
    MockServerHttpRequest request =
        MockServerHttpRequest.get(HTTP_LOCALHOST_8090_V_1_USUARIOS_PERFIL)
            .remoteAddress(address)
            .build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.increment(eq(RATE_LIMIT_192_168_1_100))).thenReturn(Mono.just(1L));
    when(redisTemplate.expire(eq(RATE_LIMIT_192_168_1_100), eq(Duration.ofMinutes(1))))
        .thenReturn(Mono.just(true));
    when(chain.filter(exchange)).thenReturn(Mono.empty());

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    verify(redisTemplate, times(1)).expire(eq(RATE_LIMIT_192_168_1_100), eq(Duration.ofMinutes(1)));
  }

  @Test
  @DisplayName("Debe manejar múltiples IPs concurrentemente")
  void debeManejarMultiplesIPsConcurrentemente() {
    // Arrange IP 1
    InetSocketAddress address1 = new InetSocketAddress(DIRECCION_IP, 8080);
    MockServerHttpRequest request1 =
        MockServerHttpRequest.get(HTTP_LOCALHOST_8090_V_1_USUARIOS_PERFIL)
            .remoteAddress(address1)
            .build();
    ServerWebExchange exchange1 = MockServerWebExchange.from(request1);

    // Arrange IP 2
    InetSocketAddress address2 = new InetSocketAddress("192.168.1.200", 8080);
    MockServerHttpRequest request2 =
        MockServerHttpRequest.get(HTTP_LOCALHOST_8090_V_1_USUARIOS_PERFIL)
            .remoteAddress(address2)
            .build();
    ServerWebExchange exchange2 = MockServerWebExchange.from(request2);

    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.increment(eq(RATE_LIMIT_192_168_1_100))).thenReturn(Mono.just(1L));
    when(valueOps.increment(eq("rate_limit:192.168.1.200"))).thenReturn(Mono.just(1L));
    when(redisTemplate.expire(anyString(), any(Duration.class))).thenReturn(Mono.just(true));
    when(chain.filter(any())).thenReturn(Mono.empty());

    // Act
    Mono<Void> result1 = filter.filter(exchange1, chain);
    Mono<Void> result2 = filter.filter(exchange2, chain);

    // Assert
    StepVerifier.create(result1).verifyComplete();
    StepVerifier.create(result2).verifyComplete();

    verify(valueOps, times(1)).increment(RATE_LIMIT_192_168_1_100);
    verify(valueOps, times(1)).increment("rate_limit:192.168.1.200");
    verify(chain, times(2)).filter(any());
  }

  @Test
  @DisplayName("Debe manejar error de conexión a Redis gracefully (fail-open)")
  void debeManejarErrorDeConexionARedis() {
    // Arrange
    InetSocketAddress address = new InetSocketAddress(DIRECCION_IP, 8080);
    MockServerHttpRequest request =
        MockServerHttpRequest.get(HTTP_LOCALHOST_8090_V_1_USUARIOS_PERFIL)
            .remoteAddress(address)
            .build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.increment(anyString()))
        .thenReturn(Mono.error(new RuntimeException("Redis connection failed")));
    when(chain.filter(exchange)).thenReturn(Mono.empty());

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert - debe permitir la petición aunque Redis falle
    StepVerifier.create(result).verifyComplete();
    verify(chain, times(1)).filter(exchange);
  }

  @Test
  @DisplayName("Debe manejar IP nula o unknown")
  void debeManejarIPNulaOUnknown() {
    // Arrange - sin remote address
    MockServerHttpRequest request =
        MockServerHttpRequest.get(HTTP_LOCALHOST_8090_V_1_USUARIOS_PERFIL).build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.increment(eq(RATE_LIMIT_UNKNOWN))).thenReturn(Mono.just(1L));
    when(redisTemplate.expire(eq(RATE_LIMIT_UNKNOWN), any(Duration.class)))
        .thenReturn(Mono.just(true));
    when(chain.filter(exchange)).thenReturn(Mono.empty());

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    verify(valueOps, times(1)).increment(RATE_LIMIT_UNKNOWN);
  }

  @Test
  @DisplayName("Debe permitir petición 100 (límite exacto)")
  void debePermitirPeticionEnLimiteExacto() {
    // Arrange
    InetSocketAddress address = new InetSocketAddress(DIRECCION_IP, 8080);
    MockServerHttpRequest request =
        MockServerHttpRequest.get(HTTP_LOCALHOST_8090_V_1_USUARIOS_PERFIL)
            .remoteAddress(address)
            .build();
    ServerWebExchange exchange = MockServerWebExchange.from(request);

    when(redisTemplate.opsForValue()).thenReturn(valueOps);
    when(valueOps.increment(eq(RATE_LIMIT_192_168_1_100))).thenReturn(Mono.just(100L));
    when(chain.filter(exchange)).thenReturn(Mono.empty());

    // Act
    Mono<Void> result = filter.filter(exchange, chain);

    // Assert
    StepVerifier.create(result).verifyComplete();
    verify(chain, times(1)).filter(exchange);
    assertThat(exchange.getResponse().getStatusCode()).isNotEqualTo(HttpStatus.TOO_MANY_REQUESTS);
  }

  @Test
  @DisplayName("Debe tener orden de ejecución -50")
  void debeTenerOrdenMenosCincuenta() {
    assertThat(filter.getOrder()).isEqualTo(-50);
  }
}
