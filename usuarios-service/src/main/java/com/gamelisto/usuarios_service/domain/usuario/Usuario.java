package com.gamelisto.usuarios_service.domain.usuario;

import java.time.Instant;

public class Usuario {

    private static final int PASSWORD_RESET_TOKEN_TTL_SECONDS = 60 * 60;  // 1 hora
    private static final int EMAIL_VERIFICATION_TOKEN_TTL_SECONDS = 24 * 60 * 60; // 24 horas
    private final UsuarioId id; 
    private Username username; 
    private Email email; 
    private PasswordHash passwordHash; 
    private Avatar avatar; 
    private final Instant createdAt; 
    private Instant updatedAt; 
    private Rol role; 
    private Idioma language; 
    private boolean notificationsActive; 
    private EstadoUsuario status; 
    private DiscordUserId discordUserId; 
    private DiscordUsername discordUsername; 
    private Instant discordLinkedAt; 
    private boolean discordConsent;
    private TokenVerificacion tokenVerificacion;
    private Instant tokenVerificacionExpiracion;
    private TokenVerificacion tokenRestablecimiento;
    private Instant tokenRestablecimientoExpiracion;

    private Usuario(UsuarioId id, Username username, Email email, PasswordHash passwordHash, Avatar avatar, Instant createdAt, Rol role, Idioma language, EstadoUsuario status) {
        validarArgumentosCreacion(username, email, passwordHash, createdAt);
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.avatar = avatar != null ? avatar : Avatar.empty();
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.role = role != null ? role : Rol.USER;
        this.language = language != null ? language : Idioma.ESP;
        this.notificationsActive = true;
        this.status = status != null ? status : EstadoUsuario.ACTIVO;
        this.discordUserId = DiscordUserId.empty();
        this.discordUsername = DiscordUsername.empty();
        this.discordLinkedAt = null;
        this.discordConsent = false;
        this.tokenVerificacion = TokenVerificacion.empty();
        this.tokenVerificacionExpiracion = null;
        this.tokenRestablecimiento = TokenVerificacion.empty();
        this.tokenRestablecimientoExpiracion = null;
    }

    public static Usuario create(Username username, Email email, PasswordHash passwordHash) {
        Usuario usuario = new Usuario(UsuarioId.generate(), username, email, passwordHash, Avatar.empty(), Instant.now(), Rol.USER, Idioma.ESP, EstadoUsuario.PENDIENTE_DE_VERIFICACION);
        usuario.generarTokenVerificacion();
        return usuario;
    }

    public static Usuario reconstitute(UsuarioId id, Username username, Email email, PasswordHash passwordHash, Avatar avatar, Instant createdAt, Instant updatedAt, Rol role, Idioma language, boolean notificationsActive, EstadoUsuario status, DiscordUserId discordUserId, DiscordUsername discordUsername, Instant discordLinkedAt, boolean discordConsent, TokenVerificacion tokenVerificacion, Instant tokenVerificacionExpiracion, TokenVerificacion tokenRestablecimiento, Instant tokenRestablecimientoExpiracion) {
        Usuario usuario = new Usuario(id, username, email, passwordHash, avatar, createdAt, role, language, status);
        usuario.updatedAt = updatedAt != null ? updatedAt : createdAt;
        usuario.notificationsActive = notificationsActive;
        usuario.discordUserId = discordUserId != null ? discordUserId : DiscordUserId.empty();
        usuario.discordUsername = discordUsername != null ? discordUsername : DiscordUsername.empty();
        usuario.discordLinkedAt = discordLinkedAt;
        usuario.discordConsent = discordConsent;
        usuario.tokenVerificacion = tokenVerificacion != null ? tokenVerificacion : TokenVerificacion.empty();
        usuario.tokenVerificacionExpiracion = tokenVerificacionExpiracion;
        usuario.tokenRestablecimiento = tokenRestablecimiento != null ? tokenRestablecimiento : TokenVerificacion.empty();
        usuario.tokenRestablecimientoExpiracion = tokenRestablecimientoExpiracion;
        return usuario;
    }

