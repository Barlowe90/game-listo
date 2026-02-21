package com.gamelisto.usuarios.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gamelisto.usuarios.application.dto.*;
import com.gamelisto.usuarios.application.usecases.*;
import com.gamelisto.usuarios.config.TestMessagingConfig;
import com.gamelisto.usuarios.domain.repositories.RepositorioUsuarios;
import com.gamelisto.usuarios.domain.usuario.*;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestMessagingConfig.class)
@Transactional
@DisplayName("Tests de Integración E2E - Flujos Completos de Usuarios")
class UsuarioFlowIntegrationTest {

  @Autowired private CrearUsuarioUseCase crearUsuarioUseCase;

  @Autowired private EditarPerfilUsuarioUseCase editarPerfilUsuarioUseCase;

  @Autowired private ObtenerUsuarioPorId obtenerUsuarioPorId;

  @Autowired private EliminarUsuarioUseCase eliminarUsuarioUseCase;

  @Autowired private CambiarEstadoUsuarioUseCase cambiarEstadoUsuarioUseCase;

  @Autowired private VerificarEmailUseCase verificarEmailUseCase;

  @Autowired private ReenviarVerificacionUseCase reenviarVerificacionUseCase;

  @Autowired private RestablecerContrasenaUseCase restablecerContrasenaUseCase;

  @Autowired private VincularDiscordUseCase vincularDiscordUseCase;

  @Autowired private DesvincularDiscordUseCase desvincularDiscordUseCase;

  @Autowired private BuscarUsuariosPorEstadoUseCase buscarUsuariosPorEstadoUseCase;

  @Autowired private RepositorioUsuarios repositorioUsuarios;

  @Test
  @DisplayName("Flujo completo: Crear usuario → Verificar email → Editar perfil")
  void flujoCompletoCrearVerificarYEditar() {
    // 1. Crear usuario
    CrearUsuarioCommand crearCommand =
        new CrearUsuarioCommand("testuser", "testuser@example.com", "Password123!");
    UsuarioDTO usuarioCreado = crearUsuarioUseCase.execute(crearCommand);

    assertThat(usuarioCreado).isNotNull();
    assertThat(usuarioCreado.username()).isEqualTo("testuser");
    assertThat(usuarioCreado.status()).isEqualTo("PENDIENTE_DE_VERIFICACION");

    // 2. Verificar email - buscar usuario en BD para obtener token
    Usuario usuarioEnBD =
        repositorioUsuarios.findByEmail(Email.of("testuser@example.com")).orElseThrow();
    VerificarEmailCommand verificarCommand =
        new VerificarEmailCommand(usuarioEnBD.getTokenVerificacion().value());
    verificarEmailUseCase.execute(verificarCommand);

    // Obtener usuario actualizado
    UsuarioDTO usuarioVerificado = obtenerUsuarioPorId.execute(usuarioCreado.id());
    assertThat(usuarioVerificado.status()).isEqualTo("ACTIVO");

    // 3. Editar perfil
    EditarPerfilUsuarioCommand editarCommand =
        new EditarPerfilUsuarioCommand(
            usuarioVerificado.id(), "https://i.imgur.com/avatar.png", "ENG", true);
    UsuarioDTO usuarioEditado = editarPerfilUsuarioUseCase.execute(editarCommand);

    assertThat(usuarioEditado.avatar()).isEqualTo("https://i.imgur.com/avatar.png");
    assertThat(usuarioEditado.language()).isEqualTo("ENG");
    assertThat(usuarioEditado.notificationsActive()).isTrue();
  }

  @Test
  @DisplayName("Flujo completo: Crear usuario → Vincular Discord → Desvincular Discord")
  void flujoCompletoDiscordIntegration() {
    // 1. Crear usuario
    CrearUsuarioCommand crearCommand =
        new CrearUsuarioCommand("discorduser", "discord@example.com", "Password123!");
    UsuarioDTO usuarioCreado = crearUsuarioUseCase.execute(crearCommand);

    // 2. Vincular Discord
    VincularDiscordCommand vincularCommand =
        new VincularDiscordCommand(usuarioCreado.id(), "123456789", "DiscordUser#1234");
    UsuarioDTO usuarioVinculado = vincularDiscordUseCase.execute(vincularCommand);

    assertThat(usuarioVinculado.discordUserId()).isEqualTo("123456789");
    assertThat(usuarioVinculado.discordUsername()).isEqualTo("DiscordUser#1234");

    // 3. Desvincular Discord
    UsuarioDTO usuarioDesvinculado = desvincularDiscordUseCase.execute(usuarioVinculado.id());

    assertThat(usuarioDesvinculado.discordUserId()).isNull();
    assertThat(usuarioDesvinculado.discordUsername()).isNull();
  }

