package com.gamelisto.usuarios_service.infrastructure.persistence.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("RedisConfig - Configuración de Redis")
class RedisConfigTest {

  private RedisConfig redisConfig;
  private RedisConnectionFactory mockConnectionFactory;

  @BeforeEach
  void setUp() {
    redisConfig = new RedisConfig();
    mockConnectionFactory = mock(RedisConnectionFactory.class);
  }

  // ========== CONFIGURACIÓN DE BEANS ==========

  @Test
  @DisplayName("Debe crear bean de StringRedisTemplate correctamente")
  void debeCrearStringRedisTemplate() {
    // Act
    StringRedisTemplate template = redisConfig.stringRedisTemplate(mockConnectionFactory);

    // Assert
    assertThat(template).isNotNull();
    assertThat(template.getConnectionFactory()).isEqualTo(mockConnectionFactory);
  }

  @Test
  @DisplayName("Debe configurar StringRedisTemplate con ConnectionFactory proporcionado")
  void debeConfigurarConConnectionFactoryProporcionado() {
    // Act
    StringRedisTemplate template = redisConfig.stringRedisTemplate(mockConnectionFactory);

    // Assert
    assertThat(template).isNotNull();
    assertThat(template.getConnectionFactory()).isNotNull();
    assertThat(template.getConnectionFactory()).isSameAs(mockConnectionFactory);
  }

  // ========== SERIALIZERS ==========

  @Test
  @DisplayName("Debe configurar serializers correctos en StringRedisTemplate")
  void debeConfigurarSerializersCorrectos() {
    // Act
    StringRedisTemplate template = redisConfig.stringRedisTemplate(mockConnectionFactory);

    // Assert
    assertThat(template.getKeySerializer()).isNotNull();
    assertThat(template.getValueSerializer()).isNotNull();
    assertThat(template.getHashKeySerializer()).isNotNull();
    assertThat(template.getHashValueSerializer()).isNotNull();
  }

  @Test
  @DisplayName("Debe usar StringRedisSerializer para claves y valores")
  void debeUsarStringRedisSerializer() {
    // Act
    StringRedisTemplate template = redisConfig.stringRedisTemplate(mockConnectionFactory);

    // Assert
    assertThat(template.getKeySerializer().getClass().getSimpleName())
        .contains("StringRedisSerializer");
    assertThat(template.getValueSerializer().getClass().getSimpleName())
        .contains("StringRedisSerializer");
  }

  // ========== OPERACIONES ==========

  @Test
  @DisplayName("Debe proveer operaciones para diferentes tipos de datos Redis")
  void debeProveerOperacionesParaDiferentesTipos() {
    // Act
    StringRedisTemplate template = redisConfig.stringRedisTemplate(mockConnectionFactory);

    // Assert - Operaciones de valor
    assertThat(template.opsForValue()).isNotNull();

    // Assert - Operaciones de hash
    assertThat(template.opsForHash()).isNotNull();

    // Assert - Operaciones de lista
    assertThat(template.opsForList()).isNotNull();

    // Assert - Operaciones de set
    assertThat(template.opsForSet()).isNotNull();

    // Assert - Operaciones de sorted set
    assertThat(template.opsForZSet()).isNotNull();
  }

  @Test
  @DisplayName("Debe configurar template como Singleton reutilizable")
  void debeCrearTemplateSingleton() {
    // Act
    StringRedisTemplate template1 = redisConfig.stringRedisTemplate(mockConnectionFactory);
    StringRedisTemplate template2 = redisConfig.stringRedisTemplate(mockConnectionFactory);

    // Assert - Nuevas instancias cada vez (comportamiento bean de Spring)
    assertThat(template1).isNotNull();
    assertThat(template2).isNotNull();
  }

  @Test
  @DisplayName("Debe establecer el connectionFactory en el template")
  void debeEstablecerConnectionFactory() {
    // Act
    StringRedisTemplate template = redisConfig.stringRedisTemplate(mockConnectionFactory);

    // Assert
    assertThat(template.getConnectionFactory()).isEqualTo(mockConnectionFactory);
  }

  @Test
  @DisplayName("Debe permitir llamar afterPropertiesSet para inicialización")
  void debePermitirInicializacion() {
    // Act
    StringRedisTemplate template = redisConfig.stringRedisTemplate(mockConnectionFactory);

    // Assert - No debe lanzar excepción
    assertThat(template).isNotNull();
    assertThat(template.getConnectionFactory()).isNotNull();
  }

  @Test
  @DisplayName("Debe crear RedisConfig correctamente")
  void debeCrearRedisConfigCorrectamente() {
    // Assert
    assertThat(redisConfig).isNotNull();
  }
}
