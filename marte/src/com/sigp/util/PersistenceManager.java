package com.sigp.util;

import com.sigp.model.Appointment;
import com.sigp.model.Doctor;
import com.sigp.model.Patient;
import com.sigp.model.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gestor de persistencia para el sistema.
 * Permite guardar y cargar doctores, usuarios, pacientes y citas en archivos locales.
 */
public class PersistenceManager {

    private static final String DATA_DIR = "data";
    private static final String DOCTORS_FILE = DATA_DIR + "/doctores.txt";
    private static final String USERS_FILE = DATA_DIR + "/usuarios.txt";
    private static final String PATIENTS_FILE = DATA_DIR + "/pacientes.txt";
    private static final String APPOINTMENTS_FILE = DATA_DIR + "/citas.txt";

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Error creando directorio de datos: " + e.getMessage());
        }
    }

    // ── Doctores ─────────────────────────────────────────────────────────────────

    public static void guardarDoctores(List<Doctor> doctores, int siguienteId) {
        try {
            StringBuilder contenido = new StringBuilder();
            contenido.append(siguienteId).append("\n");
            for (Doctor doctor : doctores) {
                contenido.append(doctor.getId()).append("|")
                        .append(escapeField(doctor.getNombreCompleto())).append("|")
                        .append(doctor.getEdad()).append("|")
                        .append(escapeField(doctor.getCedula())).append("|")
                        .append(escapeField(doctor.getEspecialidad())).append("\n");
            }
            Files.write(Paths.get(DOCTORS_FILE), contenido.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Error al guardar doctores: " + e.getMessage());
        }
    }

    public static Map<String, Object> cargarDoctores() {
        Map<String, Object> resultado = new HashMap<>();
        List<Doctor> doctores = new ArrayList<>();
        int siguienteId = 1;

        try {
            if (Files.exists(Paths.get(DOCTORS_FILE))) {
                List<String> lineas = Files.readAllLines(Paths.get(DOCTORS_FILE));
                if (!lineas.isEmpty()) {
                    try {
                        siguienteId = Integer.parseInt(lineas.get(0));
                    } catch (NumberFormatException e) {
                        siguienteId = 1;
                    }
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

    private static Doctor parsearDoctor(String linea) {
        try {
            String[] partes = linea.split("\\|");
            if (partes.length == 5) {
                Doctor doctor = new Doctor(
                        unescapeField(partes[1]),
                        Integer.parseInt(partes[2]),
                        unescapeField(partes[3]),
                        unescapeField(partes[4])
                );
                doctor.setId(Integer.parseInt(partes[0]));
                return doctor;
            }
        } catch (Exception e) {
            System.err.println("Error parseando doctor: " + e.getMessage());
        }
        return null;
    }

    // ── Usuarios ────────────────────────────────────────────────────────────────

    public static void guardarUsuarios(List<User> usuarios) {
        try {
            StringBuilder contenido = new StringBuilder();
            for (User user : usuarios) {
                contenido.append(escapeField(user.username())).append("|")
                        .append(escapeField(user.password())).append("|")
                        .append(escapeField(user.role())).append("\n");
            }
            Files.write(Paths.get(USERS_FILE), contenido.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Error al guardar usuarios: " + e.getMessage());
        }
    }

    public static Map<String, Object> cargarUsuarios() {
        Map<String, Object> resultado = new HashMap<>();
        List<User> usuarios = new ArrayList<>();
        try {
            if (Files.exists(Paths.get(USERS_FILE))) {
                List<String> lineas = Files.readAllLines(Paths.get(USERS_FILE));
                for (String linea : lineas) {
                    String registro = linea.trim();
                    if (!registro.isEmpty()) {
                        User user = parsearUsuario(registro);
                        if (user != null) {
                            usuarios.add(user);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar usuarios: " + e.getMessage());
        }
        resultado.put("usuarios", usuarios);
        return resultado;
    }

    private static User parsearUsuario(String linea) {
        try {
            String[] partes = linea.split("\\|");
            if (partes.length == 3) {
                return new User(
                        unescapeField(partes[0]),
                        unescapeField(partes[1]),
                        unescapeField(partes[2])
                );
            }
        } catch (Exception e) {
            System.err.println("Error parseando usuario: " + e.getMessage());
        }
        return null;
    }

    // ── Pacientes ──────────────────────────────────────────────────────────────

    public static void guardarPacientes(List<Patient> pacientes) {
        try {
            StringBuilder contenido = new StringBuilder();
            for (Patient paciente : pacientes) {
                contenido.append(escapeField(paciente.name())).append("|")
                        .append(escapeField(paciente.nationality())).append("|")
                        .append(escapeField(paciente.phone())).append("|")
                        .append(escapeField(paciente.email())).append("|")
                        .append(paciente.age()).append("|")
                        .append(escapeField(paciente.id())).append("\n");
            }
            Files.write(Paths.get(PATIENTS_FILE), contenido.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Error al guardar pacientes: " + e.getMessage());
        }
    }

    public static Map<String, Object> cargarPacientes() {
        Map<String, Object> resultado = new HashMap<>();
        List<Patient> pacientes = new ArrayList<>();
        try {
            if (Files.exists(Paths.get(PATIENTS_FILE))) {
                List<String> lineas = Files.readAllLines(Paths.get(PATIENTS_FILE));
                for (String linea : lineas) {
                    String registro = linea.trim();
                    if (!registro.isEmpty()) {
                        Patient paciente = parsearPaciente(registro);
                        if (paciente != null) {
                            pacientes.add(paciente);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar pacientes: " + e.getMessage());
        }
        resultado.put("pacientes", pacientes);
        return resultado;
    }

    private static Patient parsearPaciente(String linea) {
        try {
            String[] partes = linea.split("\\|");
            if (partes.length == 6) {
                return new Patient(
                        unescapeField(partes[0]),
                        unescapeField(partes[1]),
                        unescapeField(partes[2]),
                        unescapeField(partes[3]),
                        Integer.parseInt(partes[4]),
                        unescapeField(partes[5])
                );
            }
        } catch (Exception e) {
            System.err.println("Error parseando paciente: " + e.getMessage());
        }
        return null;
    }

    // ── Citas ──────────────────────────────────────────────────────────────────

    public static void guardarCitas(List<Appointment> citas, int siguienteId) {
        try {
            StringBuilder contenido = new StringBuilder();
            contenido.append(siguienteId).append("\n");
            for (Appointment cita : citas) {
                contenido.append(cita.getId()).append("|")
                        .append(escapeField(cita.getPatientName())).append("|")
                        .append(escapeField(cita.getPatientId())).append("|")
                        .append(cita.getDoctorId()).append("|")
                        .append(escapeField(cita.getDoctorName())).append("|")
                        .append(escapeField(cita.getDoctorSpecialty())).append("|")
                        .append(cita.getAppointmentDateTime().format(DATE_TIME_FORMATTER)).append("|")
                        .append(cita.getStatus()).append("|")
                        .append(cita.getCost()).append("|")
                        .append(escapeField(cita.getNotes())).append("\n");
            }
            Files.write(Paths.get(APPOINTMENTS_FILE), contenido.toString().getBytes());
        } catch (IOException e) {
            System.err.println("Error al guardar citas: " + e.getMessage());
        }
    }

    public static Map<String, Object> cargarCitas() {
        Map<String, Object> resultado = new HashMap<>();
        List<Appointment> citas = new ArrayList<>();
        int siguienteId = 1;

        try {
            if (Files.exists(Paths.get(APPOINTMENTS_FILE))) {
                List<String> lineas = Files.readAllLines(Paths.get(APPOINTMENTS_FILE));
                if (!lineas.isEmpty()) {
                    try {
                        siguienteId = Integer.parseInt(lineas.get(0));
                    } catch (NumberFormatException e) {
                        siguienteId = 1;
                    }
                    for (int i = 1; i < lineas.size(); i++) {
                        String linea = lineas.get(i).trim();
                        if (!linea.isEmpty()) {
                            Appointment cita = parsearCita(linea);
                            if (cita != null) {
                                citas.add(cita);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar citas: " + e.getMessage());
        }

        resultado.put("citas", citas);
        resultado.put("siguienteId", siguienteId);
        return resultado;
    }

    private static Appointment parsearCita(String linea) {
        try {
            String[] partes = linea.split("\\|");
            if (partes.length == 10) {
                Appointment cita = new Appointment(
                        unescapeField(partes[1]),
                        unescapeField(partes[2]),
                        Integer.parseInt(partes[3]),
                        unescapeField(partes[4]),
                        unescapeField(partes[5]),
                        LocalDateTime.parse(unescapeField(partes[6]), DATE_TIME_FORMATTER)
                );
                cita.setId(Integer.parseInt(partes[0]));
                cita.setStatus(unescapeField(partes[7]));
                cita.setCost(Double.parseDouble(partes[8]));
                cita.setNotes(unescapeField(partes[9]));
                return cita;
            }
        } catch (Exception e) {
            System.err.println("Error parseando cita: " + e.getMessage());
        }
        return null;
    }

    private static String escapeField(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("|", "\\p")
                .replace("\n", "\\n");
    }

    private static String unescapeField(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\n", "\n")
                .replace("\\p", "|")
                .replace("\\\\", "\\");
    }

    /**
     * Limpia los datos guardados (útil para testing o reset)
     */
    public static void limpiarDatos() {
        try {
            Files.deleteIfExists(Paths.get(DOCTORS_FILE));
            Files.deleteIfExists(Paths.get(USERS_FILE));
            Files.deleteIfExists(Paths.get(PATIENTS_FILE));
            Files.deleteIfExists(Paths.get(APPOINTMENTS_FILE));
        } catch (IOException e) {
            System.err.println("Error al limpiar datos: " + e.getMessage());
        }
    }
}
