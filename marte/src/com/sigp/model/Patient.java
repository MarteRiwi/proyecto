package com.sigp.model;

/**
 * Modelo de paciente del sistema hospitalario Marte.
 * Validaciones colombianas: celular de 10 dígitos iniciando en 3,
 * cédula de 6 a 10 dígitos, y email con formato estándar.
 */
public class Patient {

    private String name;
    private String nationality;
    private String phone;
    private String email;
    private int age;
    private String id;

    public Patient() {
    }

    public Patient(String name, String nationality, String phone, String email, int age, String id) {
        validateName(name);
        validateNationality(nationality);
        validatePhone(phone);
        validateEmail(email);
        validateAge(age);
        validateId(id);
        this.name = name;
        this.nationality = nationality;
        this.phone = phone;
        this.email = email;
        this.age = age;
        this.id = id;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (!name.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException("El nombre solo puede contener letras y espacios.");
        }
    }

    private void validateNationality(String nationality) {
        if (nationality == null || nationality.trim().isEmpty()) {
            throw new IllegalArgumentException("La nacionalidad no puede estar vacía.");
        }
        if (!nationality.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException("La nacionalidad solo puede contener letras y espacios.");
        }
    }

    private void validatePhone(String phone) {
        // Celular colombiano: 10 dígitos, inicia en 3
        if (phone == null || !phone.matches("^3[0-9]{9}$")) {
            throw new IllegalArgumentException(
                "Teléfono inválido. Debe ser un celular colombiano de 10 dígitos que empiece en 3 (ej: 3001234567).");
        }
    }

    private void validateEmail(String email) {
        // Email básico
        if (email == null || !email.matches("^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Email inválido. Formato esperado: usuario@dominio.com");
        }
    }

    private void validateAge(int age) {
        if (age <= 0 || age > 120) {
            throw new IllegalArgumentException("La edad debe estar entre 1 y 120 años.");
        }
    }

    private void validateId(String id) {
        // Cédula colombiana: 6 a 10 dígitos numéricos
        if (id == null || !id.matches("^[0-9]{6,10}$")) {
            throw new IllegalArgumentException(
                "Cédula inválida. Debe contener entre 6 y 10 dígitos numéricos.");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        validateNationality(nationality);
        this.nationality = nationality;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        validatePhone(phone);
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        validateAge(age);
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        validateId(id);
        this.id = id;
    }

    // Compatibilidad con el código existente basado en record.
    public String name() {
        return getName();
    }

    public String nationality() {
        return getNationality();
    }

    public String phone() {
        return getPhone();
    }

    public String email() {
        return getEmail();
    }

    public int age() {
        return getAge();
    }

    public String id() {
        return getId();
    }
}