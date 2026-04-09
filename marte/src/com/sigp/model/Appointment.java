package com.sigp.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Modelo de cita médica del sistema hospitalario Marte.
 * Contiene información sobre la cita, el paciente, el doctor, fecha/hora, estado y costo.
 * Soporta estados: PENDIENTE, EN_PROGRESO, COMPLETADA, CANCELADA
 */
public class Appointment {
    
    private static int nextId = 1;
    
    private int id;
    private String patientName;
    private String patientId;          // Cédula del paciente
    private int doctorId;              // ID del doctor
    private String doctorName;         // Nombre del doctor para fácil referencia
    private String doctorSpecialty;    // Especialidad del doctor
    private LocalDateTime appointmentDateTime;
    private String status;             // PENDIENTE, EN_PROGRESO, COMPLETADA, CANCELADA
    private double cost;               // Costo de la cita (0 si aún no está completada)
    private String notes;              // Notas del doctor después de la cita
    
    /**
     * Constructor para crear una nueva cita.
     * El estado inicial es PENDIENTE y el costo es 0.
     */
    public Appointment(String patientName, String patientId, int doctorId, 
                      String doctorName, String doctorSpecialty, LocalDateTime appointmentDateTime) {
        this.id = nextId++;
        this.patientName = patientName;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.doctorSpecialty = doctorSpecialty;
        this.appointmentDateTime = appointmentDateTime;
        this.status = "PENDIENTE";
        this.cost = 0.0;
        this.notes = "";
    }
    
    // ── Getters y Setters ──────────────────────────────────────────────────────
    
    public int getId() {
        return id;
    }
    
    public String getPatientName() {
        return patientName;
    }
    
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public int getDoctorId() {
        return doctorId;
    }
    
    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    public String getDoctorSpecialty() {
        return doctorSpecialty;
    }
    
    public void setDoctorSpecialty(String doctorSpecialty) {
        this.doctorSpecialty = doctorSpecialty;
    }
    
    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }
    
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        if (!status.matches("PENDIENTE|EN_PROGRESO|COMPLETADA|CANCELADA")) {
            throw new IllegalArgumentException("Estado inválido. Debe ser: PENDIENTE, EN_PROGRESO, COMPLETADA o CANCELADA");
        }
        this.status = status;
    }
    
    public double getCost() {
        return cost;
    }
    
    public void setCost(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("El costo no puede ser negativo.");
        }
        this.cost = cost;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes != null ? notes : "";
    }
    
    /**
     * Marca la cita como completada, registrando el costo.
     * Solo se puede completar si está en estado EN_PROGRESO.
     */
    public void completar(double cost, String notes) {
        if (!this.status.equals("EN_PROGRESO")) {
            throw new IllegalStateException("Solo se pueden completar citas que están EN_PROGRESO.");
        }
        this.cost = cost;
        this.notes = notes != null ? notes : "";
        this.status = "COMPLETADA";
    }
    
    /**
     * Cancela la cita. Solo se puede cancelar si está PENDIENTE.
     */
    public void cancelar() {
        if (!this.status.equals("PENDIENTE")) {
            throw new IllegalStateException("Solo se pueden cancelar citas que están PENDIENTE.");
        }
        this.status = "CANCELADA";
    }
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format(
            "ID: %d | Paciente: %s | Doctor: %s (%s) | Fecha: %s | Estado: %s | Costo: $%.2f",
            id, patientName, doctorName, doctorSpecialty, 
            appointmentDateTime.format(formatter), status, cost
        );
    }
    
    /**
     * Retorna una representación más detallada de la cita.
     */
    public String toDetailedString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("════════════════════════════════════════════════════════\n");
        sb.append("CITA MÉDICA\n");
        sb.append("════════════════════════════════════════════════════════\n");
        sb.append(String.format("ID de cita:        %d\n", id));
        sb.append(String.format("Paciente:          %s (Cédula: %s)\n", patientName, patientId));
        sb.append(String.format("Doctor:            %s\n", doctorName));
        sb.append(String.format("Especialidad:      %s\n", doctorSpecialty));
        sb.append(String.format("Fecha y hora:      %s\n", appointmentDateTime.format(formatter)));
        sb.append(String.format("Estado:            %s\n", status));
        sb.append(String.format("Costo:             $%.2f\n", cost));
        if (!notes.isEmpty()) {
            sb.append(String.format("Notas:             %s\n", notes));
        }
        sb.append("════════════════════════════════════════════════════════\n");
        return sb.toString();
    }
}
