package com.sigp.repository;

import com.sigp.dao.impl.DoctorDAO;
import com.sigp.model.Doctor;
import java.util.List;

/**
 * Repositorio de doctores.
 * Implementación de acceso a datos sobre PostgreSQL.
 */
public class DoctorRepository {

    private final DoctorDAO doctorDao;

    public DoctorRepository() {
        this.doctorDao = new DoctorDAO();
    }

    /** Guarda un doctor asignándole automáticamente un ID único. */
    public void guardarDoctor(Doctor doctor) {
        doctorDao.create(doctor);
    }

    /** Retorna la lista completa de doctores registrados. */
    public List<Doctor> obtenerDoctores() {
        return doctorDao.findAll();
    }

    /** Busca un doctor por su número de cédula. Retorna null si no existe. */
    public Doctor buscarPorCedula(String cedula) {
        return doctorDao.findByCedula(cedula).orElse(null);
    }

    /** Busca un doctor por su ID interno. Retorna null si no existe. */
    public Doctor buscarPorId(int id) {
        return doctorDao.findById(id).orElse(null);
    }

    /** Elimina un doctor por cédula. Retorna true si fue eliminado. */
    public boolean eliminarPorCedula(String cedula) {
        Doctor doctor = buscarPorCedula(cedula);
        if (doctor == null) {
            return false;
        }
        return doctorDao.deleteById(doctor.getId());
    }

    public boolean actualizarDoctor(Doctor doctor) {
        return doctorDao.update(doctor);
    }
}