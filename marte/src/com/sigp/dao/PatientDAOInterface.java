package com.sigp.dao;

import com.sigp.model.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientDAOInterface {
    Patient create(Patient patient);

    Optional<Patient> findById(String id);

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findByName(String name);

    List<Patient> findAll();

    boolean update(Patient patient);

    boolean deleteById(String id);
}
