package com.sigp.controller;

import com.sigp.model.Doctor;
import com.sigp.model.User;
import com.sigp.repository.DoctorRepository;
import com.sigp.service.AppointmentService;
import java.util.List;
import java.util.Scanner;

/**
 * Panel de doctor para que el médico consulte y complete sus citas.
 */
public class DoctorDashboard {

    private final Scanner scanner = new Scanner(System.in);
    private final AppointmentService appointmentService = new AppointmentService();
    private final DoctorRepository doctorRepository = new DoctorRepository();

    public void showDoctorMenu(User user) {
        System.out.println("\n--- MENÚ DE MÉDICO ---");

        List<Doctor> doctores = doctorRepository.obtenerDoctores();
        if (doctores.isEmpty()) {
            System.out.println("No hay doctores registrados en el sistema. Pide al administrador que registre los datos del médico.");
            return;
        }

        System.out.println("Selecciona tu doctor entre la lista:");
        for (Doctor doctor : doctores) {
            System.out.println(doctor.getId() + ". " + doctor.getNombreCompleto() + " (" + doctor.getEspecialidad() + ")");
        }
        System.out.print("ID del doctor: ");

        try {
            int doctorId = Integer.parseInt(scanner.nextLine().trim());
            Doctor doctor = doctorRepository.buscarPorId(doctorId);
            if (doctor == null) {
                System.out.println("ID de doctor inválido.");
                return;
            }

            boolean keepRunning = true;
            while (keepRunning) {
                System.out.println("\n--- BIENVENIDO DR. " + doctor.getNombreCompleto().toUpperCase() + " ---");
                System.out.println("1. Ver mis citas");
                System.out.println("2. Completar una cita");
                System.out.println("3. Salir");
                System.out.print("Selecciona una opción: ");

                String option = scanner.nextLine().trim();
                switch (option) {
                    case "1" -> appointmentService.verMisCitasDoctor(doctorId, doctor.getNombreCompleto());
                    case "2" -> appointmentService.completarCita(doctorId);
                    case "3" -> {
                        System.out.println("Cerrando sesión de médico...");
                        keepRunning = false;
                    }
                    default -> System.out.println("Opción no válida.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Por favor ingresa un número válido para el ID del doctor.");
        }
    }
}
