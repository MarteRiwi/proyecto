package com.sigp.service;

import com.sigp.model.Appointment;
import com.sigp.model.Doctor;
import com.sigp.repository.AppointmentRepository;
import com.sigp.repository.DoctorRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Servicio de gestión de doctores.
 * Permite que los doctores vean sus citas, las inicien y las completen con registro de costos.
 */
public class DoctorAppointmentService {

    private DoctorRepository doctorRepository;
    private Scanner scanner;

    public DoctorAppointmentService() {
        this.doctorRepository = new DoctorRepository();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Menú principal para un doctor logueado.
     * Permite ver citas, iniciar citas y completar citas.
     *
     * @param doctorId ID del doctor
     * @param doctorName Nombre del doctor
     */
    public void mostrarMenuDoctorCitas(int doctorId, String doctorName) {
        boolean inMenu = true;

        while (inMenu) {
            System.out.println("\n════════════════════════════════════════════════════════");
            System.out.println("MENÚ DE CITAS MÉDICAS - Dr/Dra. " + doctorName.toUpperCase());
            System.out.println("════════════════════════════════════════════════════════");
            System.out.println("1. Ver todas mis citas");
            System.out.println("2. Ver citas pendientes");
            System.out.println("3. Ver citas en progreso");
            System.out.println("4. Iniciar una cita");
            System.out.println("5. Completar una cita");
            System.out.println("6. Ver estadísticas de citas");
            System.out.println("7. Salir");
            System.out.print("Selecciona una opción: ");

            try {
                int option = Integer.parseInt(scanner.nextLine().trim());

                switch (option) {
                    case 1 -> verTodasMisCitas(doctorId);
                    case 2 -> verCitasPendientes(doctorId);
                    case 3 -> verCitasEnProgreso(doctorId);
                    case 4 -> iniciarCita(doctorId);
                    case 5 -> completarCita(doctorId);
                    case 6 -> verEstadisticas(doctorId);
                    case 7 -> {
                        System.out.println("\nGracias por usar el sistema. ¡Hasta pronto, Dr/Dra. " + doctorName + "!");
                        inMenu = false;
                    }
                    default -> System.out.println("Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingresa un número válido.");
            }
        }
    }

    /**
     * Muestra todas las citas del doctor (cualquier estado).
     */
    private void verTodasMisCitas(int doctorId) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("TODAS MIS CITAS");
        System.out.println("════════════════════════════════════════════════════════");

        List<Appointment> citas = AppointmentRepository.findByDoctorId(doctorId);

        if (citas.isEmpty()) {
            System.out.println("No tienes citas asignadas.");
            return;
        }

        System.out.println();
        for (int i = 0; i < citas.size(); i++) {
            System.out.println((i + 1) + ". " + citas.get(i).toString());
        }
        System.out.println("\nTotal de citas: " + citas.size());
    }

    /**
     * Muestra solo las citas pendientes del doctor.
     */
    private void verCitasPendientes(int doctorId) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("CITAS PENDIENTES");
        System.out.println("════════════════════════════════════════════════════════");

        List<Appointment> citasPendientes = AppointmentRepository
                .findByDoctorAndStatus(doctorId, "PENDIENTE");

        if (citasPendientes.isEmpty()) {
            System.out.println("No tienes citas pendientes.");
            return;
        }

        System.out.println();
        for (int i = 0; i < citasPendientes.size(); i++) {
            Appointment apt = citasPendientes.get(i);
            System.out.println((i + 1) + ". " + apt.toString());
        }
        System.out.println("\nTotal pendientes: " + citasPendientes.size());
    }

    /**
     * Muestra solo las citas en progreso del doctor.
     */
    private void verCitasEnProgreso(int doctorId) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("CITAS EN PROGRESO");
        System.out.println("════════════════════════════════════════════════════════");

        List<Appointment> citasEnProgreso = AppointmentRepository
                .findByDoctorAndStatus(doctorId, "EN_PROGRESO");

        if (citasEnProgreso.isEmpty()) {
            System.out.println("No tienes citas en progreso.");
            return;
        }

