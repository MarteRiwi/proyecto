package com.sigp.model;

import java.util.ArrayList;
import java.util.List;


public class doctor {
    private static List<Create_doctor> listDoctor = new ArrayList<>();

    public record Create_doctor(String full_name, String cargo, int edad, int cedula, short specialty) {}

    public static void create_doctor(String full_name, String cargo, int edad, int cedula, short specialty) {
        if (full_name == null || !full_name.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{3,60}$")) {
            System.out.println(" Nombre inválido. Solo letras y espacios, entre 3 y 60 caracteres.");
            return;
        }
        if (cargo == null || cargo.trim().isEmpty()) {
            System.out.println(" El cargo no puede estar vacío.");
            return;
        }
        if (edad < 18 || edad > 75) {
            System.out.println(" Edad inválida. Un doctor debe tener entre 18 y 75 años.");
            return;
        }
        String cedulaStr = String.valueOf(cedula);
        if (cedulaStr.length() < 6 || cedulaStr.length() > 10) {
            System.out.println(" Cédula inválida. Debe tener entre 6 y 10 dígitos.");
            return;
        }
        // Verificar cédula duplicada
        for (Create_doctor d : listDoctor) {
            if (d.cedula() == cedula) {
                System.out.println(" Ya existe un doctor con esa cédula.");
                return;
            }
        }
        var nuevoDoctor = new Create_doctor(full_name, cargo, edad, cedula, specialty);
        listDoctor.add(nuevoDoctor);
        System.out.println(" Doctor " + full_name + " creado con éxito.");
    }

    public static boolean eliminarDoctor(int cedula) {
        return listDoctor.removeIf(doc -> doc.cedula() == cedula);
    }

    public static List<Create_doctor> getListDoctor() {
        return listDoctor;
    }
}