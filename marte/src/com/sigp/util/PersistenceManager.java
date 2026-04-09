package com.sigp.util;

import com.sigp.model.Doctor;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Gestor de persistencia para doctores usando archivos JSON.
 * Permite guardar y cargar doctores entre ejecuciones.
 */
public class PersistenceManager {

    private static final String DATA_DIR = "data";
    private static final String DOCTORS_FILE = DATA_DIR + "/doctores.txt";

    static {
        // Crear carpeta de datos si no existe
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Error creando directorio de datos: " + e.getMessage());
        }
    }

    /**
     * Guarda la lista de doctores en un archivo de texto
     */
    public static void guardarDoctores(List<Doctor> doctores, int siguienteId) {
        try {
            StringBuilder contenido = new StringBuilder();
            contenido.append(siguienteId).append("\n"); // Primera línea: siguienteId

            for (Doctor doctor : doctores) {
                contenido.append(doctor.getId()).append("|")
                        .append(doctor.getNombreCompleto()).append("|")
                        .append(doctor.getEdad()).append("|")
                        .append(doctor.getCedula()).append("|")
                        .append(doctor.getEspecialidad()).append("\n");
            }

            Files.write(Paths.get(DOCTORS_FILE), contenido.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Error al guardar doctores: " + e.getMessage());
        }
    }

    /**
     * Carga la lista de doctores desde el archivo
     * Retorna un mapa con "doctores" (List) y "siguienteId" (Integer)
     */
    public static Map<String, Object> cargarDoctores() {
        Map<String, Object> resultado = new HashMap<>();
        List<Doctor> doctores = new ArrayList<>();
        int siguienteId = 1;

        try {
            if (Files.exists(Paths.get(DOCTORS_FILE))) {
                List<String> lineas = Files.readAllLines(Paths.get(DOCTORS_FILE));

                if (!lineas.isEmpty()) {
                    // Primera línea es el siguienteId
                    try {
                        siguienteId = Integer.parseInt(lineas.get(0));
                    } catch (NumberFormatException e) {
                        siguienteId = 1;
                    }

                    // Resto de líneas son doctores
                    for (int i = 1; i < lineas.size(); i++) {
                        String linea = lineas.get(i).trim();
                        if (!linea.isEmpty()) {
                            Doctor doctor = parsearDoctor(linea);
                            if (doctor != null) {
                                doctores.add(doctor);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar doctores: " + e.getMessage());
        }

        resultado.put("doctores", doctores);
        resultado.put("siguienteId", siguienteId);
        return resultado;
    }

    /**
     * Parsea una línea de texto a un objeto Doctor
     */
    private static Doctor parsearDoctor(String linea) {
        try {
            String[] partes = linea.split("\\|");
            if (partes.length == 5) {
                Doctor doctor = new Doctor(
                        partes[1],  // nombreCompleto
                        Integer.parseInt(partes[2]),  // edad
                        partes[3],  // cedula
                        partes[4]   // especialidad
                );
                doctor.setId(Integer.parseInt(partes[0])); // id
                return doctor;
            }
        } catch (Exception e) {
            System.err.println("Error parseando doctor: " + e.getMessage());
        }
        return null;
    }

    /**
     * Limpia los datos guardados (útil para testing o reset)
     */
    public static void limpiarDatos() {
        try {
            Files.deleteIfExists(Paths.get(DOCTORS_FILE));
        } catch (IOException e) {
            System.err.println("Error al limpiar datos: " + e.getMessage());
        }
    }
}