  @Test
  @DisplayName("Flujo completo: Crear usuario → Cambiar estado → Suspender → Eliminar")
  void flujoCompletoGestionEstados() {
    // 1. Crear usuario
    CrearUsuarioCommand crearCommand =
        new CrearUsuarioCommand("stateuser", "state@example.com", "Password123!");
    UsuarioDTO usuarioCreado = crearUsuarioUseCase.execute(crearCommand);

    // 2. Activar (verificar email)
    Usuario usuarioEnBD =
        repositorioUsuarios.findByEmail(Email.of("state@example.com")).orElseThrow();
    VerificarEmailCommand verificarCommand =
        new VerificarEmailCommand(usuarioEnBD.getTokenVerificacion().value());
    verificarEmailUseCase.execute(verificarCommand);
    UsuarioDTO usuarioActivo = obtenerUsuarioPorId.execute(usuarioCreado.id());
    assertThat(usuarioActivo.status()).isEqualTo("ACTIVO");

    // 3. Suspender
    CambiarEstadoUsuarioCommand suspenderCommand =
        new CambiarEstadoUsuarioCommand(usuarioActivo.id(), EstadoUsuario.SUSPENDIDO);
    UsuarioDTO usuarioSuspendido = cambiarEstadoUsuarioUseCase.execute(suspenderCommand);
    assertThat(usuarioSuspendido.status()).isEqualTo("SUSPENDIDO");

    // 4. Eliminar (marca como ELIMINADO)
    eliminarUsuarioUseCase.execute(usuarioSuspendido.id());

    // Verificar que el usuario aún existe pero con estado ELIMINADO (soft delete)
    UsuarioDTO usuarioEliminado = obtenerUsuarioPorId.execute(usuarioSuspendido.id());
    assertThat(usuarioEliminado.status()).isEqualTo("ELIMINADO");
  }

  @Disabled(
      "Requiere configuración adicional para generación de tokens - habilitar solo para pruebas manuales")
  @Test
  @DisplayName("Flujo completo: Crear usuario → Solicitar reset password → Restablecer contraseña")
  void flujoCompletoResetPassword() {
    // 1. Crear y verificar usuario
    CrearUsuarioCommand crearCommand =
        new CrearUsuarioCommand("resetuser", "reset@example.com", "OldPassword123!");
    UsuarioDTO usuarioCreado = crearUsuarioUseCase.execute(crearCommand);

    Usuario usuarioEnBD =
        repositorioUsuarios.findByEmail(Email.of("reset@example.com")).orElseThrow();
    VerificarEmailCommand verificarCommand =
        new VerificarEmailCommand(usuarioEnBD.getTokenVerificacion().value());
    verificarEmailUseCase.execute(verificarCommand);

    // 2. Solicitar reset de contraseña (genera token)
    Usuario usuario = repositorioUsuarios.findByEmail(Email.of("reset@example.com")).orElseThrow();
    usuario.generarTokenRestablecimiento();
    repositorioUsuarios.save(usuario);

    String tokenReset = usuario.getTokenRestablecimiento().value();

    // 3. Restablecer contraseña con el token
    RestablecerContrasenaCommand resetCommand =
        new RestablecerContrasenaCommand("reset@example.com", tokenReset, "NewPassword456!");
    restablecerContrasenaUseCase.execute(resetCommand);

    // Verificar que el usuario aún existe
    UsuarioDTO usuarioFinal = obtenerUsuarioPorId.execute(usuarioCreado.id());
    assertThat(usuarioFinal).isNotNull();
    assertThat(usuarioFinal.email()).isEqualTo("reset@example.com");
  }