    private void validarArgumentosCreacion(Username username, Email email, PasswordHash passwordHash, Instant createdAt) {
        if (username == null) {
            throw new IllegalArgumentException("El username es obligatorio");
        }
        if (email == null) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        if (passwordHash == null) {
            throw new IllegalArgumentException("El password hash es obligatorio");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("La fecha de creación es obligatoria");
        }
    }

    public void changeUsername(Username newUsername) {
        if (newUsername == null) {
            throw new IllegalArgumentException("El username no puede ser nulo");
        }
        this.username = newUsername;
        this.updatedAt = Instant.now();
    }

    public void changeEmail(Email newEmail) {
        if (newEmail == null) {
            throw new IllegalArgumentException("El email no puede ser nulo");
        }
        this.email = newEmail;
        this.updatedAt = Instant.now();
    }

    public void changePasswordHash(PasswordHash newPasswordHash) {
        if (newPasswordHash == null) {
            throw new IllegalArgumentException("El password hash no puede ser nulo");
        }
        this.passwordHash = newPasswordHash;
        this.updatedAt = Instant.now();
    }

    public void changeAvatar(Avatar newAvatar) {
        this.avatar = newAvatar != null ? newAvatar : Avatar.empty();
        this.updatedAt = Instant.now();
    }

    public void changeLanguage(Idioma newLanguage) {
        this.language = newLanguage != null ? newLanguage : Idioma.ESP;
        this.updatedAt = Instant.now();
    }

    public void enableNotifications() {
        this.notificationsActive = true;
        this.updatedAt = Instant.now();
    }

    public void disableNotifications() {
        this.notificationsActive = false;
        this.updatedAt = Instant.now();
    }

    public void suspend() {
        this.status = EstadoUsuario.SUSPENDIDO;
        this.updatedAt = Instant.now();
    }

    public void activate() {
        if (this.status == EstadoUsuario.ELIMINADO) {
            throw new IllegalStateException("No se puede activar un usuario eliminado");
        }
        this.status = EstadoUsuario.ACTIVO;
        this.updatedAt = Instant.now();
    }

    public void delete() {
        this.status = EstadoUsuario.ELIMINADO;
        this.updatedAt = Instant.now();
    }

    public void marcarPendienteVerificacion() {
        this.status = EstadoUsuario.PENDIENTE_DE_VERIFICACION;
        this.updatedAt = Instant.now();
    }

    public void generarTokenVerificacion() {
        this.tokenVerificacion = TokenVerificacion.generate();
        this.tokenVerificacionExpiracion = Instant.now().plusSeconds(EMAIL_VERIFICATION_TOKEN_TTL_SECONDS);
        this.updatedAt = Instant.now();
    }

    public void generarTokenRestablecimiento() {
        this.tokenRestablecimiento = TokenVerificacion.generate();
        this.tokenRestablecimientoExpiracion = Instant.now().plusSeconds(PASSWORD_RESET_TOKEN_TTL_SECONDS);
        this.updatedAt = Instant.now();
    }

    public boolean tieneTokenRestablecimientoValido(TokenVerificacion token) {
        if (this.tokenRestablecimiento == null || this.tokenRestablecimiento.isEmpty()) {
            return false;
        }
        if (this.tokenRestablecimientoExpiracion == null || Instant.now().isAfter(this.tokenRestablecimientoExpiracion)) {
            return false;
        }
        return this.tokenRestablecimiento.equals(token);
    }

    public void invalidarTokenRestablecimiento() {
        this.tokenRestablecimiento = TokenVerificacion.empty();
        this.tokenRestablecimientoExpiracion = null;
        this.updatedAt = Instant.now();
    }

