package com.sigp.dao;

import com.sigp.model.Doctor;

import java.util.List;
import java.util.Optional;

public interface DoctorDAOInterface {
    Doctor create(Doctor doctor);

    Optional<Doctor> findById(int id);

    Optional<Doctor> findByCedula(String cedula);

    List<Doctor> findAll();

    boolean update(Doctor doctor);

    boolean deleteById(int id);
}
