package com.sigp.repository;

import com.sigp.dao.impl.AppointmentDAO;
import com.sigp.model.Appointment;
import java.util.List;

/**
 * Repositorio de citas médicas.
 * Implementación de acceso a datos sobre PostgreSQL.
 */
public class AppointmentRepository {

    private static final AppointmentDAO appointmentDao = new AppointmentDAO();

    public static void addAppointment(Appointment appointment) {
        appointmentDao.create(appointment);
    }

    public static List<Appointment> getAllAppointments() {
        return appointmentDao.findAll();
    }

    public static Appointment findById(int id) {
        return appointmentDao.findById(id).orElse(null);
    }

    public static List<Appointment> findByPatientName(String patientName) {
        return appointmentDao.findByPatientName(patientName);
    }

    public static List<Appointment> findByPatientId(String patientId) {
        return appointmentDao.findByPatientId(patientId);
    }

    public static List<Appointment> findByDoctorId(int doctorId) {
        return appointmentDao.findByDoctorId(doctorId);
    }

    public static List<Appointment> findByPatientAndStatus(String patientName, String status) {
        return appointmentDao.findByPatientAndStatus(patientName, status);
    }

    public static List<Appointment> findByDoctorAndStatus(int doctorId, String status) {
        return appointmentDao.findByDoctorAndStatus(doctorId, status);
    }

    public static boolean removeById(int id) {
        return appointmentDao.deleteById(id);
    }

    public static boolean updateAppointment(Appointment appointment) {
        return appointmentDao.update(appointment);
    }

    public static List<Appointment> getPatientHistory(String patientName) {
        return appointmentDao.findPatientHistory(patientName);
    }

    public static int generateNextId() {
        List<Appointment> allAppointments = appointmentDao.findAll();
        int max = allAppointments.stream().mapToInt(Appointment::getId).max().orElse(0);
        return max + 1;
    }

    public static void clear() {
        for (Appointment appointment : appointmentDao.findAll()) {
            appointmentDao.deleteById(appointment.getId());
        }
    }
}
