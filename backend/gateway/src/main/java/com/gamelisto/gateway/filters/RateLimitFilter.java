package com.gamelisto.gateway.filters;

import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RateLimitFilter implements GlobalFilter, Ordered {

  private static final Logger logger = LoggerFactory.getLogger(RateLimitFilter.class);

  private static final String RATE_LIMIT_PREFIX = "rate_limit:";
  private static final int MAX_REQUESTS = 300;
  private static final Duration WINDOW = Duration.ofMinutes(1);

  private final ReactiveRedisTemplate<String, String> redisTemplate;

  public RateLimitFilter(ReactiveRedisTemplate<String, String> redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    String path = exchange.getRequest().getURI().getPath();

    if (shouldSkipRateLimit(exchange, path)) {
      return chain.filter(exchange);
    }

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
                return redisTemplate.expire(key, WINDOW).then(chain.filter(exchange));
              }

              if (count > MAX_REQUESTS) {
                logger.warn(
                    "Rate limit excedido para IP: {}. Path: {}. Peticiones: {}",
                    clientIp,
                    path,
                    count);
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
              }

              return chain.filter(exchange);
            })
        .onErrorResume(
            error -> {
              logger.error(
                  "Error en rate limiting (Redis): {}. Permitiendo peticion.",
                  error.getMessage());
              return chain.filter(exchange);
            });
  }

  private boolean shouldSkipRateLimit(ServerWebExchange exchange, String path) {
    return exchange.getRequest().getMethod() == HttpMethod.OPTIONS
        || path.startsWith("/actuator/health")
        || path.startsWith("/v1/usuarios/auth/");
  }

  @Override
  public int getOrder() {
    return -50;
  }
}
