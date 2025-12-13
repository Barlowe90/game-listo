package com.gamelisto.usuarios_service.domain.usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class UsuarioTest {
    
    // ========== FACTORY METHODS ==========
    
    @Test
    @DisplayName("Debe crear nuevo usuario con factory method create()")
    void debeCrearNuevoUsuarioConCreate() {
        // Arrange
        Username username = Username.of("jugador123");
        Email email = Email.of("jugador@test.com");
        PasswordHash passwordHash = PasswordHash.of("$2a$10$hashed");
        
        // Act
        Usuario usuario = Usuario.create(username, email, passwordHash);
        
        // Assert
        assertNotNull(usuario.getId());
        assertEquals("jugador123", usuario.getUsername().value());
        assertEquals("jugador@test.com", usuario.getEmail().value());
        assertEquals(EstadoUsuario.PENDIENTE_DE_VERIFICACION, usuario.getStatus());
        assertEquals(Rol.USER, usuario.getRole());
        assertEquals(Idioma.ESP, usuario.getLanguage());
        assertTrue(usuario.isNotificationsActive());
        assertNotNull(usuario.getCreatedAt());
        assertNotNull(usuario.getUpdatedAt());
        // updatedAt puede ser ligeramente posterior debido a generarTokenVerificacion()
        assertTrue(usuario.getUpdatedAt().compareTo(usuario.getCreatedAt()) >= 0);
        assertTrue(usuario.getAvatar().isEmpty());
        assertTrue(usuario.getDiscordUserId().isEmpty());
        assertTrue(usuario.getDiscordUsername().isEmpty());
        assertNull(usuario.getDiscordLinkedAt());
        assertFalse(usuario.isDiscordConsent());
    }
    
    @Test
    @DisplayName("Debe reconstituir usuario desde persistencia con reconstitute()")
    void debeReconstituirUsuarioConReconstitute() {
        // Arrange
        UsuarioId id = UsuarioId.generate();
        Username username = Username.of("testuser");
        Email email = Email.of("test@test.com");
        PasswordHash passwordHash = PasswordHash.of("$2a$10$hash");
        Avatar avatar = Avatar.of("https://example.com/avatar.jpg");
        Instant createdAt = Instant.parse("2025-01-01T00:00:00Z");
        Instant updatedAt = Instant.parse("2025-01-02T00:00:00Z");
        Rol role = Rol.ADMIN;
        Idioma language = Idioma.ENG;
        boolean notificationsActive = false;
        EstadoUsuario status = EstadoUsuario.ACTIVO;
        DiscordUserId discordUserId = DiscordUserId.of("123456");
        DiscordUsername discordUsername = DiscordUsername.of("player#1234");
        Instant discordLinkedAt = Instant.parse("2025-01-01T12:00:00Z");
        boolean discordConsent = true;
        
        // Act
        Usuario usuario = Usuario.reconstitute(
            id, username, email, passwordHash, avatar, createdAt, updatedAt,
            role, language, notificationsActive, status, discordUserId,
            discordUsername, discordLinkedAt, discordConsent,
            TokenVerificacion.empty(), null,
            TokenVerificacion.empty(), null
        );
        
        // Assert
        assertEquals(id.value(), usuario.getId().value());
        assertEquals("testuser", usuario.getUsername().value());
        assertEquals("test@test.com", usuario.getEmail().value());
        assertEquals("https://example.com/avatar.jpg", usuario.getAvatar().url());
        assertEquals(createdAt, usuario.getCreatedAt());
        assertEquals(updatedAt, usuario.getUpdatedAt());
        assertEquals(Rol.ADMIN, usuario.getRole());
        assertEquals(Idioma.ENG, usuario.getLanguage());
        assertFalse(usuario.isNotificationsActive());
        assertEquals(EstadoUsuario.ACTIVO, usuario.getStatus());
        assertEquals("123456", usuario.getDiscordUserId().value());
        assertEquals("player#1234", usuario.getDiscordUsername().value());
        assertEquals(discordLinkedAt, usuario.getDiscordLinkedAt());
        assertTrue(usuario.isDiscordConsent());
    }
    
    // ========== VALIDACIÓN DE INVARIANTES ==========
    
    @Test
    @DisplayName("Debe lanzar excepción si username es nulo")
    void debeLanzarExcepcionSiUsernameEsNulo() {
        // Arrange
        Email email = Email.of("test@test.com");
        PasswordHash passwordHash = PasswordHash.of("$2a$10$hash");
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Usuario.create(null, email, passwordHash)
        );
        
        assertTrue(exception.getMessage().contains("username es obligatorio"));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si email es nulo")
    void debeLanzarExcepcionSiEmailEsNulo() {
        // Arrange
        Username username = Username.of("testuser");
        PasswordHash passwordHash = PasswordHash.of("$2a$10$hash");
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Usuario.create(username, null, passwordHash)
        );
        
        assertTrue(exception.getMessage().contains("email es obligatorio"));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción si passwordHash es nulo")
    void debeLanzarExcepcionSiPasswordHashEsNulo() {
        // Arrange
        Username username = Username.of("testuser");
        Email email = Email.of("test@test.com");
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Usuario.create(username, email, null)
        );
        
        assertTrue(exception.getMessage().contains("password hash es obligatorio"));
    }
    
    // ========== CAMBIO DE USERNAME ==========
    
    @Test
    @DisplayName("Debe cambiar username y actualizar timestamp")
    void debeCambiarUsernameYActualizarTimestamp() throws InterruptedException {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        Instant updatedAtAntes = usuario.getUpdatedAt();
        Thread.sleep(10); // Pequeña espera para asegurar diferencia en timestamp
        Username nuevoUsername = Username.of("nuevoNombre");
        
        // Act
        usuario.changeUsername(nuevoUsername);
        
        // Assert
        assertEquals("nuevoNombre", usuario.getUsername().value());
        assertTrue(usuario.getUpdatedAt().isAfter(updatedAtAntes));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción al cambiar username a nulo")
    void debeLanzarExcepcionAlCambiarUsernameANulo() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> usuario.changeUsername(null));
    }
    
    // ========== CAMBIO DE EMAIL ==========
    
    @Test
    @DisplayName("Debe cambiar email y actualizar timestamp")
    void debeCambiarEmailYActualizarTimestamp() throws InterruptedException {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        Instant updatedAtAntes = usuario.getUpdatedAt();
        Thread.sleep(10);
        Email nuevoEmail = Email.of("nuevo@test.com");
        
        // Act
        usuario.changeEmail(nuevoEmail);
        
        // Assert
        assertEquals("nuevo@test.com", usuario.getEmail().value());
        assertTrue(usuario.getUpdatedAt().isAfter(updatedAtAntes));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción al cambiar email a nulo")
    void debeLanzarExcepcionAlCambiarEmailANulo() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> usuario.changeEmail(null));
    }
    
    // ========== CAMBIO DE PASSWORD ==========
    
    @Test
    @DisplayName("Debe cambiar password hash y actualizar timestamp")
    void debeCambiarPasswordHashYActualizarTimestamp() throws InterruptedException {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        Instant updatedAtAntes = usuario.getUpdatedAt();
        Thread.sleep(10);
        PasswordHash nuevoHash = PasswordHash.of("$2a$10$newHash");
        
        // Act
        usuario.changePasswordHash(nuevoHash);
        
        // Assert
        assertEquals("$2a$10$newHash", usuario.getPasswordHash().value());
        assertTrue(usuario.getUpdatedAt().isAfter(updatedAtAntes));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción al cambiar password hash a nulo")
    void debeLanzarExcepcionAlCambiarPasswordHashANulo() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> usuario.changePasswordHash(null));
    }
    
    // ========== CAMBIO DE AVATAR ==========
    
    @Test
    @DisplayName("Debe cambiar avatar y actualizar timestamp")
    void debeCambiarAvatarYActualizarTimestamp() throws InterruptedException {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        Instant updatedAtAntes = usuario.getUpdatedAt();
        Thread.sleep(10);
        Avatar nuevoAvatar = Avatar.of("https://example.com/new-avatar.jpg");
        
        // Act
        usuario.changeAvatar(nuevoAvatar);
        
        // Assert
        assertEquals("https://example.com/new-avatar.jpg", usuario.getAvatar().url());
        assertTrue(usuario.getUpdatedAt().isAfter(updatedAtAntes));
    }
    
    @Test
    @DisplayName("Debe establecer avatar vacío si se pasa nulo")
    void debeEstablecerAvatarVacioSiEsNulo() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        
        // Act
        usuario.changeAvatar(null);
        
        // Assert
        assertTrue(usuario.getAvatar().isEmpty());
    }
    
    // ========== CAMBIO DE IDIOMA ==========
    
    @Test
    @DisplayName("Debe cambiar idioma y actualizar timestamp")
    void debeCambiarIdiomaYActualizarTimestamp() throws InterruptedException {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        Instant updatedAtAntes = usuario.getUpdatedAt();
        Thread.sleep(10);
        
        // Act
        usuario.changeLanguage(Idioma.ENG);
        
        // Assert
        assertEquals(Idioma.ENG, usuario.getLanguage());
        assertTrue(usuario.getUpdatedAt().isAfter(updatedAtAntes));
    }
    
    @Test
    @DisplayName("Debe establecer idioma por defecto (ESP) si se pasa nulo")
    void debeEstablecerIdiomaPorDefectoSiEsNulo() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        
        // Act
        usuario.changeLanguage(null);
        
        // Assert
        assertEquals(Idioma.ESP, usuario.getLanguage());
    }
    
    // ========== NOTIFICACIONES ==========
    
    @Test
    @DisplayName("Debe habilitar notificaciones")
    void debeHabilitarNotificaciones() throws InterruptedException {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        usuario.disableNotifications();
        Instant updatedAtAntes = usuario.getUpdatedAt();
        Thread.sleep(10);
        
        // Act
        usuario.enableNotifications();
        
        // Assert
        assertTrue(usuario.isNotificationsActive());
        assertTrue(usuario.getUpdatedAt().isAfter(updatedAtAntes));
    }
    
    @Test
    @DisplayName("Debe deshabilitar notificaciones")
    void debeDeshabilitarNotificaciones() throws InterruptedException {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        Instant updatedAtAntes = usuario.getUpdatedAt();
        Thread.sleep(10);
        
        // Act
        usuario.disableNotifications();
        
        // Assert
        assertFalse(usuario.isNotificationsActive());
        assertTrue(usuario.getUpdatedAt().isAfter(updatedAtAntes));
    }
    
    // ========== GESTIÓN DE ESTADO ==========
    
    @Test
    @DisplayName("Debe suspender usuario")
    void debeSuspenderUsuario() throws InterruptedException {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        Instant updatedAtAntes = usuario.getUpdatedAt();
        Thread.sleep(10);
        
        // Act
        usuario.suspend();
        
        // Assert
        assertEquals(EstadoUsuario.SUSPENDIDO, usuario.getStatus());
        assertTrue(usuario.isSuspended());
        assertFalse(usuario.isActive());
        assertTrue(usuario.getUpdatedAt().isAfter(updatedAtAntes));
    }
    
    @Test
    @DisplayName("Debe activar usuario suspendido")
    void debeActivarUsuarioSuspendido() throws InterruptedException {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        usuario.suspend();
        Instant updatedAtAntes = usuario.getUpdatedAt();
        Thread.sleep(10);
        
        // Act
        usuario.activate();
        
        // Assert
        assertEquals(EstadoUsuario.ACTIVO, usuario.getStatus());
        assertTrue(usuario.isActive());
        assertFalse(usuario.isSuspended());
        assertTrue(usuario.getUpdatedAt().isAfter(updatedAtAntes));
    }
    
    @Test
    @DisplayName("Debe activar usuario pendiente de verificación")
    void debeActivarUsuarioPendienteDeVerificacion() {
        // Arrange
        Usuario usuario = Usuario.create(
            Username.of("test"),
            Email.of("test@test.com"),
            PasswordHash.of("$2a$10$hash")
        );
        
        // Act
        usuario.activate();
        
        // Assert
        assertEquals(EstadoUsuario.ACTIVO, usuario.getStatus());
        assertTrue(usuario.isActive());
    }
    
    @Test
    @DisplayName("Debe lanzar excepción al intentar activar usuario eliminado")
    void debeLanzarExcepcionAlActivarUsuarioEliminado() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        usuario.delete();
        
        // Act & Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> usuario.activate()
        );
        
        assertTrue(exception.getMessage().contains("No se puede activar un usuario eliminado"));
    }
    
    // ========== DISCORD ==========
    
    @Test
    @DisplayName("Debe vincular cuenta de Discord")
    void debeVincularCuentaDeDiscord() throws InterruptedException {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        DiscordUserId discordId = DiscordUserId.of("123456789");
        DiscordUsername discordUsername = DiscordUsername.of("player#1234");
        Instant beforeLink = Instant.now();
        Thread.sleep(10);
        
        // Act
        usuario.linkDiscord(discordId, discordUsername);
        
        // Assert
        assertEquals("123456789", usuario.getDiscordUserId().value());
        assertEquals("player#1234", usuario.getDiscordUsername().value());
        assertNotNull(usuario.getDiscordLinkedAt());
        assertTrue(usuario.getDiscordLinkedAt().isAfter(beforeLink));
        assertTrue(usuario.isDiscordConsent());
        assertTrue(usuario.hasDiscordLinked());
    }
    
    @Test
    @DisplayName("Debe lanzar excepción al vincular Discord con ID nulo")
    void debeLanzarExcepcionAlVincularDiscordConIdNulo() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        DiscordUsername discordUsername = DiscordUsername.of("player#1234");
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> usuario.linkDiscord(null, discordUsername));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción al vincular Discord con ID vacío")
    void debeLanzarExcepcionAlVincularDiscordConIdVacio() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        DiscordUserId discordId = DiscordUserId.empty();
        DiscordUsername discordUsername = DiscordUsername.of("player#1234");
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> usuario.linkDiscord(discordId, discordUsername));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción al vincular Discord con username nulo")
    void debeLanzarExcepcionAlVincularDiscordConUsernameNulo() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        DiscordUserId discordId = DiscordUserId.of("123456789");
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> usuario.linkDiscord(discordId, null));
    }
    
    @Test
    @DisplayName("Debe lanzar excepción al vincular Discord con username vacío")
    void debeLanzarExcepcionAlVincularDiscordConUsernameVacio() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        DiscordUserId discordId = DiscordUserId.of("123456789");
        DiscordUsername discordUsername = DiscordUsername.empty();
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, 
            () -> usuario.linkDiscord(discordId, discordUsername));
    }
    
    @Test
    @DisplayName("Debe desvincular cuenta de Discord")
    void debeDesvincularCuentaDeDiscord() throws InterruptedException {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        DiscordUserId discordId = DiscordUserId.of("123456789");
        DiscordUsername discordUsername = DiscordUsername.of("player#1234");
        usuario.linkDiscord(discordId, discordUsername);
        Instant updatedAtAntes = usuario.getUpdatedAt();
        Thread.sleep(10);
        
        // Act
        usuario.unlinkDiscord();
        
        // Assert
        assertTrue(usuario.getDiscordUserId().isEmpty());
        assertTrue(usuario.getDiscordUsername().isEmpty());
        assertNull(usuario.getDiscordLinkedAt());
        assertFalse(usuario.isDiscordConsent());
        assertFalse(usuario.hasDiscordLinked());
        assertTrue(usuario.getUpdatedAt().isAfter(updatedAtAntes));
    }
    
    // ========== MÉTODOS DE CONSULTA ==========
    
    @Test
    @DisplayName("isActive() debe retornar true para usuario activo")
    void isActiveDebeRetornarTrueParaUsuarioActivo() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        usuario.activate();
        
        // Act & Assert
        assertTrue(usuario.isActive());
        assertFalse(usuario.isSuspended());
        assertFalse(usuario.isDeleted());
    }
    
    @Test
    @DisplayName("isSuspended() debe retornar true para usuario suspendido")
    void isSuspendedDebeRetornarTrueParaUsuarioSuspendido() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        usuario.suspend();
        
        // Act & Assert
        assertTrue(usuario.isSuspended());
        assertFalse(usuario.isActive());
        assertFalse(usuario.isDeleted());
    }
    
    @Test
    @DisplayName("isDeleted() debe retornar true para usuario eliminado")
    void isDeletedDebeRetornarTrueParaUsuarioEliminado() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        usuario.delete();
        
        // Act & Assert
        assertTrue(usuario.isDeleted());
        assertFalse(usuario.isActive());
        assertFalse(usuario.isSuspended());
    }
    
    @Test
    @DisplayName("hasDiscordLinked() debe retornar false sin vinculación")
    void hasDiscordLinkedDebeRetornarFalseSinVinculacion() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        
        // Act & Assert
        assertFalse(usuario.hasDiscordLinked());
    }
    
    @Test
    @DisplayName("hasDiscordLinked() debe retornar true con vinculación")
    void hasDiscordLinkedDebeRetornarTrueConVinculacion() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        usuario.linkDiscord(
            DiscordUserId.of("123456"),
            DiscordUsername.of("player#1234")
        );
        
        // Act & Assert
        assertTrue(usuario.hasDiscordLinked());
    }
    
    // ========== toString ==========
    
    @Test
    @DisplayName("toString debe incluir información básica del usuario")
    void toStringDebeIncluirInformacionBasica() {
        // Arrange
        Usuario usuario = crearUsuarioDefault();
        
        // Act
        String resultado = usuario.toString();
        
        // Assert
        assertTrue(resultado.contains("Usuario"));
        assertTrue(resultado.contains(usuario.getId().toString()));
        assertTrue(resultado.contains(usuario.getUsername().value()));
        assertTrue(resultado.contains(usuario.getEmail().value()));
        assertTrue(resultado.contains(usuario.getStatus().toString()));
    }
    
    // ========== HELPER METHOD ==========
    
    private Usuario crearUsuarioDefault() {
        Usuario usuario = Usuario.create(
            Username.of("testuser"),
            Email.of("test@test.com"),
            PasswordHash.of("$2a$10$hash")
        );
        // Activar usuario para tests que requieren estado ACTIVO
        usuario.activate();
        return usuario;
    }
}
