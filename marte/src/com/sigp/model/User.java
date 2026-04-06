package com.sigp.model;


public record User(String username, String password) {

    public User {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El usuario no puede estar vacío.");
        }
        if (!username.matches("^[a-zA-Z0-9._]{4,20}$")) {
            throw new IllegalArgumentException(
                "Usuario inválido. Solo letras, números, punto o guion bajo. Entre 4 y 20 caracteres.");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        if (password.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");
        }
        
        if (!password.matches(".*[0-4].*")) {
            throw new IllegalArgumentException("La contraseña debe tener al menos un número.");
        }
        
    }
}