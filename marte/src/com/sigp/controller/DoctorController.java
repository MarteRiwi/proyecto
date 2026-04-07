package com.sigp.controller;

import com.sigp.exception.CustomException;
import com.sigp.model.Doctor;
import com.sigp.service.DoctorService;

import java.util.List;
import java.util.Scanner;

/**
 * Controlador de gestión de doctores.
 * Ofrece CRUD completo: registrar, listar, actualizar por ID y eliminar por cédula.
 * Basado en el proyecto de Stiven, adaptado para ser llamado desde AdminController.
 */
public class DoctorController {

    private final DoctorService service;
    private final Scanner scanner;

    public DoctorController() {
        this.service = new DoctorService();
        this.scanner = new Scanner(System.in);
    }

    /** Muestra el submenú de gestión de doctores dentro del panel admin. */
    public void mostrarMenuDoctores() {
        int opcion;
        do {
            System.out.println("\n=== Gestión de Doctores ===");
            System.out.println("1. Registrar doctor");
            System.out.println("2. Ver doctores registrados");
            System.out.println("3. Actualizar doctor por ID");
            System.out.println("4. Eliminar doctor por cédula");
            System.out.println("5. Volver al menú admin");
            opcion = leerEntero("Seleccione una opción: ");

            switch (opcion) {
                case 1 -> registrarDoctor();
                case 2 -> listarDoctores();
                case 3 -> actualizarDoctor();
                case 4 -> eliminarDoctor();
                case 5 -> System.out.println("Volviendo al menú de administración...");
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
        } while (opcion != 5);
    }

    // ── Operaciones CRUD ───────────────────────────────────────────────────────

    private void registrarDoctor() {
        try {
            System.out.println("\nRegistro de Médico");
            String nombre = leerTexto("Nombre completo: ");
            int edad = leerEntero("Edad: ");
            String cedula = leerTexto("Cédula: ");
            String especialidad = leerTexto("Especialidad: ");

            service.registrarDoctor(nombre, edad, cedula, especialidad);
            Doctor registrado = service.obtenerUltimoRegistrado();
            System.out.println("Doctor registrado correctamente con ID " + registrado.getId() + ".");
        } catch (CustomException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarDoctores() {
        List<Doctor> doctores = service.obtenerDoctores();
        System.out.println("\nLista de doctores registrados:");
        if (doctores.isEmpty()) {
            System.out.println("No hay doctores registrados hasta el momento.");
            return;
        }
        for (Doctor doctor : doctores) {
            System.out.println(doctor);
        }
    }

    private void actualizarDoctor() {
        try {
            int id = leerEntero("ID del doctor a actualizar: ");
            Doctor actual = service.buscarDoctorPorId(id);
            System.out.println("Doctor encontrado: " + actual);

            String nombre = leerTexto("Nuevo nombre completo: ");
            int edad = leerEntero("Nueva edad: ");
            String cedula = leerTexto("Nueva cédula: ");
            String especialidad = leerTexto("Nueva especialidad: ");

            service.actualizarDoctor(id, nombre, edad, cedula, especialidad);
            System.out.println("Doctor actualizado correctamente.");
        } catch (CustomException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarDoctor() {
        try {
            String cedula = leerTexto("Cédula del doctor a eliminar: ");
            service.eliminarDoctorPorCedula(cedula);
            System.out.println("Doctor eliminado correctamente.");
        } catch (CustomException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // ── Utilidades de entrada ──────────────────────────────────────────────────

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String valor = scanner.nextLine().trim();
            try {
                return Integer.parseInt(valor);
            } catch (NumberFormatException e) {
                System.out.println("Debe ingresar un número válido.");
            }
        }
    }
}