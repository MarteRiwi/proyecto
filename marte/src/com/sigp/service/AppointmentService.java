package com.sigp.service;

import com.sigp.model.Appointment;
import com.sigp.model.Doctor;
import com.sigp.repository.AppointmentRepository;
import com.sigp.repository.DoctorRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * Servicio de gestión de citas médicas.
 * Permite a los pacientes agendar citas, ver su historial, y cancelas citas.
 * Permite a los doctores completar citas registrando costos.
 */
public class AppointmentService {

    private DoctorRepository doctorRepository;
    private Scanner scanner;

    public AppointmentService() {
        this.doctorRepository = new DoctorRepository();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Permite a un paciente agendar una nueva cita con un doctor.
     *
     * @param patientName   Nombre del paciente
     * @param patientId     Cédula del paciente
     */
    public void agendarCita(String patientName, String patientId) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("AGENDAR NUEVA CITA");
        System.out.println("════════════════════════════════════════════════════════");

        // Mostrar doctores disponibles
        List<Doctor> doctores = doctorRepository.obtenerDoctores();
        if (doctores.isEmpty()) {
            System.out.println("No hay doctores disponibles en el sistema.");
            return;
        }

        System.out.println("\nDoctores disponibles:\n");
        for (Doctor doctor : doctores) {
            System.out.println((doctor.getId()) + ". " + doctor.getNombreCompleto()
                    + " - Especialidad: " + doctor.getEspecialidad());
        }

        System.out.print("\nSelecciona el número del doctor: ");
        try {
            int doctorChoice = Integer.parseInt(scanner.nextLine().trim());
            Doctor selectedDoctor = doctorRepository.buscarPorId(doctorChoice);

            if (selectedDoctor == null) {
                System.out.println("Doctor no encontrado.");
                return;
            }

            // Solicitar fecha y hora
            System.out.println("\nIngresa la fecha y hora de la cita");
            System.out.print("Fecha (dd/MM/yyyy): ");
            String dateStr = scanner.nextLine().trim();
            System.out.print("Hora (HH:mm): ");
            String timeStr = scanner.nextLine().trim();

            LocalDateTime appointmentDateTime = parseFechaHora(dateStr, timeStr);
            if (appointmentDateTime == null) {
                System.out.println("Formato de fecha u hora inválido.");
                return;
            }

            // Validar que la fecha no sea en el pasado
            if (appointmentDateTime.isBefore(LocalDateTime.now())) {
                System.out.println("No puedes agendar citas en el pasado.");
                return;
            }

            // Crear la cita
            Appointment appointment = new Appointment(
                    patientName,
                    patientId,
                    selectedDoctor.getId(),
                    selectedDoctor.getNombreCompleto(),
                    selectedDoctor.getEspecialidad(),
                    appointmentDateTime
            );

            AppointmentRepository.addAppointment(appointment);

            System.out.println("\n✓ Cita agendada exitosamente!");
            System.out.println(appointment.toDetailedString());

        } catch (NumberFormatException e) {
            System.out.println("Por favor ingresa un número válido.");
        }
    }

    /**
     * Muestra el historial de citas completadas del paciente.
     *
     * @param patientName Nombre del paciente
     */
    public void verHistorialCitas(String patientName) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("HISTORIAL DE CITAS - " + patientName.toUpperCase());
        System.out.println("════════════════════════════════════════════════════════");

        List<Appointment> history = AppointmentRepository.getPatientHistory(patientName);

        if (history.isEmpty()) {
            System.out.println("No tienes citas completadas aún.");
            return;
        }

        double totalCost = 0;
        System.out.println();
        for (Appointment apt : history) {
            System.out.println(apt.toDetailedString());
            totalCost += apt.getCost();
        }