        System.out.println();
        for (int i = 0; i < citasEnProgreso.size(); i++) {
            Appointment apt = citasEnProgreso.get(i);
            System.out.println((i + 1) + ". " + apt.toString());
        }
        System.out.println("\nTotal en progreso: " + citasEnProgreso.size());
    }

    /**
     * Permite iniciar una cita (cambiar de PENDIENTE a EN_PROGRESO).
     */
    private void iniciarCita(int doctorId) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("INICIAR CITA");
        System.out.println("════════════════════════════════════════════════════════");

        List<Appointment> citasPendientes = AppointmentRepository
                .findByDoctorAndStatus(doctorId, "PENDIENTE");

        if (citasPendientes.isEmpty()) {
            System.out.println("No tienes citas pendientes para iniciar.");
            return;
        }

        System.out.println("\nCitas pendientes:\n");
        for (int i = 0; i < citasPendientes.size(); i++) {
            System.out.println((i + 1) + ". " + citasPendientes.get(i));
        }

        System.out.print("\nSelecciona el número de la cita a iniciar: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > citasPendientes.size()) {
                System.out.println("Selección inválida.");
                return;
            }

            Appointment appointment = citasPendientes.get(choice - 1);
            appointment.setStatus("EN_PROGRESO");
            AppointmentRepository.updateAppointment(appointment);

            System.out.println("\n✓ Cita iniciada correctamente.");
            System.out.println("Estado actualizado a: EN_PROGRESO");
            System.out.println("\nDetalles de la cita:");
            System.out.println(appointment.toDetailedString());

        } catch (NumberFormatException e) {
            System.out.println("Por favor ingresa un número válido.");
        }
    }

    /**
     * Permite completar una cita registrando costo y notas.
     */
    private void completarCita(int doctorId) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("COMPLETAR CITA");
        System.out.println("════════════════════════════════════════════════════════");

        List<Appointment> citasEnProgreso = AppointmentRepository
                .findByDoctorAndStatus(doctorId, "EN_PROGRESO");

        if (citasEnProgreso.isEmpty()) {
            System.out.println("No tienes citas en progreso para completar.");
            return;
        }

        System.out.println("\nCitas en progreso:\n");
        for (int i = 0; i < citasEnProgreso.size(); i++) {
            System.out.println((i + 1) + ". " + citasEnProgreso.get(i));
        }

        System.out.print("\nSelecciona el número de la cita a completar: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > citasEnProgreso.size()) {
                System.out.println("Selección inválida.");
                return;
            }

            Appointment appointment = citasEnProgreso.get(choice - 1);

            System.out.print("\nIngresa el costo de la consulta ($): ");
            double cost = Double.parseDouble(scanner.nextLine().trim());

            if (cost < 0) {
                System.out.println("El costo no puede ser negativo.");
                return;
            }

            System.out.print("Notas de la cita (diagnóstico, recomendaciones, etc.): ");
            String notes = scanner.nextLine().trim();

            appointment.completar(cost, notes);
            AppointmentRepository.updateAppointment(appointment);

            System.out.println("\n✓ Cita completada exitosamente.");
            System.out.println(appointment.toDetailedString());

        } catch (NumberFormatException e) {
            System.out.println("Por favor ingresa valores válidos (número para costo).");
        }
    }

    /**
     * Muestra estadísticas de las citas del doctor.
     */
    private void verEstadisticas(int doctorId) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("ESTADÍSTICAS DE CITAS");
        System.out.println("════════════════════════════════════════════════════════");

        List<Appointment> todasLasCitas = AppointmentRepository.findByDoctorId(doctorId);
        
        if (todasLasCitas.isEmpty()) {
            System.out.println("No tienes datos de citas aún.");
            return;
        }

        // Contar por estado
        long citasPendientes = todasLasCitas.stream()
                .filter(apt -> apt.getStatus().equals("PENDIENTE"))
                .count();

        long citasEnProgreso = todasLasCitas.stream()
                .filter(apt -> apt.getStatus().equals("EN_PROGRESO"))
                .count();

        long citasCompletadas = todasLasCitas.stream()
                .filter(apt -> apt.getStatus().equals("COMPLETADA"))
                .count();

        long citasCanceladas = todasLasCitas.stream()
                .filter(apt -> apt.getStatus().equals("CANCELADA"))
                .count();

        // Calcular ingresos
        double ingresoTotal = todasLasCitas.stream()
                .filter(apt -> apt.getStatus().equals("COMPLETADA"))
                .mapToDouble(Appointment::getCost)
                .sum();

        double ingresoPromedio = citasCompletadas > 0 ? ingresoTotal / citasCompletadas : 0;

        // Mostrar estadísticas
        System.out.println();
        System.out.println("Total de citas: " + todasLasCitas.size());
        System.out.println();
        System.out.println("ESTADO DE LAS CITAS:");
        System.out.println("  Pendientes:  " + citasPendientes);
        System.out.println("  En progreso: " + citasEnProgreso);
        System.out.println("  Completadas: " + citasCompletadas);
        System.out.println("  Canceladas:  " + citasCanceladas);
        System.out.println();
        System.out.println("FINANZAS:");
        System.out.println(String.format("  Ingreso total:    $%.2f", ingresoTotal));
        System.out.println(String.format("  Ingreso promedio: $%.2f", ingresoPromedio));
        System.out.println();

        // Mostrar próximas citas
        LocalDateTime ahora = LocalDateTime.now();
        List<Appointment> proximasCitas = todasLasCitas.stream()
                .filter(apt -> apt.getAppointmentDateTime().isAfter(ahora))
                .filter(apt -> apt.getStatus().equals("PENDIENTE") || apt.getStatus().equals("EN_PROGRESO"))
                .sorted((a, b) -> a.getAppointmentDateTime().compareTo(b.getAppointmentDateTime()))
                .limit(5)
                .toList();

        if (!proximasCitas.isEmpty()) {
            System.out.println("PRÓXIMAS CITAS (hasta 5):");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (int i = 0; i < proximasCitas.size(); i++) {
                Appointment apt = proximasCitas.get(i);
                System.out.println(String.format("  %d. %s - %s (%s)",
                        i + 1,
                        apt.getPatientName(),
                        apt.getAppointmentDateTime().format(formatter),
                        apt.getStatus()
                ));
            }
        }

        System.out.println();
    }
}
