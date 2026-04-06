package com.sigp.model;


public record Patient(String name, String nationality, String phone, String email, int age, String id) {

    public Patient {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (!name.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException("El nombre solo puede contener letras y espacios.");
        }
        if (nationality == null || nationality.trim().isEmpty()) {
            throw new IllegalArgumentException("La nacionalidad no puede estar vacía.");
        }
        if (!nationality.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException("La nacionalidad solo puede contener letras y espacios.");
        }
        // Celular colombiano: 10 dígitos, inicia en 3
        if (phone == null || !phone.matches("^3[0-9]{9}$")) {
            throw new IllegalArgumentException(
                "Teléfono inválido. Debe ser un celular colombiano de 10 dígitos que empiece en 3 (ej: 3001234567).");
        }
        // Email básico
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Email inválido. Formato esperado: usuario@dominio.com");
        }
        if (age <= 0 || age > 120) {
            throw new IllegalArgumentException("La edad debe estar entre 1 y 120 años.");
        }
        // Cédula colombiana: 6 a 10 dígitos numéricos
        if (id == null || !id.matches("^[0-9]{6,10}$")) {
            throw new IllegalArgumentException(
                "Cédula inválida. Debe contener entre 6 y 10 dígitos numéricos.");
        }
    }
}