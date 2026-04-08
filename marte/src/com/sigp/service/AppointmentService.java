package com.sigp.service;

import com.sigp.model.Appointment;
import com.sigp.repository.AppointmentRepository;

import java.util.List;

/**
 * Servicio de citas médicas.
 * Maneja el agendamiento, consulta y cancelación de citas.
 */
public class AppointmentService {

    /** Agenda una nueva cita para el paciente indicado. */
    public Appointment agendarCita(String patientName, String doctorName, String dateTime, String reason) {
        validarTexto(patientName, "El nombre del paciente es obligatorio.");
        validarTexto(doctorName, "El nombre del doctor es obligatorio.");
        validarTexto(dateTime, "La fecha y hora de la cita es obligatoria.");
        validarTexto(reason, "El motivo de la cita es obligatorio.");

        return AppointmentRepository.addAppointment(
            patientName.trim(),
            doctorName.trim(),
            dateTime.trim(),
            reason.trim()
        );
    }

    /** Obtiene todas las citas de un paciente. */
    public List<Appointment> obtenerCitasPaciente(String patientName) {
        validarTexto(patientName, "El nombre del paciente es obligatorio.");
        return AppointmentRepository.findByPatientName(patientName.trim());
    }

    /** Cancela una cita por ID para un paciente específico. */
    public boolean cancelarCita(String patientName, int appointmentId) {
        validarTexto(patientName, "El nombre del paciente es obligatorio.");
        if (appointmentId <= 0) {
            throw new IllegalArgumentException("El ID de la cita debe ser mayor a cero.");
        }
        return AppointmentRepository.removeByIdForPatient(patientName.trim(), appointmentId);
    }

    private void validarTexto(String value, String errorMessage) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
