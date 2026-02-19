package com.gamelisto.usuarios.domain.repositories;

// mover a repo
public interface IEmailService {

  void sendVerificationEmail(String toEmail, String username, String verificationToken);

  void sendPasswordResetEmail(String toEmail, String username, String resetToken);
}
