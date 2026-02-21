package com.gamelisto.api_gateway.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

  private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

  private static final String RATE_LIMIT_PREFIX = "rate_limit:";
  private static final int MAX_REQUESTS = 100; // Máximo de peticiones
  private static final Duration WINDOW = Duration.ofMinutes(1); // Ventana de tiempo

  private final ReactiveRedisTemplate<String, String> redisTemplate;

  public RateLimitFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // Obtener IP del cliente
    String clientIp =
        exchange.getRequest().getRemoteAddress() != null
            ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
            : "unknown";

    String key = RATE_LIMIT_PREFIX + clientIp;

    return redisTemplate
        .opsForValue()
        .increment(key)
        .flatMap(
            count -> {
              if (count == 1) {
                // Primera petición: establecer TTL
                return redisTemplate.expire(key, WINDOW).then(chain.filter(exchange));
              } else if (count > MAX_REQUESTS) {
                // Límite excedido
                logger.warn("Rate limit excedido para IP: {}. Peticiones: {}", clientIp, count);
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
              } else {
                // Dentro del límite
                return chain.filter(exchange);
              }
            })
        .onErrorResume(
            e -> {
              // Si Redis falla, permitir la petición (fail-open)
              logger.error(
                  "Error en rate limiting (Redis): {}. Permitiendo petición.", e.getMessage());
              return chain.filter(exchange);
            });
  }

  @Override
  public int getOrder() {
    // Ejecutar después del filtro JWT pero antes del enrutamiento
    return -50;
  }
}
