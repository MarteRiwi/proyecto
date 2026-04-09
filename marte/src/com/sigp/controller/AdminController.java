package com.sigp.controller;

import com.sigp.model.User;
import com.sigp.repository.PatientRepository;
import com.sigp.service.LoginService;
import java.util.Scanner;

/**
 * Panel de administración del sistema hospitalario Marte.
 *
 * El admin puede:
 *   - Gestionar doctores (CRUD completo, delegado a DoctorController)
 *   - Registrar y eliminar pacientes
 *   - Crear nuevos usuarios del sistema
 *
 * Basado en el controllerAdmin de Esteban, con gestión de doctores
 * reemplazada por el DoctorController de Stiven.
 */
public class AdminController {

    private final Scanner scanner = new Scanner(System.in);

    /** Muestra el menú principal del administrador y ejecuta las opciones. */
    public void showAdminMenu(User admin) {
        int option = 0;
        do {
            System.out.println("\n========================================");
            System.out.println("   BIENVENIDO, ADMIN: " + admin.username().toUpperCase());
            System.out.println("========================================");
            System.out.println("1. Gestionar Doctores (CRUD completo)");
            System.out.println("2. Ver lista de pacientes");
            System.out.println("3. Eliminar paciente");
            System.out.println("4. Registrar nuevo usuario");
            System.out.println("5. Cerrar sesión");
            System.out.print("Seleccione una opción: ");

            try {
                option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 1 -> {
                        // Delega completamente en DoctorController (proyecto Stiven)
                        DoctorController doctorController = new DoctorController();
                        doctorController.mostrarMenuDoctores();
                    }
                    case 2 -> listarPacientes();
                    case 3 -> eliminarPaciente();
                    case 4 -> registrarNuevoUsuario();
                    case 5 -> System.out.println("Cerrando sesión de administrador...");
                    default -> System.out.println("Opción no válida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Por favor ingresa un número válido.");
            }

        } while (option != 5);
    }

    // ── Gestión de pacientes ───────────────────────────────────────────────────

    private void listarPacientes() {
        System.out.println("\n--- Lista de Pacientes ---");
        if (PatientRepository.getPatientList().isEmpty()) {
            System.out.println("No hay pacientes registrados.");
        } else {
            PatientRepository.getPatientList().forEach(p ->
                System.out.println(p.name() + " | Cédula: " + p.id() + " | Email: " + p.email())
            );
        }
    }

    private void eliminarPaciente() {
        System.out.println("\n--- Eliminar Paciente ---");
        if (PatientRepository.getPatientList().isEmpty()) {
            System.out.println("No hay pacientes registrados.");
            return;
        }
        listarPacientes();
        System.out.print("Ingrese el nombre del paciente a eliminar: ");
        String name = scanner.nextLine();
        if (PatientRepository.removeByName(name)) {
            System.out.println("Paciente '" + name + "' eliminado correctamente.");
        } else {
            System.out.println("No se encontró ningún paciente con ese nombre.");
        }
    }

    // ── Gestión de usuarios ────────────────────────────────────────────────────

    private void registrarNuevoUsuario() {
        System.out.print("Email del nuevo usuario: ");
        String username = scanner.nextLine();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();
        System.out.println("Rol del usuario:\n1. PACIENTE\n2. DOCTOR\n3. ADMIN");
        System.out.print("Selecciona el rol: ");

        String role = "PATIENT";
        String option = scanner.nextLine().trim();
        switch (option) {
            case "1" -> role = "PATIENT";
            case "2" -> role = "DOCTOR";
            case "3" -> role = "ADMIN";
            default -> System.out.println("Rol no válido. Se registrará como PATIENT por defecto.");
        }

        LoginService loginService = new LoginService();
        loginService.registerUser(username, password, role);
    }
}
