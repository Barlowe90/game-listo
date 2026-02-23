package com.gamelisto.usuarios.infrastructure.out.persistence.redis;

import static org.assertj.core.api.Assertions.assertThat;

import com.gamelisto.usuarios.config.TestMessagingConfig;
import com.gamelisto.usuarios.domain.refreshtoken.Jti;
import com.gamelisto.usuarios.test.config.RedisTestContainerExtension;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Import({TestMessagingConfig.class, RepositorioJtiRevocadosRedis.class})
@ActiveProfiles("test")
@DisplayName("RepositorioJtiRevocadosRedis - Persistencia de JTIs revocados")
@ExtendWith(RedisTestContainerExtension.class)
class RepositorioJtiRevocadosRedisTest {

  @Autowired private RepositorioJtiRevocadosRedis repositorio;
  @Autowired private StringRedisTemplate redisTemplate;

  @BeforeEach
  void setUp() {
    // Limpiar todas las claves del patrón jti:revoked:*
    redisTemplate.delete(redisTemplate.keys("jti:revoked:*"));
  }

  @Test
  @DisplayName("Debe guardar JTI en Redis con clave 'jti:revoked:<JTI>'")
  void debeGuardarJtiEnRedis() {
    Jti jti = Jti.generate();
    Duration ttl = Duration.ofMinutes(15);

    repositorio.revocar(jti, ttl);

    String key = "jti:revoked:" + jti.value();
    assertThat(redisTemplate.hasKey(key)).isTrue();
  }

  @Test
  @DisplayName("Debe establecer TTL correcto (15 minutos)")
  void debeEstablecerTTL() {
    Jti jti = Jti.generate();
    Duration ttl = Duration.ofMinutes(15);

    repositorio.revocar(jti, ttl);

    String key = "jti:revoked:" + jti.value();
    Long ttlSeconds = redisTemplate.getExpire(key);
    assertThat(ttlSeconds).isGreaterThan(890L).isLessThanOrEqualTo(900L);
  }

  @Test
  @DisplayName("Debe verificar existencia de JTI revocado")
  void debeVerificarExistencia() {
    Jti jti = Jti.generate();
    repositorio.revocar(jti, Duration.ofMinutes(15));

    assertThat(repositorio.estaRevocado(jti)).isTrue();
  }

  @Test
  @DisplayName("Debe retornar false si JTI no existe")
  void debeRetornarFalseSiNoExiste() {
    Jti jti = Jti.generate();
    assertThat(repositorio.estaRevocado(jti)).isFalse();
  }

  @Test
  @DisplayName("Debe manejar JTIs con caracteres especiales (UUID)")
  void debeManejarJtisUUID() {
    Jti jti = Jti.generate();
    repositorio.revocar(jti, Duration.ofMinutes(10));

    assertThat(repositorio.estaRevocado(jti)).isTrue();
  }
}
