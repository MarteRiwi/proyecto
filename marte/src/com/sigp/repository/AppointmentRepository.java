package com.sigp.repository;

import com.sigp.model.Appointment;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio en memoria para citas médicas.
 */
public class AppointmentRepository {

    private static final List<Appointment> appointmentList = new ArrayList<>();
    private static int nextId = 1;

    /** Crea y guarda una nueva cita con ID autoincremental. */
    public static Appointment addAppointment(String patientName, String doctorName, String dateTime, String reason) {
        Appointment appointment = new Appointment(nextId++, patientName, doctorName, dateTime, reason);
        appointmentList.add(appointment);
        return appointment;
    }

    /** Retorna todas las citas de un paciente por nombre. */
    public static List<Appointment> findByPatientName(String patientName) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointmentList) {
            if (appointment.patientName().equalsIgnoreCase(patientName)) {
                result.add(appointment);
            }
        }
        return result;
    }

    /**
     * Elimina una cita por ID validando que pertenezca al paciente.
     * Retorna true si se eliminó.
     */
    public static boolean removeByIdForPatient(String patientName, int id) {
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment appointment = appointmentList.get(i);
            if (appointment.id() == id && appointment.patientName().equalsIgnoreCase(patientName)) {
                appointmentList.remove(i);
                return true;
            }
        }
        return false;
    }
}
