package com.sigp.repository;

import com.sigp.model.Doctor;

import java.util.ArrayList;
import java.util.List;

public class DoctorRepository {
    private final List<Doctor> doctores;
    private int siguienteId;

    public DoctorRepository() {
        this.doctores = new ArrayList<>();
        this.siguienteId = 1;
    }

    public void guardarDoctor(Doctor doctor) {
        doctor.setId(siguienteId);
        siguienteId++;
        doctores.add(doctor);
    }

    public List<Doctor> obtenerDoctores() {
        return doctores;
    }

    public Doctor buscarPorCedula(String cedula) {
        for (Doctor doctor : doctores) {
            if (doctor.getCedula().equals(cedula)) {
                return doctor;
            }
        }
        return null;
    }

    public boolean eliminarPorCedula(String cedula) {
        Doctor doctor = buscarPorCedula(cedula);
        if (doctor == null) {
            return false;
        }

        doctores.remove(doctor);
        return true;
    }

    public Doctor buscarPorId(int id) {
        for (Doctor doctor : doctores) {
            if (doctor.getId() == id) {
                return doctor;
            }
        }
        return null;
    }
}
