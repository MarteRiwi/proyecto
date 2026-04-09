package com.sigp.controller;

import com.sigp.model.User;
import com.sigp.repository.DoctorRepository;
import com.sigp.repository.PatientRepository;
import com.sigp.service.DoctorAppointmentService;
import com.sigp.service.LoginService;

import java.util.Scanner;

/**
 * Panel de administración del sistema hospitalario Marte (ACTUALIZADO).
 *
 * El admin puede:
 *   - Gestionar doctores (CRUD completo)
 *   - Registrar y eliminar pacientes
 *   - Crear nuevos usuarios del sistema
 *   - Los doctores pueden acceder a su menú de citas (✨ NUEVO)
 *
 * Basado en el controllerAdmin original con integración de citas.
 */
public class AdminController {

    private final Scanner scanner = new Scanner(System.in);
    private final DoctorRepository doctorRepository;

    public AdminController() {
        this.doctorRepository = new DoctorRepository();
    }

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
            System.out.println("5. Panel de Doctor (ver mis citas)"); // ✨ NUEVO
            System.out.println("6. Cerrar sesión");
            System.out.print("Seleccione una opción: ");

            try {
                option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 1 -> {
                        DoctorController doctorController = new DoctorController();
                        doctorController.mostrarMenuDoctores();
                    }
                    case 2 -> listarPacientes();
                    case 3 -> eliminarPaciente();
                    case 4 -> registrarNuevoUsuario();
                    case 5 -> accederPanelDoctor();  // ✨ NUEVO
                    case 6 -> System.out.println("Cerrando sesión de administrador...");
                    default -> System.out.println("Opción no válida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Por favor ingresa un número válido.");
            }

        } while (option != 6);
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

        LoginService loginService = new LoginService();
        loginService.registerUser(username, password);
    }

    // ── Panel de Doctor (✨ NUEVO) ────────────────────────────────────────────

    /**
     * Permite a un doctor (admin) acceder a su panel de gestión de citas.
     * Solicita el ID o nombre del doctor y muestra el menú de citas.
     */
    private void accederPanelDoctor() {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("PANEL DE DOCTOR");
        System.out.println("════════════════════════════════════════════════════════");

        if (doctorRepository.obtenerDoctores().isEmpty()) {
            System.out.println("No hay doctores registrados en el sistema.");
            return;
        }

        System.out.println("\nDoctores disponibles:\n");
        doctorRepository.obtenerDoctores().forEach(d ->
            System.out.println(d.getId() + ". " + d.getNombreCompleto() + 
                             " (" + d.getEspecialidad() + ")")
        );

        System.out.print("\nSelecciona el ID del doctor: ");
        try {
            int doctorId = Integer.parseInt(scanner.nextLine().trim());
            var doctor = doctorRepository.buscarPorId(doctorId);

            if (doctor == null) {
                System.out.println("Doctor no encontrado.");
                return;
            }

            // Mostrar el menú de citas del doctor
            DoctorAppointmentService doctorService = new DoctorAppointmentService();
            doctorService.mostrarMenuDoctorCitas(doctorId, doctor.getNombreCompleto());

        } catch (NumberFormatException e) {
            System.out.println("Por favor ingresa un número válido.");
        }
    }
}
