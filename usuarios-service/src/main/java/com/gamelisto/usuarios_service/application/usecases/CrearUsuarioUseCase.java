package com.gamelisto.usuarios_service.application.usecases;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//import com.gamelisto.usuarios_service.application.IEventosPublisher;
import com.gamelisto.usuarios_service.application.dto.CrearUsuarioCommand;
import com.gamelisto.usuarios_service.application.dto.UsuarioDTO;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Email;
import com.gamelisto.usuarios_service.domain.usuario.PasswordHash;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.Username;
import com.gamelisto.usuarios_service.domain.exceptions.EmailYaRegistradoException;
import com.gamelisto.usuarios_service.domain.exceptions.UsernameYaExisteException;

@Service
public class CrearUsuarioUseCase {
    
    private final RepositorioUsuarios repositorioUsuarios;
    // private final IEventosPublisher eventosPublisher;
    private final PasswordEncoder passwordEncoder;

    public CrearUsuarioUseCase(
            RepositorioUsuarios repositorioUsuarios,
            PasswordEncoder passwordEncoder) {
        this.repositorioUsuarios = repositorioUsuarios;
        // this.eventosPublisher = eventosPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioDTO execute(CrearUsuarioCommand command) {
        Username username = Username.of(command.username());
        Email email = Email.of(command.email());

        if (repositorioUsuarios.existsByUsername(username)) {
            throw new UsernameYaExisteException(command.username());
        }
        
        if (repositorioUsuarios.existsByEmail(email)) {
            throw new EmailYaRegistradoException(command.email());
        }

        String hashedPassword = passwordEncoder.encode(command.password());
        PasswordHash passwordHash = PasswordHash.of(hashedPassword);
        
        Usuario usuario = Usuario.create(username, email, passwordHash);
        
        Usuario usuarioGuardado = repositorioUsuarios.save(usuario);
        
        // TODO: Implementar envío de email de verificación
        // El token de verificación ya fue generado en Usuario.create()
        //
        // emailService.sendVerificationEmail(
        //     usuarioGuardado.getEmail().value(),
        //     usuarioGuardado.getTokenVerificacion().value()
        // );
        //
        // Contenido email:
        // https://gamelisto.com/verify-email?token={token}
        
        // eventosPublisher.publish(new UsuarioCreadoEvent(
        //     usuarioGuardado.getId().value().toString(),
        //     usuarioGuardado.getUsername().value(),
        //     usuarioGuardado.getEmail().value()
        // ));
        
        return UsuarioDTO.from(usuarioGuardado);
    }
    
}
