package com.gamelisto.usuarios_service.domain.exceptions;

/**
 * Excepción lanzada cuando ocurre un error al enviar un email.
 */
public class EmailSendingException extends RuntimeException {
    
    public EmailSendingException(String message) {
        super(message);
    }
    
    public EmailSendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
