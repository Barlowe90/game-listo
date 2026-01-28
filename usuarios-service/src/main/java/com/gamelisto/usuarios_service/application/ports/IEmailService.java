package com.gamelisto.usuarios_service.application.ports;

public interface IEmailService {

    void sendVerificationEmail(String toEmail, String username, String verificationToken);

    void sendPasswordResetEmail(String toEmail, String username, String resetToken);
}
