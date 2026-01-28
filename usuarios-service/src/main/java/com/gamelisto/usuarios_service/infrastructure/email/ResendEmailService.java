package com.gamelisto.usuarios_service.infrastructure.email;

import com.gamelisto.usuarios_service.application.ports.IEmailService;
import com.gamelisto.usuarios_service.domain.exceptions.EmailSendingException;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailService implements IEmailService {

  private static final Logger logger = LoggerFactory.getLogger(ResendEmailService.class);

  private final Resend resend;
  private final String fromEmail;
  private final String frontendUrl;

  public ResendEmailService(
      @Value("${resend.api-key}") String apiKey,
      @Value("${resend.from-email}") String fromEmail,
      @Value("${app.frontend.url}") String frontendUrl) {
    this.resend = new Resend(apiKey);
    this.fromEmail = fromEmail;
    this.frontendUrl = frontendUrl;
    logger.info("✅ Servicio de email Resend inicializado correctamente");
  }

  @Override
  public void sendVerificationEmail(String toEmail, String username, String verificationToken) {
    String subject = "Verifica tu cuenta en GameListo";
    String htmlBody = buildVerificationEmailHtml(username, verificationToken);

    sendEmail(toEmail, subject, htmlBody, "verificación");
  }

  @Override
  public void sendPasswordResetEmail(String toEmail, String username, String resetToken) {
    String subject = "Restablece tu contraseña en GameListo";
    String htmlBody = buildPasswordResetEmailHtml(username, resetToken);

    sendEmail(toEmail, subject, htmlBody, "restablecimiento de contraseña");
  }

  private void sendEmail(String toEmail, String subject, String htmlBody, String emailType) {
    CreateEmailOptions params =
        CreateEmailOptions.builder()
            .from(fromEmail)
            .to(toEmail)
            .subject(subject)
            .html(htmlBody)
            .build();

    try {
      CreateEmailResponse response = resend.emails().send(params);
      logger.info(
          "📧 Email de {} enviado exitosamente a {} - ID: {}",
          emailType,
          toEmail,
          response.getId());
    } catch (ResendException e) {
      logger.error("❌ Error al enviar email de {} a {}: {}", emailType, toEmail, e.getMessage(), e);
      throw new EmailSendingException(
          "No se pudo enviar el email de " + emailType + " a " + toEmail, e);
    }
  }

  private String buildVerificationEmailHtml(String username, String verificationToken) {
    String verificationUrl = frontendUrl + "/verify-email?token=" + verificationToken;

    return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4;">
                    <table role="presentation" style="width: 100%%; border-collapse: collapse;">
                        <tr>
                            <td align="center" style="padding: 40px 0;">
                                <table role="presentation" style="width: 600px; border-collapse: collapse; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                                    <!-- Header -->
                                    <tr>
                                        <td style="padding: 40px 40px 20px 40px; text-align: center; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); border-radius: 8px 8px 0 0;">
                                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: bold;">🎮 GameListo</h1>
                                        </td>
                                    </tr>

                                    <!-- Content -->
                                    <tr>
                                        <td style="padding: 40px;">
                                            <h2 style="margin: 0 0 20px 0; color: #333333; font-size: 24px;">¡Hola, %s! 👋</h2>
                                            <p style="margin: 0 0 20px 0; color: #666666; font-size: 16px; line-height: 1.6;">
                                                ¡Bienvenido a GameListo! Estamos emocionados de tenerte en nuestra comunidad de gamers.
                                            </p>
                                            <p style="margin: 0 0 30px 0; color: #666666; font-size: 16px; line-height: 1.6;">
                                                Para completar tu registro y empezar a gestionar tu biblioteca de juegos, por favor verifica tu dirección de email haciendo clic en el botón de abajo:
                                            </p>

                                            <!-- CTA Button -->
                                            <table role="presentation" style="margin: 0 auto;">
                                                <tr>
                                                    <td style="border-radius: 4px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);">
                                                        <a href="%s" target="_blank" style="display: inline-block; padding: 16px 40px; color: #ffffff; text-decoration: none; font-size: 16px; font-weight: bold; border-radius: 4px;">
                                                            Verificar mi cuenta
                                                        </a>
                                                    </td>
                                                </tr>
                                            </table>

                                            <p style="margin: 30px 0 20px 0; color: #999999; font-size: 14px; line-height: 1.6;">
                                                Si el botón no funciona, copia y pega el siguiente enlace en tu navegador:
                                            </p>
                                            <p style="margin: 0 0 30px 0; color: #667eea; font-size: 14px; word-break: break-all;">
                                                %s
                                            </p>

                                            <div style="border-top: 1px solid #eeeeee; padding-top: 20px; margin-top: 30px;">
                                                <p style="margin: 0; color: #999999; font-size: 14px; line-height: 1.6;">
                                                    ⏰ Este enlace expirará en 24 horas por razones de seguridad.
                                                </p>
                                                <p style="margin: 10px 0 0 0; color: #999999; font-size: 14px; line-height: 1.6;">
                                                    🔒 Si no creaste esta cuenta, puedes ignorar este mensaje de forma segura.
                                                </p>
                                            </div>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="padding: 30px 40px; text-align: center; background-color: #f8f9fa; border-radius: 0 0 8px 8px;">
                                            <p style="margin: 0 0 10px 0; color: #999999; font-size: 12px;">
                                                © 2026 GameListo. Todos los derechos reservados.
                                            </p>
                                            <p style="margin: 0; color: #999999; font-size: 12px;">
                                                Este es un email automático, por favor no respondas a este mensaje.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """
        .formatted(username, verificationUrl, verificationUrl);
  }

  private String buildPasswordResetEmailHtml(String username, String resetToken) {
    String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;

    return """
                <!DOCTYPE html>
                <html lang="es">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f4f4f4;">
                    <table role="presentation" style="width: 100%%; border-collapse: collapse;">
                        <tr>
                            <td align="center" style="padding: 40px 0;">
                                <table role="presentation" style="width: 600px; border-collapse: collapse; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                                    <!-- Header -->
                                    <tr>
                                        <td style="padding: 40px 40px 20px 40px; text-align: center; background: linear-gradient(135deg, #f093fb 0%%, #f5576c 100%%); border-radius: 8px 8px 0 0;">
                                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: bold;">🔐 GameListo</h1>
                                        </td>
                                    </tr>

                                    <!-- Content -->
                                    <tr>
                                        <td style="padding: 40px;">
                                            <h2 style="margin: 0 0 20px 0; color: #333333; font-size: 24px;">Hola, %s</h2>
                                            <p style="margin: 0 0 20px 0; color: #666666; font-size: 16px; line-height: 1.6;">
                                                Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en GameListo.
                                            </p>
                                            <p style="margin: 0 0 30px 0; color: #666666; font-size: 16px; line-height: 1.6;">
                                                Para crear una nueva contraseña, haz clic en el botón de abajo:
                                            </p>

                                            <!-- CTA Button -->
                                            <table role="presentation" style="margin: 0 auto;">
                                                <tr>
                                                    <td style="border-radius: 4px; background: linear-gradient(135deg, #f093fb 0%%, #f5576c 100%%);">
                                                        <a href="%s" target="_blank" style="display: inline-block; padding: 16px 40px; color: #ffffff; text-decoration: none; font-size: 16px; font-weight: bold; border-radius: 4px;">
                                                            Restablecer contraseña
                                                        </a>
                                                    </td>
                                                </tr>
                                            </table>

                                            <p style="margin: 30px 0 20px 0; color: #999999; font-size: 14px; line-height: 1.6;">
                                                Si el botón no funciona, copia y pega el siguiente enlace en tu navegador:
                                            </p>
                                            <p style="margin: 0 0 30px 0; color: #f5576c; font-size: 14px; word-break: break-all;">
                                                %s
                                            </p>

                                            <div style="border-top: 1px solid #eeeeee; padding-top: 20px; margin-top: 30px;">
                                                <p style="margin: 0; color: #999999; font-size: 14px; line-height: 1.6;">
                                                    ⏰ Este enlace expirará en 1 hora por razones de seguridad.
                                                </p>
                                                <p style="margin: 10px 0 0 0; color: #d9534f; font-size: 14px; line-height: 1.6; font-weight: bold;">
                                                    ⚠️ Si no solicitaste este cambio, ignora este email y tu contraseña permanecerá sin cambios.
                                                </p>
                                            </div>
                                        </td>
                                    </tr>

                                    <!-- Footer -->
                                    <tr>
                                        <td style="padding: 30px 40px; text-align: center; background-color: #f8f9fa; border-radius: 0 0 8px 8px;">
                                            <p style="margin: 0 0 10px 0; color: #999999; font-size: 12px;">
                                                © 2026 GameListo. Todos los derechos reservados.
                                            </p>
                                            <p style="margin: 0; color: #999999; font-size: 12px;">
                                                Este es un email automático, por favor no respondas a este mensaje.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """
        .formatted(username, resetUrl, resetUrl);
  }
}