  @Test
  @DisplayName("Flujo completo: Buscar usuarios por estado y notificaciones")
  void flujoCompletoBusquedaPorCriterios() {
    // 1. Crear varios usuarios con diferentes estados
    CrearUsuarioCommand user1 =
        new CrearUsuarioCommand("activeuser1", "active1@example.com", "Pass123!");
    CrearUsuarioCommand user2 =
        new CrearUsuarioCommand("activeuser2", "active2@example.com", "Pass123!");
    CrearUsuarioCommand user3 =
        new CrearUsuarioCommand("pendinguser", "pending@example.com", "Pass123!");

    crearUsuarioUseCase.execute(user1);
    crearUsuarioUseCase.execute(user2);
    crearUsuarioUseCase.execute(user3);

    // 2. Verificar usuario1 y usuario2
    Usuario u1 = repositorioUsuarios.findByEmail(Email.of("active1@example.com")).orElseThrow();
    Usuario u2 = repositorioUsuarios.findByEmail(Email.of("active2@example.com")).orElseThrow();
    verificarEmailUseCase.execute(new VerificarEmailCommand(u1.getTokenVerificacion().value()));
    verificarEmailUseCase.execute(new VerificarEmailCommand(u2.getTokenVerificacion().value()));

    // 3. Buscar por estado ACTIVO
    List<UsuarioDTO> usuariosActivos = buscarUsuariosPorEstadoUseCase.execute(EstadoUsuario.ACTIVO);

    assertThat(usuariosActivos)
        .hasSizeGreaterThanOrEqualTo(2)
        .extracting(UsuarioDTO::status)
        .containsOnly("ACTIVO");

    // 4. Buscar por estado PENDIENTE_DE_VERIFICACION
    List<UsuarioDTO> usuariosPendientes =
        buscarUsuariosPorEstadoUseCase.execute(EstadoUsuario.PENDIENTE_DE_VERIFICACION);

    assertThat(usuariosPendientes).isNotEmpty().anyMatch(u -> u.username().equals("pendinguser"));
  }

  @Test
  @DisplayName("Flujo completo: Reenviar verificación de email")
  void flujoCompletoReenviarVerificacion() {
    // 1. Crear usuario
    CrearUsuarioCommand crearCommand =
        new CrearUsuarioCommand("reenviouser", "reenvio@example.com", "Password123!");
    UsuarioDTO usuarioCreado = crearUsuarioUseCase.execute(crearCommand);
    Usuario usuarioOriginal =
        repositorioUsuarios.findByEmail(Email.of("reenvio@example.com")).orElseThrow();
    String tokenOriginal = usuarioOriginal.getTokenVerificacion().value();

    // 2. Reenviar verificación (genera nuevo token)
    ReenviarVerificacionCommand reenviarCommand =
        new ReenviarVerificacionCommand("reenvio@example.com");
    reenviarVerificacionUseCase.execute(reenviarCommand);

    Usuario usuarioReenviado =
        repositorioUsuarios.findByEmail(Email.of("reenvio@example.com")).orElseThrow();
    assertThat(usuarioReenviado.getTokenVerificacion()).isNotNull();
    assertThat(usuarioReenviado.getTokenVerificacion().value()).isNotEqualTo(tokenOriginal);

    // 3. Verificar con el nuevo token
    VerificarEmailCommand verificarCommand =
        new VerificarEmailCommand(usuarioReenviado.getTokenVerificacion().value());
    verificarEmailUseCase.execute(verificarCommand);

    UsuarioDTO usuarioVerificado = obtenerUsuarioPorId.execute(usuarioCreado.id());
    assertThat(usuarioVerificado.status()).isEqualTo("ACTIVO");
  }

  @Test
  @DisplayName("Debe validar que no se puedan crear usuarios duplicados")
  void debeValidarUsuariosDuplicados() {
    // 1. Crear primer usuario
    CrearUsuarioCommand command1 =
        new CrearUsuarioCommand("duplicateuser", "duplicate@example.com", "Password123!");
    crearUsuarioUseCase.execute(command1);

    // 2. Intentar crear con mismo username
    CrearUsuarioCommand command2 =
        new CrearUsuarioCommand(
            "duplicateuser", // Username duplicado
            "different@example.com",
            "Password123!");
    assertThatThrownBy(() -> crearUsuarioUseCase.execute(command2))
        .hasMessageContaining("duplicateuser");

    // 3. Intentar crear con mismo email
    CrearUsuarioCommand command3 =
        new CrearUsuarioCommand(
            "differentuser",
            "duplicate@example.com", // Email duplicado
            "Password123!");
    assertThatThrownBy(() -> crearUsuarioUseCase.execute(command3))
        .hasMessageContaining("duplicate@example.com");
  }
}
