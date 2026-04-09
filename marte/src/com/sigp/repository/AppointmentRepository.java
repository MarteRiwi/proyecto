package com.sigp.repository;

import com.sigp.model.Appointment;
import com.sigp.util.PersistenceManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repositorio de citas médicas.
 * Maneja el almacenamiento en memoria de todas las citas registradas.
 * Persiste las citas en un archivo local para mantener los datos entre ejecuciones.
 */
public class AppointmentRepository {

    private static final List<Appointment> appointments = new ArrayList<>();
    private static int nextId = 1;

    static {
        cargarCitas();
    }

    public static void addAppointment(Appointment appointment) {
        appointments.add(appointment);
        persist();
    }

    public static List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
    }

    public static Appointment findById(int id) {
        for (Appointment apt : appointments) {
            if (apt.getId() == id) {
                return apt;
            }
        }
        return null;
    }

    public static List<Appointment> findByPatientName(String patientName) {
        return appointments.stream()
                .filter(apt -> apt.getPatientName().equalsIgnoreCase(patientName))
                .collect(Collectors.toList());
    }

    public static List<Appointment> findByPatientId(String patientId) {
        return appointments.stream()
                .filter(apt -> apt.getPatientId().equals(patientId))
                .collect(Collectors.toList());
    }

    public static List<Appointment> findByDoctorId(int doctorId) {
        return appointments.stream()
                .filter(apt -> apt.getDoctorId() == doctorId)
                .collect(Collectors.toList());
    }

    public static List<Appointment> findByPatientAndStatus(String patientName, String status) {
        return appointments.stream()
                .filter(apt -> apt.getPatientName().equalsIgnoreCase(patientName)
                        && apt.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public static List<Appointment> findByDoctorAndStatus(int doctorId, String status) {
        return appointments.stream()
                .filter(apt -> apt.getDoctorId() == doctorId && apt.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public static boolean removeById(int id) {
        boolean removed = appointments.removeIf(apt -> apt.getId() == id);
        if (removed) {
            persist();
        }
        return removed;
    }

    public static boolean updateAppointment(Appointment appointment) {
        Appointment existing = findById(appointment.getId());
        if (existing != null) {
            appointments.remove(existing);
            appointments.add(appointment);
            persist();
            return true;
        }
        return false;
    }

    public static List<Appointment> getPatientHistory(String patientName) {
        return appointments.stream()
                .filter(apt -> apt.getPatientName().equalsIgnoreCase(patientName)
                        && apt.getStatus().equals("COMPLETADA"))
                .collect(Collectors.toList());
    }

    public static int generateNextId() {
        return nextId++;
    }

    public static void clear() {
        appointments.clear();
        persist();
    }

    private static void persist() {
        PersistenceManager.guardarCitas(appointments, nextId);
    }

    private static void cargarCitas() {
        Map<String, Object> datos = PersistenceManager.cargarCitas();
        List<Appointment> citasCargadas = (List<Appointment>) datos.get("citas");
        Integer siguienteId = (Integer) datos.get("siguienteId");
        if (citasCargadas != null) {
            appointments.clear();
            appointments.addAll(citasCargadas);
        }
        if (siguienteId != null && siguienteId > 0) {
            nextId = siguienteId;
        }
        if (!appointments.isEmpty()) {
            int maxId = appointments.stream().mapToInt(Appointment::getId).max().orElse(nextId - 1);
            if (maxId >= nextId) {
                nextId = maxId + 1;
            }
        }
        Appointment.setNextId(nextId);
    }
}
