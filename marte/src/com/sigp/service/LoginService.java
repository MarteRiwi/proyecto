package com.sigp.service;

import com.sigp.controller.AdminController;
import com.sigp.model.User;
import com.sigp.repository.UserRepository;

/**
 * Servicio de autenticación y registro de usuarios.
 * Decide si el usuario es admin (redirige al panel de administración)
 * o paciente (redirige al registro de información personal).
 * Basado en el proyecto de Esteban (serviceLogin).
 */
public class LoginService {

    /** Verifica credenciales contra la base de datos de usuarios. */
    public boolean authenticate(String email, String password) {
        for (User u : UserRepository.getUserDatabase()) {
            if (u.username().equalsIgnoreCase(email) && u.password().equals(password)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Valida que el email no exista previamente y que cumpla las reglas del record User.
     */
    public void registerUser(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("El email no puede estar vacío.");
            return;
        }
        // Verificar si ya existe
        if (UserRepository.findByEmail(email) != null) {
            System.out.println("El email '" + email + "' ya está registrado. Usa otro.");
            return;
        }
        try {
            UserRepository.addUser(new User(email, password));
            System.out.println("Usuario con email '" + email + "' registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /** Determina si el usuario tiene rol de administrador. */
    public boolean isAdmin(String email) {
        return "admin".equalsIgnoreCase(email) || email.toLowerCase().contains("admin");
    }

    /**
     * Punto de entrada post-login.
     * Si es admin → panel de administración.
     * Si es paciente → registro de información personal.
     */
    public void handleLoginSuccess(User user) {
        if (isAdmin(user.username())) {
            System.out.println("Acceso de Administrador detectado.");
            AdminController adminController = new AdminController();
            adminController.showAdminMenu(user);
        } else {
            System.out.println("Bienvenido, " + user.username());
            System.out.println("Por favor, completa tu información como paciente:");
            PatientService patientService = new PatientService();
            patientService.registrarInformacionPaciente(user.username());
        }
    }
}
