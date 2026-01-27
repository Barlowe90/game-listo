package com.gamelisto.usuarios_service.application.ports;

public interface IEmailService {

    /**
     * Envía un email de verificación al usuario con un token.
     * 
     * @param toEmail           Dirección de email del destinatario
     * @param username          Nombre de usuario para personalizar el mensaje
     * @param verificationToken Token de verificación único
     * @throws EmailSendingException si ocurre un error al enviar el email
     */
    void sendVerificationEmail(String toEmail, String username, String verificationToken);

    /**
     * Envía un email de restablecimiento de contraseña.
     * 
     * @param toEmail    Dirección de email del destinatario
     * @param username   Nombre de usuario para personalizar el mensaje
     * @param resetToken Token de restablecimiento único
     * @throws EmailSendingException si ocurre un error al enviar el email
     */
    void sendPasswordResetEmail(String toEmail, String username, String resetToken);
}