    public void verificarEmail(TokenVerificacion token) {
        if (this.status != EstadoUsuario.PENDIENTE_DE_VERIFICACION) {
            throw new IllegalStateException("El usuario ya ha sido verificado");
        }
        if (this.tokenVerificacion == null || this.tokenVerificacion.isEmpty()) {
            throw new IllegalArgumentException("No hay token de verificación pendiente");
        }
        if (!this.tokenVerificacion.equals(token)) {
            throw new IllegalArgumentException("El token de verificación no es válido");
        }
        if (this.tokenVerificacionExpiracion == null || Instant.now().isAfter(this.tokenVerificacionExpiracion)) {
            throw new IllegalArgumentException("El token de verificación ha expirado");
        }
        
        this.status = EstadoUsuario.ACTIVO;
        this.tokenVerificacion = TokenVerificacion.empty();
        this.tokenVerificacionExpiracion = null;
        this.updatedAt = Instant.now();
    }

    public boolean isTokenVerificacionExpirado() {
        return this.tokenVerificacionExpiracion == null || Instant.now().isAfter(this.tokenVerificacionExpiracion);
    }

    public boolean isPendienteDeVerificacion() {
        return this.status == EstadoUsuario.PENDIENTE_DE_VERIFICACION;
    }

    public void linkDiscord(DiscordUserId discordUserId, DiscordUsername discordUsername) {
        if (discordUserId == null || discordUserId.isEmpty()) {
            throw new IllegalArgumentException("El ID de Discord no puede ser nulo o vacío");
        }
        if (discordUsername == null || discordUsername.isEmpty()) {
            throw new IllegalArgumentException("El username de Discord no puede ser nulo o vacío");
        }
        this.discordUserId = discordUserId;
        this.discordUsername = discordUsername;
        this.discordLinkedAt = Instant.now();
        this.discordConsent = true;
        this.updatedAt = Instant.now();
    }

    public void unlinkDiscord() {
        this.discordUserId = DiscordUserId.empty();
        this.discordUsername = DiscordUsername.empty();
        this.discordLinkedAt = null;
        this.discordConsent = false;
        this.updatedAt = Instant.now();
    }

    public boolean isActive() {
        return this.status == EstadoUsuario.ACTIVO;
    }

    public boolean isSuspended() {
        return this.status == EstadoUsuario.SUSPENDIDO;
    }

    public boolean isDeleted() {
        return this.status == EstadoUsuario.ELIMINADO;
    }

    public boolean hasDiscordLinked() {
        return !this.discordUserId.isEmpty() && !this.discordUsername.isEmpty();
    }

    // Getters
    public UsuarioId getId() {
        return id;
    }

    public Username getUsername() {
        return username;
    }

    public Email getEmail() {
        return email;
    }

    public PasswordHash getPasswordHash() {
        return passwordHash;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Rol getRole() {
        return role;
    }

    public Idioma getLanguage() {
        return language;
    }

    public boolean isNotificationsActive() {
        return notificationsActive;
    }

    public EstadoUsuario getStatus() {
        return status;
    }

    public DiscordUserId getDiscordUserId() {
        return discordUserId;
    }

    public DiscordUsername getDiscordUsername() {
        return discordUsername;
    }

    public Instant getDiscordLinkedAt() {
        return discordLinkedAt;
    }

    public boolean isDiscordConsent() {
        return discordConsent;
    }

    public TokenVerificacion getTokenVerificacion() {
        return tokenVerificacion;
    }

    public Instant getTokenVerificacionExpiracion() {
        return tokenVerificacionExpiracion;
    }

    public TokenVerificacion getTokenRestablecimiento() {
        return tokenRestablecimiento;
    }

    public Instant getTokenRestablecimientoExpiracion() {
        return tokenRestablecimientoExpiracion;
    }

    @Override
    public String toString() {
        return "Usuario{" + "id=" + id + ", username=" + username + ", email=" + email + ", status=" + status +'}';
    }
}
