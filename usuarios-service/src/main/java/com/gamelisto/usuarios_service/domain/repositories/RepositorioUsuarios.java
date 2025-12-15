package com.gamelisto.usuarios_service.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.gamelisto.usuarios_service.domain.usuario.DiscordUserId;
import com.gamelisto.usuarios_service.domain.usuario.Email;
import com.gamelisto.usuarios_service.domain.usuario.EstadoUsuario;
import com.gamelisto.usuarios_service.domain.usuario.TokenVerificacion;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import com.gamelisto.usuarios_service.domain.usuario.Username;

public interface RepositorioUsuarios {
    
    Usuario save(Usuario usuario);
    
    Optional<Usuario> findById(UsuarioId id);
    
    Optional<Usuario> findByEmail(Email email);

    // Optional<Usuario> findByUsername(Username username); // no usado

    Optional<Usuario> findByDiscordUserId(DiscordUserId discordUserId);

    Optional<Usuario> findByTokenVerificacion(TokenVerificacion token);

    List<Usuario> findByStatus(EstadoUsuario status); // Para métricas: en el dashboard admin muestro cantidad total de usuarios por estado

    // List<Usuario> findUsersWithNotificationsEnabled(); // Para el envío de notificaciones
    
    boolean existsByUsername(Username username);

    boolean existsByEmail(Email email);
    
    // List<Usuario> searchByUsernameFragment(String fragment); // Para autocompletar en búsqueda de usuarios
    
    List<Usuario> findAll();
    
    // void delete(Usuario usuario);
}
