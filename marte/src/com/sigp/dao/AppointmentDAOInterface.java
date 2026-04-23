package com.sigp.dao;

import com.sigp.model.Appointment;

import java.util.List;
import java.util.Optional;

public interface AppointmentDAOInterface {
    Appointment create(Appointment appointment);

    Optional<Appointment> findById(int id);

    List<Appointment> findAll();

    List<Appointment> findByPatientName(String patientName);

    List<Appointment> findByPatientId(String patientId);

    List<Appointment> findByDoctorId(int doctorId);

    List<Appointment> findByPatientAndStatus(String patientName, String status);

    List<Appointment> findByDoctorAndStatus(int doctorId, String status);

    List<Appointment> findPatientHistory(String patientName);

    boolean update(Appointment appointment);

    boolean deleteById(int id);
}
