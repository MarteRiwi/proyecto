package com.sigp.repository;

import com.sigp.model.Patient;
import com.sigp.util.PersistenceManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de pacientes.
 * Maneja el almacenamiento en memoria de todos los pacientes registrados.
 * Diseño basado en el proyecto de Esteban.
 */
public class PatientRepository {

    private static final List<Patient> patientList = new ArrayList<>();

    static {
        var datos = PersistenceManager.cargarPacientes();
        List<Patient> pacientesCargados = (List<Patient>) datos.get("pacientes");
        if (pacientesCargados != null) {
            patientList.addAll(pacientesCargados);
        }
    }

    /** Agrega un nuevo paciente a la lista. */
    public static void addPatient(Patient patient) {
        if (findByEmail(patient.email()) != null) {
            throw new IllegalArgumentException("Ya existe un paciente registrado con ese email.");
        }
        if (findById(patient.id()) != null) {
            throw new IllegalArgumentException("Ya existe un paciente registrado con esa cédula.");
        }
        patientList.add(patient);
        PersistenceManager.guardarPacientes(patientList);
    }

    /** Retorna la lista completa de pacientes. */
    public static List<Patient> getPatientList() {
        return new ArrayList<>(patientList);
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

    /** Busca un paciente por email (sin distinguir mayúsculas). */
    public static Patient findByEmail(String email) {
        for (Patient p : patientList) {
            if (p.email().equalsIgnoreCase(email)) {
                return p;
            }
        }
        return null;
    }

    /** Elimina un paciente por nombre. Retorna true si fue eliminado. */
    public static boolean removeByName(String name) {
        boolean removed = patientList.removeIf(p -> p.name().equalsIgnoreCase(name));
        if (removed) {
            PersistenceManager.guardarPacientes(patientList);
        }
        return removed;
    }
}