        System.out.println(String.format("TOTAL GASTADO EN CITAS: $%.2f\n", totalCost));
    }

    /**
     * Muestra todas las citas del paciente (pendientes, en progreso, completadas).
     *
     * @param patientName Nombre del paciente
     */
    public void verMisCitas(String patientName) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("MIS CITAS - " + patientName.toUpperCase());
        System.out.println("════════════════════════════════════════════════════════");

        List<Appointment> citas = AppointmentRepository.findByPatientName(patientName);

        if (citas.isEmpty()) {
            System.out.println("No tienes citas agendadas.");
            return;
        }

        System.out.println();
        for (Appointment apt : citas) {
            System.out.println(apt.toDetailedString());
        }
    }

    /**
     * Permite al paciente cancelar una cita pendiente.
     *
     * @param patientName Nombre del paciente
     */
    public void cancelarCita(String patientName) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("CANCELAR CITA");
        System.out.println("════════════════════════════════════════════════════════");

        // Mostrar citas pendientes
        List<Appointment> citasPendientes = AppointmentRepository
                .findByPatientAndStatus(patientName, "PENDIENTE");

        if (citasPendientes.isEmpty()) {
            System.out.println("No tienes citas pendientes para cancelar.");
            return;
        }

        System.out.println("\nCitas pendientes:\n");
        for (Appointment apt : citasPendientes) {
            System.out.println((citasPendientes.indexOf(apt) + 1) + ". " + apt);
        }

        System.out.print("\nSelecciona el número de la cita a cancelar: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > citasPendientes.size()) {
                System.out.println("Selección inválida.");
                return;
            }

            Appointment appointmentToCanccel = citasPendientes.get(choice - 1);
            appointmentToCanccel.cancelar();
            AppointmentRepository.updateAppointment(appointmentToCanccel);

            System.out.println("\n✓ Cita cancelada exitosamente.");

        } catch (NumberFormatException e) {
            System.out.println("Por favor ingresa un número válido.");
        }
    }

    /**
     * Permite a un doctor ver sus citas pendientes y completarlas.
     *
     * @param doctorId ID del doctor
     * @param doctorName Nombre del doctor
     */
    public void verMisCitasDoctor(int doctorId, String doctorName) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("MIS CITAS COMO DOCTOR - " + doctorName.toUpperCase());
        System.out.println("════════════════════════════════════════════════════════");

        List<Appointment> todasLasCitas = AppointmentRepository.findByDoctorId(doctorId);

        if (todasLasCitas.isEmpty()) {
            System.out.println("No tienes citas asignadas.");
            return;
        }

        System.out.println();
        for (Appointment apt : todasLasCitas) {
            System.out.println(apt.toDetailedString());
        }
    }

    /**
     * Permite a un doctor completar una cita registrando el costo.
     *
     * @param doctorId ID del doctor
     */
    public void completarCita(int doctorId) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("COMPLETAR CITA");
        System.out.println("════════════════════════════════════════════════════════");

        List<Appointment> citasDisponibles = AppointmentRepository
                .getAllAppointments().stream()
                .filter(apt -> apt.getDoctorId() == doctorId &&
                        (apt.getStatus().equals("PENDIENTE") || apt.getStatus().equals("EN_PROGRESO")))
                .toList();

        if (citasDisponibles.isEmpty()) {
            System.out.println("No tienes citas pendientes o en progreso para completar.");
            return;
        }

        System.out.println("\nCitas disponibles para completar:\n");
        for (int i = 0; i < citasDisponibles.size(); i++) {
            System.out.println((i + 1) + ". " + citasDisponibles.get(i));
        }

        System.out.print("\nSelecciona el número de la cita a completar: ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice < 1 || choice > citasDisponibles.size()) {
                System.out.println("Selección inválida.");
                return;
            }

            Appointment appointmentToComplete = citasDisponibles.get(choice - 1);
            if (appointmentToComplete.getStatus().equals("PENDIENTE")) {
                System.out.println("La cita está pendiente. Se completará directamente.");
            }

            System.out.print("Costo de la cita ($): ");
            double cost = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Notas de la cita (opcional): ");
            String notes = scanner.nextLine().trim();

            appointmentToComplete.completar(cost, notes);
            AppointmentRepository.updateAppointment(appointmentToComplete);

            System.out.println("\n✓ Cita completada exitosamente.");
            System.out.println(appointmentToComplete.toDetailedString());

        } catch (NumberFormatException e) {
            System.out.println("Por favor ingresa valores válidos.");
        }
    }

    /**
     * Permite a un doctor ver una cita pendiente y cambiar su estado a EN_PROGRESO.
     *
     * @param doctorId ID del doctor
     */
    public void iniciarCita(int doctorId) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("INICIAR CITA");
        System.out.println("════════════════════════════════════════════════════════");

        // Mostrar citas pendientes del doctor
        List<Appointment> citasPendientes = AppointmentRepository
                .findByDoctorAndStatus(doctorId, "PENDIENTE");

        if (citasPendientes.isEmpty()) {
            System.out.println("No tienes citas pendientes.");
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

            Appointment appointmentToStart = citasPendientes.get(choice - 1);
            appointmentToStart.setStatus("EN_PROGRESO");
            AppointmentRepository.updateAppointment(appointmentToStart);

            System.out.println("\n✓ Cita iniciada. Estado: EN_PROGRESO");
            System.out.println(appointmentToStart.toDetailedString());

        } catch (NumberFormatException e) {
            System.out.println("Por favor ingresa un número válido.");
        }
    }

    /**
     * Convierte strings de fecha y hora al formato LocalDateTime.
     *
     * @param dateStr Fecha en formato dd/MM/yyyy
     * @param timeStr Hora en formato HH:mm
     * @return LocalDateTime o null si el formato es inválido
     */
    private LocalDateTime parseFechaHora(String dateStr, String timeStr) {
        try {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            String combined = dateStr + " " + timeStr;
            return LocalDateTime.parse(combined, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
