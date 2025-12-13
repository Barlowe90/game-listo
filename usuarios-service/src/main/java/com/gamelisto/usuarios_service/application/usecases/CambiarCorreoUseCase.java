package com.gamelisto.usuarios_service.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.dto.CambiarCorreoCommand;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Email;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import com.gamelisto.usuarios_service.domain.exceptions.EmailYaRegistradoException;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;

@Service
public class CambiarCorreoUseCase {
    
    private final RepositorioUsuarios repositorioUsuarios;

    public CambiarCorreoUseCase(RepositorioUsuarios repositorioUsuarios) {
        this.repositorioUsuarios = repositorioUsuarios;
    }

    @Transactional
    public void execute(CambiarCorreoCommand command) {
        Email nuevoEmail = Email.of(command.email());
        UsuarioId id = UsuarioId.fromString(command.usuarioId());

        Usuario usuario = repositorioUsuarios
            .findById(id)
            .orElseThrow(() -> new UsuarioNoEncontradoException(command.usuarioId()));

        // Validar que el nuevo email no esté ya registrado por otro usuario
        repositorioUsuarios.findByEmail(nuevoEmail)
            .filter(u -> !u.getId().equals(id))
            .ifPresent(u -> {
                throw new EmailYaRegistradoException(command.email());
            });

        // Solo procesar si el email realmente cambió
        if (!usuario.getEmail().equals(nuevoEmail)) {
            usuario.changeEmail(nuevoEmail);
            usuario.marcarPendienteVerificacion();
            usuario.generarTokenVerificacion();
            
            repositorioUsuarios.save(usuario);
            
            // TODO: Enviar email de verificación al nuevo correo
        }
    }
}
