package com.sigp.model;

/**
 * Modelo de usuario del sistema (admin, doctor o paciente).
 * Usa record de Java para inmutabilidad y validaciones en el constructor compacto.
 */
public record User(String username, String password, String role) {

    public User {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío.");
        }
        boolean isAdminUser = "admin".equalsIgnoreCase(username);
        if (!isAdminUser && !username.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException(
                "Email inválido. Formato esperado: usuario@dominio.com");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        if (password.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un número.");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("El rol del usuario no puede estar vacío.");
        }
        String normalizedRole = role.trim().toUpperCase();
        if (!normalizedRole.equals("ADMIN") && !normalizedRole.equals("DOCTOR") && !normalizedRole.equals("PATIENT")) {
            throw new IllegalArgumentException("Rol inválido. Debe ser ADMIN, DOCTOR o PATIENT.");
        }
        role = normalizedRole;
    }
}
