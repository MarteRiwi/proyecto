package com.sigp.model;

/**
 * Modelo de usuario del sistema (admin, doctor o paciente).
 */
public class User {

    private String username;
    private String password;
    private String role;

    public User() {
    }

    public User(String username, String password, String role) {
        validateUsername(username);
        validatePassword(password);
        validateRole(role);
        this.username = username;
        this.password = password;
        this.role = role.trim().toUpperCase();
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío.");
        }
        boolean isAdminUser = "admin".equalsIgnoreCase(username);
        if (!isAdminUser && !username.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException(
                "Email inválido. Formato esperado: usuario@dominio.com");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        if (password.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres.");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un número.");
        }
    }

    private void validateRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("El rol del usuario no puede estar vacío.");
        }
        String normalizedRole = role.trim().toUpperCase();
        if (!normalizedRole.equals("ADMIN") && !normalizedRole.equals("DOCTOR") && !normalizedRole.equals("PATIENT")) {
            throw new IllegalArgumentException("Rol inválido. Debe ser ADMIN, DOCTOR o PATIENT.");
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        validateUsername(username);
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        validatePassword(password);
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        validateRole(role);
        this.role = role.trim().toUpperCase();
    }

    // Compatibilidad con el código existente basado en record.
    public String username() {
        return getUsername();
    }

    public String password() {
        return getPassword();
    }

    public String role() {
        return getRole();
    }
}
