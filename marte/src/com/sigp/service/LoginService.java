package com.sigp.service;

import com.sigp.controller.AdminController;
import com.sigp.controller.DoctorDashboard;
import com.sigp.model.User;
import com.sigp.repository.UserRepository;
import java.util.Scanner;

/**
 * Servicio de autenticación y registro de usuarios.
 * Decide si el usuario es admin, doctor o paciente.
 */
public class LoginService {

    public User authenticate(String email, String password) {
        try {
            User user = UserRepository.findByEmail(email);
            if (user != null && user.password().equals(password)) {
                return user;
            }
            return null;
        } catch (RuntimeException e) {
            System.out.println("Error de conexión al validar credenciales. Intenta de nuevo en unos segundos.");
            return null;
        }
    }

    public void registerUser(String email, String password) {
        registerUser(email, password, "PATIENT");
    }

    public void registerUser(String email, String password, String role) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("El email no puede estar vacío.");
            return;
        }

        // Validar formato y reglas de usuario antes de consultar la base de datos.
        try {
            new User(email, password, role);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        try {
            if (UserRepository.findByEmail(email) != null) {
                System.out.println("El email '" + email + "' ya está registrado. Usa otro.");
                return;
            }

            UserRepository.addUser(email, password, role);
            System.out.println("Usuario con email '" + email + "' registrado exitosamente como " + role + ".");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("No se pudo registrar el usuario por un problema de conexión con la base de datos.");
        }
    }

    public void handleLoginSuccess(User user, Scanner scanner) {
        String role = user.role().toUpperCase();
        switch (role) {
            case "ADMIN" -> {
                System.out.println("Acceso de Administrador detectado.");
                AdminController adminController = new AdminController(scanner);
                adminController.showAdminMenu(user);
            }
            case "DOCTOR" -> {
                System.out.println("Bienvenido médico " + user.username() + ".");
                DoctorDashboard dashboard = new DoctorDashboard(scanner);
                dashboard.showDoctorMenu(user);
            }
            default -> {
                System.out.println("Bienvenido, " + user.username());
                com.sigp.service.PatientService patientService = new com.sigp.service.PatientService(scanner);
                if (!patientService.gestionarPacienteExistente(user.username())) {
                    System.out.println("Por favor, completa tu información como paciente:");
                    patientService.registrarInformacionPaciente(user.username());
                }
            }
        }
    }
}
