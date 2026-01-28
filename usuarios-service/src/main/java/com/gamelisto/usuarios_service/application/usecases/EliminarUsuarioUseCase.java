package com.gamelisto.usuarios_service.application.usecases;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gamelisto.usuarios_service.application.ports.IUsuarioPublisher;
import com.gamelisto.usuarios_service.domain.events.UsuarioEliminado;
import com.gamelisto.usuarios_service.domain.exceptions.UsuarioNoEncontradoException;
import com.gamelisto.usuarios_service.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios_service.domain.usuario.UsuarioId;
import com.gamelisto.usuarios_service.domain.usuario.Usuario;

@Service
public class EliminarUsuarioUseCase {

    private static final String ROUTING_KEY_SUFFIX = "usuario.eliminado";
    private final RepositorioUsuarios repositorioUsuarios;
    private final IUsuarioPublisher eventosPublisher;

    public EliminarUsuarioUseCase(
            RepositorioUsuarios repositorioUsuarios, 
            IUsuarioPublisher eventosPublisher) {
        this.repositorioUsuarios = repositorioUsuarios;
        this.eventosPublisher = eventosPublisher;
    }

    @Transactional
    public void execute(String idUsuario) {
        UsuarioId id = UsuarioId.fromString(idUsuario);
        Usuario usuario = repositorioUsuarios
            .findById(id)
            .orElseThrow(() -> new UsuarioNoEncontradoException(idUsuario));

        usuario.delete();
        
        Usuario usuarioEliminado = repositorioUsuarios.save(usuario);
        
        enviarEventoUsuarioEliminado(usuarioEliminado);
    }

    private void enviarEventoUsuarioEliminado(Usuario usuario) {
        UsuarioEliminado evento = UsuarioEliminado.of(
            usuario.getId().value().toString()
        );
        eventosPublisher.publish(ROUTING_KEY_SUFFIX, evento);
    }
}
