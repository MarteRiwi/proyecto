package com.sigp.repository;

import com.sigp.model.Patient;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de pacientes.
 * Maneja el almacenamiento en memoria de todos los pacientes registrados.
 * Diseño basado en el proyecto de Esteban.
 */
public class PatientRepository {

    private static final List<Patient> patientList = new ArrayList<>();

    /** Agrega un nuevo paciente a la lista. */
    public static void addPatient(Patient patient) {
        patientList.add(patient);
    }

    /** Retorna la lista completa de pacientes. */
    public static List<Patient> getPatientList() {
        return patientList;
    }

    /** Busca un paciente por nombre (sin distinguir mayúsculas). */
    public static Patient findByName(String name) {
        for (Patient p : patientList) {
            if (p.name().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    /** Busca un paciente por su cédula (ID). */
    public static Patient findById(String id) {
        for (Patient p : patientList) {
            if (p.id().equals(id)) {
                return p;
            }
        }
        return null;
    }

    /** Elimina un paciente por nombre. Retorna true si fue eliminado. */
    public static boolean removeByName(String name) {
        return patientList.removeIf(p -> p.name().equalsIgnoreCase(name));
    }
}