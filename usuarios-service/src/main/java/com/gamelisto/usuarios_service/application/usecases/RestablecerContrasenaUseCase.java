package com.gamelisto.usuarios_service.application.usecases;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.dto.RestablecerContrasenaCommand;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.Email;
import com.gamelisto.usuarios_service.domain.usuario.PasswordHash;
import com.gamelisto.usuarios_service.domain.usuario.TokenVerificacion;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;
import com.gamelisto.usuarios_service.domain.exceptions.TokenVerificacionInvalidoException;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;

@Service
public class RestablecerContrasenaUseCase {
    private final RepositorioUsuarios repositorioUsuarios;
    private final PasswordEncoder passwordEncoder;
    
    public RestablecerContrasenaUseCase(RepositorioUsuarios repositorioUsuarios, PasswordEncoder passwordEncoder) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(RestablecerContrasenaCommand command){
        Email email = Email.of(command.email());

        Usuario usuario = repositorioUsuarios
                .findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(command.email()));

        TokenVerificacion tokenRecibido = TokenVerificacion.of(command.token());

        if (!usuario.tieneTokenRestablecimientoValido(tokenRecibido)) {
            throw new TokenVerificacionInvalidoException("Token de restablecimiento inválido o expirado");
        }

        String hashedPasswordNueva = passwordEncoder.encode(command.nuevaContrasena());
        usuario.changePasswordHash(PasswordHash.of(hashedPasswordNueva));
        usuario.invalidarTokenRestablecimiento();

        repositorioUsuarios.save(usuario);
    }

}
