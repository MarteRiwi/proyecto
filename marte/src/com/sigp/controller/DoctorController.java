package com.sigp.controller;

import com.sigp.exception.CustomException;
import com.sigp.model.Doctor;
import com.sigp.service.DoctorService;

import java.util.List;
import java.util.Scanner;

public class DoctorController {
    private final DoctorService service;
    private final Scanner scanner;

    public DoctorController() {
        this.service = new DoctorService();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opcion: ");
            ejecutarOpcion(opcion);
        } while (opcion != 5);
    }

    private void mostrarMenu() {
        System.out.println("\n=== Gestion de Doctores ===");
        System.out.println("1. Registrar doctor");
        System.out.println("2. Ver doctores registrados");
        System.out.println("3. Actualizar doctor por ID");
        System.out.println("4. Borrar doctor por cedula");
        System.out.println("5. Salir");
    }

    private void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1:
                registrarDoctor();
                break;
            case 2:
                listarDoctores();
                break;
            case 3:
                actualizarDoctor();
                break;
            case 4:
                eliminarDoctor();
                break;
            case 5:
                System.out.println("Saliendo del sistema...");
                break;
            default:
                System.out.println("Opcion invalida. Intente nuevamente.");
        }
    }

    private void registrarDoctor() {
        try {
            System.out.println("\nRegistro de Medicos");
            String nombre = leerTexto("Nombre completo: ");
            int edad = leerEntero("Edad: ");
            String cedula = leerTexto("Cedula: ");
            String especialidad = leerTexto("Especialidad: ");

            service.registrarDoctor(nombre, edad, cedula, especialidad);
            Doctor doctorRegistrado = service.obtenerUltimoDoctorRegistrado();
            System.out.println("Doctor registrado correctamente con ID " + doctorRegistrado.getId() + ".");
        } catch (CustomException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void listarDoctores() {
        List<Doctor> doctores = service.obtenerDoctores();

        System.out.println("\nLista de doctores registrados");
        if (doctores.isEmpty()) {
            System.out.println("No hay doctores registrados hasta el momento.");
            return;
        }

        for (Doctor doctor : doctores) {
            System.out.println(doctor);
        }
    }

    private void eliminarDoctor() {
        try {
            String cedula = leerTexto("Ingrese la cedula del doctor a borrar: ");
            service.eliminarDoctorPorCedula(cedula);
            System.out.println("Doctor eliminado correctamente.");
        } catch (CustomException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void actualizarDoctor() {
        try {
            int id = leerEntero("Ingrese el ID del doctor a actualizar: ");
            Doctor doctorActual = service.buscarDoctorPorId(id);

            System.out.println("Doctor encontrado: " + doctorActual);
            String nombre = leerTexto("Nuevo nombre completo: ");
            int edad = leerEntero("Nueva edad: ");
            String cedula = leerTexto("Nueva cedula: ");
            String especialidad = leerTexto("Nueva especialidad: ");

            service.actualizarDoctor(id, nombre, edad, cedula, especialidad);
            System.out.println("Doctor actualizado correctamente.");
        } catch (CustomException exception) {
            System.out.println(exception.getMessage());
        }
    }

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
            } catch (NumberFormatException exception) {
                System.out.println("Debe ingresar un numero valido.");
            }
        }
    }
}
