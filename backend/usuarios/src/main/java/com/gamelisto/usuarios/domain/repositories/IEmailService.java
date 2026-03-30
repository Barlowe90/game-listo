package com.gamelisto.usuarios.domain.repositories;

public interface IEmailService {

  void sendVerificationEmail(String toEmail, String username, String verificationToken);

  void sendPasswordResetEmail(String toEmail, String username, String resetToken);
}
