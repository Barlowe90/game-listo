package com.gamelist.catalogo.shared.igdb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.function.Supplier;

/**
 * Manejador de rate limiting para la API de IGDB.
 *
 * <p>IGDB tiene límite de 4 peticiones por segundo. Cuando se excede, retorna HTTP 429. Este
 * handler implementa exponential backoff para reintentar las peticiones.
 */
@Component
@Slf4j
public class IgdbRateLimitHandler {

  private static final int MAX_RETRIES = 3;
  private static final long INITIAL_BACKOFF_MS = 1000L; // 1 segundo

  /**
   * Ejecuta una operación con reintentos automáticos en caso de rate limit.
   *
   * @param operation Operación a ejecutar (petición HTTP)
   * @param <T> Tipo de retorno de la operación
   * @return Resultado de la operación
   * @throws RuntimeException si se alcanza el máximo de reintentos
   */
  public <T> T executeWithRetry(Supplier<T> operation) {
    int attempt = 0;

    while (attempt < MAX_RETRIES) {
      try {
        return operation.get();

      } catch (WebClientResponseException e) {
        // Verificar si es error de rate limit (429 Too Many Requests)
        if (e.getStatusCode().value() == 429) {
          attempt++;

          if (attempt >= MAX_RETRIES) {
            log.error(
                "Máximo de reintentos alcanzado para IGDB API después de {} intentos", MAX_RETRIES);
            throw new RuntimeException("Máximo de reintentos alcanzado para IGDB API", e);
          }

          // Calcular backoff exponencial: 1s, 2s, 4s
          long backoffMs = INITIAL_BACKOFF_MS * (long) Math.pow(2, attempt - 1);

          log.warn(
              "Rate limit alcanzado (HTTP 429). Reintentando en {}ms (intento {}/{})",
              backoffMs,
              attempt,
              MAX_RETRIES);

          try {
            Thread.sleep(backoffMs);
          } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupción durante rate limit backoff", ie);
          }

        } else {
          // Si no es rate limit, propagar la excepción
          log.error("Error en petición IGDB: HTTP {}", e.getStatusCode().value(), e);
          throw e;
        }
      }
    }

    // No debería llegar aquí, pero por si acaso
    throw new RuntimeException("Error inesperado en executeWithRetry");
  }
}
