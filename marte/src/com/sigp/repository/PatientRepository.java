package com.sigp.repository;

import com.sigp.model.Patient;
<<<<<<< HEAD

import java.util.ArrayList;
import java.util.List;
=======
import com.sigp.util.PersistenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)

/**
 * Repositorio de pacientes.
 * Maneja el almacenamiento en memoria de todos los pacientes registrados.
 * Diseño basado en el proyecto de Esteban.
 */
public class PatientRepository {

    private static final List<Patient> patientList = new ArrayList<>();

<<<<<<< HEAD
    /** Agrega un nuevo paciente a la lista. */
    public static void addPatient(Patient patient) {
        patientList.add(patient);
=======
    static {
        Map<String, Object> datos = PersistenceManager.cargarPacientes();
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
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
    }

    /** Retorna la lista completa de pacientes. */
    public static List<Patient> getPatientList() {
<<<<<<< HEAD
        return patientList;
=======
        return new ArrayList<>(patientList);
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
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

<<<<<<< HEAD
    /** Elimina un paciente por nombre. Retorna true si fue eliminado. */
    public static boolean removeByName(String name) {
        return patientList.removeIf(p -> p.name().equalsIgnoreCase(name));
    }
}
=======
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
>>>>>>> 477a5e3 (Feat: Cambios en el menu rol paciente salida y gestion)
