package com.sigp.repository;

import com.sigp.dao.impl.PatientDAO;
import com.sigp.model.Patient;
import java.util.List;

/**
 * Repositorio de pacientes.
 * Implementación de acceso a datos sobre PostgreSQL.
 */
public class PatientRepository {

    private static final PatientDAO patientDao = new PatientDAO();

    /** Agrega un nuevo paciente a la lista. */
    public static void addPatient(Patient patient) {
        if (findByEmail(patient.email()) != null) {
            throw new IllegalArgumentException("Ya existe un paciente registrado con ese email.");
        }
        if (findById(patient.id()) != null) {
            throw new IllegalArgumentException("Ya existe un paciente registrado con esa cédula.");
        }
        patientDao.create(patient);
    }

    /** Retorna la lista completa de pacientes. */
    public static List<Patient> getPatientList() {
        return patientDao.findAll();
    }

    /** Busca un paciente por nombre (sin distinguir mayúsculas). */
    public static Patient findByName(String name) {
        return patientDao.findByName(name).orElse(null);
    }

    /** Busca un paciente por su cédula (ID). */
    public static Patient findById(String id) {
        return patientDao.findById(id).orElse(null);
    }

    /** Busca un paciente por email (sin distinguir mayúsculas). */
    public static Patient findByEmail(String email) {
        return patientDao.findByEmail(email).orElse(null);
    }

    /** Elimina un paciente por nombre. Retorna true si fue eliminado. */
    public static boolean removeByName(String name) {
        Patient patient = findByName(name);
        if (patient == null) {
            return false;
        }
        return patientDao.deleteById(patient.id());
    }
}
