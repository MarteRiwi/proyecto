package com.sigp.repository;

import com.sigp.model.Appointment;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repositorio de citas médicas.
 * Maneja el almacenamiento en memoria de todas las citas registradas.
 */
public class AppointmentRepository {

    private static final List<Appointment> appointments = new ArrayList<>();

    /**
     * Agrega una nueva cita a la lista.
     */
    public static void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    /**
     * Retorna la lista completa de citas.
     */
    public static List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
    }

    /**
     * Busca una cita por su ID.
     */
    public static Appointment findById(int id) {
        for (Appointment apt : appointments) {
            if (apt.getId() == id) {
                return apt;
            }
        }
        return null;
    }

    /**
     * Retorna todas las citas de un paciente específico (por nombre).
     */
    public static List<Appointment> findByPatientName(String patientName) {
        return appointments.stream()
                .filter(apt -> apt.getPatientName().equalsIgnoreCase(patientName))
                .collect(Collectors.toList());
    }

    /**
     * Retorna todas las citas de un paciente específico (por cédula).
     */
    public static List<Appointment> findByPatientId(String patientId) {
        return appointments.stream()
                .filter(apt -> apt.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }

    /**
     * Retorna todas las citas de un doctor específico (por ID).
     */
    public static List<Appointment> findByDoctorId(int doctorId) {
        return appointments.stream()
                .filter(apt -> apt.getDoctorId() == doctorId)
                .collect(Collectors.toList());
    }

    /**
     * Retorna todas las citas de un paciente con un estado específico.
     */
    public static List<Appointment> findByPatientAndStatus(String patientName, String status) {
        return appointments.stream()
                .filter(apt -> apt.getPatientName().equalsIgnoreCase(patientName) 
                            && apt.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    /**
     * Retorna todas las citas de un doctor con un estado específico.
     */
    public static List<Appointment> findByDoctorAndStatus(int doctorId, String status) {
        return appointments.stream()
                .filter(apt -> apt.getDoctorId() == doctorId && apt.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    /**
     * Elimina una cita por su ID. Retorna true si fue eliminada.
     */
    public static boolean removeById(int id) {
        return appointments.removeIf(apt -> apt.getId() == id);
    }

    /**
     * Actualiza una cita existente.
     */
    public static boolean updateAppointment(Appointment appointment) {
        Appointment existing = findById(appointment.getId());
        if (existing != null) {
            appointments.remove(existing);
            appointments.add(appointment);
            return true;
        }
        return false;
    }

    /**
     * Retorna todas las citas completadas de un paciente (para su historial).
     */
    public static List<Appointment> getPatientHistory(String patientName) {
        return appointments.stream()
                .filter(apt -> apt.getPatientName().equalsIgnoreCase(patientName)
                            && apt.getStatus().equals("COMPLETADA"))
                .collect(Collectors.toList());
    }

    /**
     * Limpia todos los datos (útil para testing).
     */
    public static void clear() {
        appointments.clear();
    }
}
