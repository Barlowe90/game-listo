package com.gamelisto.usuarios_service.domain.usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class PasswordHashTest {
    
    @Test
    @DisplayName("Debe crear password hash válido")
    void debeCrearPasswordHashValido() {
        // Arrange & Act
        PasswordHash passwordHash = PasswordHash.of("$2a$10$abcdefghijklmnopqrstuvwxyz");
        
        // Assert
        assertNotNull(passwordHash);
        assertEquals("$2a$10$abcdefghijklmnopqrstuvwxyz", passwordHash.value());
    }
    
    @Test
    @DisplayName("Debe crear password hash con formato BCrypt")
    void debeCrearPasswordHashConFormatoBCrypt() {
        // Arrange - Simular hash real de BCrypt
        String bcryptHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        
        // Act
        PasswordHash passwordHash = PasswordHash.of(bcryptHash);
        
        // Assert
        assertEquals(bcryptHash, passwordHash.value());
    }
    
    @Test
    @DisplayName("Debe preservar el valor exacto del hash")
    void debePreservarValorExactoDelHash() {
        // Arrange
        String hash = "$2y$12$someHashValueWithSpecialChars!@#";
        
        // Act
        PasswordHash passwordHash = PasswordHash.of(hash);
        
        // Assert
        assertEquals(hash, passwordHash.value());
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si el hash es nulo")
    void debeLanzarExcepcionSiHashEsNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> PasswordHash.of(null)
        );
        
        assertTrue(exception.getMessage().contains("no puede ser nulo"));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si el hash es vacío")
    void debeLanzarExcepcionSiHashEsVacio() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> PasswordHash.of(""));
        assertThrows(IllegalArgumentException.class, () -> PasswordHash.of("   "));
    }
    
    @Test
    @DisplayName("toString debe retornar [PROTECTED] para no exponer el hash")
    void toStringDebeOcultarHash() {
        // Arrange
        PasswordHash passwordHash = PasswordHash.of("$2a$10$someSecretHash");
        
        // Act
        String result = passwordHash.toString();
        
        // Assert
        assertEquals("[PROTECTED]", result);
        assertNotEquals("$2a$10$someSecretHash", result);
    }
    
    @Test
    @DisplayName("Debe preservar espacios en el hash (no hace trim)")
    void debePreservarEspaciosEnElHash() {
        // Arrange - La implementación NO hace trim al guardar
        String hashConEspacios = "  $2a$10$hash  ";
        
        // Act
        PasswordHash passwordHash = PasswordHash.of(hashConEspacios);
        
        // Assert - El valor se preserva tal cual (con espacios)
        assertEquals("  $2a$10$hash  ", passwordHash.value());
    }
}
