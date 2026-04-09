package com.sigp.exception;

/**
 * Excepción personalizada usada en toda la aplicación
 * para representar errores de negocio con mensajes claros.
 */
public class CustomException extends Exception {
    public CustomException(String message) {
        super(message);
    }
}