package com.sigp.model;

/**
 * Modelo de cita médica del sistema.
 * Se guarda en memoria y se asocia a un paciente por nombre.
 */
public record Appointment(int id, String patientName, String doctorName, String dateTime, String reason) {

    public Appointment {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de la cita debe ser mayor a cero.");
        }
        if (patientName == null || patientName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del paciente es obligatorio.");
        }
        if (doctorName == null || doctorName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del doctor es obligatorio.");
        }
        if (dateTime == null || dateTime.trim().isEmpty()) {
            throw new IllegalArgumentException("La fecha y hora de la cita es obligatoria.");
        }
        if (reason == null || reason.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo de la cita es obligatorio.");
        }
    }
}
