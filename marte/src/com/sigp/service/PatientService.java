package com.sigp.service;

import com.sigp.model.Appointment;
import com.sigp.model.Patient;
import com.sigp.repository.PatientRepository;
import java.util.List;
import java.util.Scanner;

/**
 * Servicio de registro y consulta de pacientes.
 * Cuando un usuario sin rol admin inicia sesión, este servicio
 * lo guía para completar su ficha como paciente y agendar citas.
 * Basado en el proyecto de Esteban (serviceAsignacionPatient).
 */
public class PatientService {

    private final AppointmentService appointmentService = new AppointmentService();

    /**
     * Solicita los datos del paciente por consola y lo registra en el sistema.
     *
     * @param loginEmail email con el que inició sesión
     */
    public void registrarInformacionPaciente(String loginEmail) {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n--- REGISTRO DE PACIENTE: " + loginEmail.toUpperCase() + " ---");

        try {
            System.out.print("Nombre completo: ");
            String name = sc.nextLine();

            
            System.out.print("Nacionalidad: ");
            String nationality = sc.nextLine();

            System.out.print("Teléfono celular (ej: 3001234567): ");
            String phone = sc.nextLine();

            System.out.print("Edad: ");
            int age = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Número de cédula: ");
            String id = sc.nextLine();

            guardarPaciente(name, nationality, phone, loginEmail, age, id);

        } catch (NumberFormatException e) {
            System.out.println("Error: ingresa un número válido para la edad.");
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    /**
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
        Scanner sc = new Scanner(System.in);
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("¿Qué deseas hacer ahora?");
            System.out.println("1. Ir al menú de citas");
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
                    }
                    default -> System.out.println("Opción no válida. Ingresa 1 o 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingresa un número válido.");
            }
        }
    }

    /** Menú de gestión de citas médicas del paciente. */
    private void mostrarMenuCitas(String name) {
        Scanner sc = new Scanner(System.in);
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n========================================");
            System.out.println("   MENÚ DE CITAS — " + name.toUpperCase());
            System.out.println("========================================");
            System.out.println("1. Agendar nueva cita");
            System.out.println("2. Ver mis citas");
            System.out.println("3. Cancelar una cita");
            System.out.println("4. Salir");
            System.out.print("Selecciona una opción: ");
            try {
                int opt = Integer.parseInt(sc.nextLine().trim());
                switch (opt) {
                    case 1 -> agendarCita(name, sc);
                    case 2 -> verMisCitas(name);
                    case 3 -> cancelarCita(name, sc);
                    case 4 -> {
                        System.out.println("Saliendo. ¡Hasta pronto, " + name + "!");
                        inMenu = false;
                        System.exit(0);
                    }
                    default -> System.out.println("Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingresa un número válido.");
            }
        }
    }

    private void agendarCita(String patientName, Scanner sc) {
        try {
            System.out.print("Nombre del doctor: ");
            String doctorName = sc.nextLine();
            System.out.print("Fecha y hora (ej: 2026-04-10 09:00): ");
            String dateTime = sc.nextLine();
            System.out.print("Motivo de la cita: ");
            String reason = sc.nextLine();

            Appointment appointment = appointmentService.agendarCita(patientName, doctorName, dateTime, reason);
            System.out.println("Cita agendada correctamente con ID " + appointment.id() + ".");
        } catch (IllegalArgumentException e) {
            System.out.println("No fue posible agendar la cita: " + e.getMessage());
        }
    }

    private void verMisCitas(String patientName) {
        try {
            List<Appointment> appointments = appointmentService.obtenerCitasPaciente(patientName);
            System.out.println("\n--- MIS CITAS ---");
            if (appointments.isEmpty()) {
                System.out.println("No tienes citas registradas.");
                return;
            }
            for (Appointment appointment : appointments) {
                System.out.println(
                    "ID: " + appointment.id()
                    + " | Doctor: " + appointment.doctorName()
                    + " | Fecha/Hora: " + appointment.dateTime()
                    + " | Motivo: " + appointment.reason()
                );
            }
        } catch (IllegalArgumentException e) {
            System.out.println("No fue posible consultar las citas: " + e.getMessage());
        }
    }

    private void cancelarCita(String patientName, Scanner sc) {
        System.out.print("ID de la cita a cancelar: ");
        String idStr = sc.nextLine().trim();

        try {
            int appointmentId = Integer.parseInt(idStr);
            boolean deleted = appointmentService.cancelarCita(patientName, appointmentId);
            if (deleted) {
                System.out.println("Cita cancelada correctamente.");
            } else {
                System.out.println("No se encontró una cita con ese ID para tu usuario.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Debes ingresar un ID numérico válido.");
        } catch (IllegalArgumentException e) {
            System.out.println("No fue posible cancelar la cita: " + e.getMessage());
        }
    }
}
