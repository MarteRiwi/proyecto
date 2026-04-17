package com.sigp.service;

import com.sigp.controller.AdminController;
import com.sigp.controller.DoctorDashboard;
import com.sigp.model.User;
import com.sigp.repository.UserRepository;
<<<<<<< HEAD
=======
import java.util.Scanner;
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)

/**
 * Servicio de autenticación y registro de usuarios.
 * Decide si el usuario es admin, doctor o paciente.
 */
public class LoginService {

    public User authenticate(String email, String password) {
        User user = UserRepository.findByEmail(email);
        if (user != null && user.password().equals(password)) {
            return user;
        }
        return null;
    }

    public void registerUser(String email, String password) {
        registerUser(email, password, "PATIENT");
    }

    public void registerUser(String email, String password, String role) {
        if (email == null || email.trim().isEmpty()) {
            System.out.println("El email no puede estar vacío.");
            return;
        }
        if (UserRepository.findByEmail(email) != null) {
            System.out.println("El email '" + email + "' ya está registrado. Usa otro.");
            return;
        }
        try {
            UserRepository.addUser(email, password, role);
            System.out.println("Usuario con email '" + email + "' registrado exitosamente como " + role + ".");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

<<<<<<< HEAD
    public void handleLoginSuccess(User user) {
=======
    public void handleLoginSuccess(User user, Scanner scanner) {
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
        String role = user.role().toUpperCase();
        switch (role) {
            case "ADMIN" -> {
                System.out.println("Acceso de Administrador detectado.");
<<<<<<< HEAD
                AdminController adminController = new AdminController();
=======
                AdminController adminController = new AdminController(scanner);
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
                adminController.showAdminMenu(user);
            }
            case "DOCTOR" -> {
                System.out.println("Bienvenido médico " + user.username() + ".");
<<<<<<< HEAD
                DoctorDashboard dashboard = new DoctorDashboard();
=======
                DoctorDashboard dashboard = new DoctorDashboard(scanner);
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
                dashboard.showDoctorMenu(user);
            }
            default -> {
                System.out.println("Bienvenido, " + user.username());
<<<<<<< HEAD
                System.out.println("Por favor, completa tu información como paciente:");
                PatientService patientService = new PatientService();
                patientService.registrarInformacionPaciente(user.username());
=======
                PatientService patientService = new PatientService(scanner);
                if (!patientService.gestionarPacienteExistente(user.username())) {
                    System.out.println("Por favor, completa tu información como paciente:");
                    patientService.registrarInformacionPaciente(user.username());
                }
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
            }
        }
    }
}
