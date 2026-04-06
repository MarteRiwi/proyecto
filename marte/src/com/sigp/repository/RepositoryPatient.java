package com.sigp.repository;

import com.sigp.model.Patient;
import java.util.ArrayList;
import java.util.List;


public class RepositoryPatient {

    private static List<Patient> patient_list = new ArrayList<>();

    public static void addPatient(Patient patient) {
        patient_list.add(patient);
    }

    public static List<Patient> getPatientList() {
        return patient_list;
    }

    public static Patient findByName(String name) {
        for (Patient p : patient_list) {
            if (p.name().equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public static boolean removeByName(String name) {
        return patient_list.removeIf(p -> p.name().equalsIgnoreCase(name));
    }
}
