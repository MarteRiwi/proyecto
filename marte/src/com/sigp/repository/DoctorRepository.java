package com.sigp.repository;

import com.sigp.model.Doctor;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de doctores.
 * Gestiona el almacenamiento en memoria con ID autoincrementales.
 * Diseño basado en el proyecto de Stiven.
 */
public class DoctorRepository {

    private final List<Doctor> doctores;
    private int siguienteId;

    public DoctorRepository() {
        this.doctores = new ArrayList<>();
        this.siguienteId = 1;
    }

    /** Guarda un doctor asignándole automáticamente un ID único. */
    public void guardarDoctor(Doctor doctor) {
        doctor.setId(siguienteId++);
        doctores.add(doctor);
    }

    /** Retorna la lista completa de doctores registrados. */
    public List<Doctor> obtenerDoctores() {
        return doctores;
    }

    /** Busca un doctor por su número de cédula. Retorna null si no existe. */
    public Doctor buscarPorCedula(String cedula) {
        for (Doctor doctor : doctores) {
            if (doctor.getCedula().equals(cedula)) {
                return doctor;
            }
        }
        return null;
    }

    /** Busca un doctor por su ID interno. Retorna null si no existe. */
    public Doctor buscarPorId(int id) {
        for (Doctor doctor : doctores) {
            if (doctor.getId() == id) {
                return doctor;
            }
        }
        return null;
    }

    /** Elimina un doctor por cédula. Retorna true si fue eliminado. */
    public boolean eliminarPorCedula(String cedula) {
        Doctor doctor = buscarPorCedula(cedula);
        if (doctor == null) return false;
        doctores.remove(doctor);
        return true;
    }
}