package com.sigp.service;

import com.sigp.model.Patient;
import com.sigp.repository.PatientRepository;
import java.util.Scanner;

/**
 * Servicio de registro y consulta de pacientes.
 * Cuando un usuario sin rol admin inicia sesión, este servicio
 * lo guía para completar su ficha como paciente y agendar citas.
 * Basado en el proyecto de Esteban (serviceAsignacionPatient).
 */
public class PatientService {

<<<<<<< HEAD
=======
    private final Scanner scanner;

    public PatientService(Scanner scanner) {
        this.scanner = scanner;
    }

>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
    /**
     * Solicita los datos del paciente por consola y lo registra en el sistema.
     *
     * @param loginEmail email con el que inició sesión
     */
    public void registrarInformacionPaciente(String loginEmail) {
<<<<<<< HEAD
        Scanner sc = new Scanner(System.in);

=======
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
        System.out.println("\n--- REGISTRO DE PACIENTE: " + loginEmail.toUpperCase() + " ---");

        try {
            System.out.print("Nombre completo: ");
<<<<<<< HEAD
            String name = sc.nextLine();

            
            System.out.print("Nacionalidad: ");
            String nationality = sc.nextLine();

            System.out.print("Teléfono celular (ej: 3001234567): ");
            String phone = sc.nextLine();

            System.out.print("Edad: ");
            int age = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Número de cédula: ");
            String id = sc.nextLine();
=======
            String name = scanner.nextLine();

            
            System.out.print("Nacionalidad: ");
            String nationality = scanner.nextLine();

            System.out.print("Teléfono celular (ej: 3001234567): ");
            String phone = scanner.nextLine();

            System.out.print("Edad: ");
            int age = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Número de cédula: ");
            String id = scanner.nextLine();
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)

            guardarPaciente(name, nationality, phone, loginEmail, age, id);

        } catch (NumberFormatException e) {
            System.out.println("Error: ingresa un número válido para la edad.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
<<<<<<< HEAD
=======
     * Si el paciente ya existe, muestra su ficha y entra al menú de citas.
     */
    public boolean gestionarPacienteExistente(String loginEmail) {
        Patient paciente = PatientRepository.findByEmail(loginEmail);
        if (paciente == null) {
            return false;
        }

        System.out.println("Tus datos como paciente ya están registrados.");
        mostrarFichaPacientePorEmail(loginEmail);
        mostrarMenuPostRegistro(paciente.name());
        return true;
    }

    /**
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
     * Crea y persiste el paciente, luego muestra su ficha y el menú de citas.
     */
    private void guardarPaciente(String name, String nationality, String phone,
                                  String email, int age, String id) {
        try {
            Patient paciente = new Patient(name, nationality, phone, email, age, id);
            PatientRepository.addPatient(paciente);
            mostrarFichaPaciente(name);
            mostrarMenuPostRegistro(name);
        } catch (IllegalArgumentException e) {
            System.out.println("Error al registrar paciente: " + e.getMessage());
        }
    }

    /** Muestra en pantalla todos los datos del paciente recién registrado. */
    public static void mostrarFichaPaciente(String name) {
        Patient found = PatientRepository.findByName(name);
<<<<<<< HEAD
=======
        imprimirFichaPaciente(found);
    }

    /** Muestra la ficha del paciente buscándolo por email. */
    public static void mostrarFichaPacientePorEmail(String email) {
        Patient found = PatientRepository.findByEmail(email);
        imprimirFichaPaciente(found);
    }

    private static void imprimirFichaPaciente(Patient found) {
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
        if (found != null) {
            System.out.println("\n========================================");
            System.out.println(" " + found.name() + " registrado como paciente.");
            System.out.println("Tus datos:");
            System.out.println("  Nombre:       " + found.name());
            System.out.println("  Cédula:       " + found.id());
            System.out.println("  Nacionalidad: " + found.nationality());
            System.out.println("  Teléfono:     " + found.phone());
            System.out.println("  Email:        " + found.email());
            System.out.println("  Edad:         " + found.age() + " años");
            System.out.println("========================================\n");
        } else {
            System.out.println("No se encontró el paciente.");
        }
    }

    /** Menú que aparece justo después de completar el registro del paciente. */
    private void mostrarMenuPostRegistro(String name) {
<<<<<<< HEAD
        Scanner sc = new Scanner(System.in);
=======
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("¿Qué deseas hacer ahora?");
            System.out.println("1. Ir al menú de citas");
<<<<<<< HEAD
            System.out.println("2. Salir del sistema");
            System.out.print("Selecciona una opción: ");
            try {
                int opt = Integer.parseInt(sc.nextLine().trim());
                switch (opt) {
                    case 1 -> {
                        mostrarMenuCitas(name);
                        inMenu = false;
                    }
                    case 2 -> {
                        System.out.println("\nGracias por usar el sistema Marte. ¡Hasta pronto, " + name + "!");
                        inMenu = false;
                        System.exit(0);
=======
            System.out.println("2. Volver al menú principal");
            System.out.print("Selecciona una opción: ");
            try {
                int opt = Integer.parseInt(scanner.nextLine().trim());
                switch (opt) {
                    case 1 -> {
                        mostrarMenuCitas(name);
                    }
                    case 2 -> {
                        System.out.println("\nVolviendo al menú principal...");
                        inMenu = false;
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
                    }
                    default -> System.out.println("Opción no válida. Ingresa 1 o 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingresa un número válido.");
            }
        }
    }

    /** Menú de gestión de citas médicas. */
    private void mostrarMenuCitas(String name) {
<<<<<<< HEAD
        Scanner sc = new Scanner(System.in);
=======
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
        // Obtener la cédula del paciente para poder agendar citas
        Patient paciente = PatientRepository.findByName(name);
        String patientId = paciente != null ? paciente.id() : "";
        
<<<<<<< HEAD
        AppointmentService appointmentService = new AppointmentService();
=======
        AppointmentService appointmentService = new AppointmentService(scanner);
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
        boolean inMenu = true;
        
        while (inMenu) {
            System.out.println("\n========================================");
            System.out.println("   MENÚ DE CITAS — " + name.toUpperCase());
            System.out.println("========================================");
            System.out.println("1. Agendar nueva cita");
            System.out.println("2. Ver mis citas");
            System.out.println("3. Ver historial de citas");
            System.out.println("4. Cancelar una cita");
<<<<<<< HEAD
            System.out.println("5. Salir");
            System.out.print("Selecciona una opción: ");
            try {
                int opt = Integer.parseInt(sc.nextLine().trim());
=======
            System.out.println("5. Volver al menú principal");
            System.out.print("Selecciona una opción: ");
            try {
                int opt = Integer.parseInt(scanner.nextLine().trim());
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
                switch (opt) {
                    case 1 -> appointmentService.agendarCita(name, patientId);
                    case 2 -> appointmentService.verMisCitas(name);
                    case 3 -> appointmentService.verHistorialCitas(name);
                    case 4 -> appointmentService.cancelarCita(name);
                    case 5 -> {
<<<<<<< HEAD
                        System.out.println("\nSaliendo. ¡Hasta pronto, " + name + "!");
                        inMenu = false;
                        System.exit(0);
=======
                        System.out.println("\nVolviendo al menú principal...");
                        inMenu = false;
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
                    }
                    default -> System.out.println("Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingresa un número válido.");
            }
        }
    }
}
