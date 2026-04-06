package com.sigp.controller;

import com.sigp.model.doctor;
import com.sigp.model.Patient;
import com.sigp.model.User;
import com.sigp.repository.RepositoryPatient;
import com.sigp.service.serviceAsignacionPatient;
import java.util.Scanner;


public class controllerAdmin {

    private Scanner scanner = new Scanner(System.in);

    public void showAdminMenu(User admin) {
        int option = 0;
        do {
            System.out.println("\n========================================");
            System.out.println("   BIENVENIDO, ADMIN: " + admin.username().toUpperCase());
            System.out.println("========================================");
            System.out.println("1. Crear Doctor");
            System.out.println("2. Ver Lista de Doctores");
            System.out.println("3. Eliminar Doctor");
            System.out.println("4. Registrar Nuevo Usuario");
            System.out.println("5. Ver Lista de Pacientes");
            System.out.println("6. Eliminar Paciente");
            System.out.println("7. Cerrar Sesión");
            System.out.print("Seleccione una opción: ");

            try {
                option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 1 -> createDoctor();
                    case 2 -> listDoctors();
                    case 3 -> deleteDoctor();
                    case 4 -> registerNewUser();
                    case 5 -> listPatients();
                    case 6 -> deletePatient();
                    case 7 -> System.out.println("Cerrando sesión de administrador...");
                    default -> System.out.println("Opción no válida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Por favor ingresa un número válido.");
            }

        } while (option != 7);
    }

    
    private void createDoctor() {
        System.out.print("Full Name: ");
        String name = scanner.nextLine();
        System.out.print("Cargo: ");
        String cargo = scanner.nextLine();
        System.out.print("Edad: ");
        int edad = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Cédula: ");
        int cedula = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Specialty (ID): ");
        short spec = Short.parseShort(scanner.nextLine().trim());

        doctor.create_doctor(name, cargo, edad, cedula, spec);
    }

    
    private void listDoctors() {
        System.out.println("--- List of Doctors ---");
        if (doctor.getListDoctor().isEmpty()) {
            System.out.println("No hay doctores registrados.");
        } else {
            doctor.getListDoctor().forEach(d ->
                System.out.println(d.full_name() + " - " + d.cargo() + " | Especialidad: " + d.specialty())
            );
        }
    }

    private void deleteDoctor() {
        System.out.print("Enter Doctor's ID (Cédula) to delete: ");
        int idEliminar = Integer.parseInt(scanner.nextLine().trim());
        if (doctor.eliminarDoctor(idEliminar)) {
            System.out.println("Doctor eliminado correctamente.");
        } else {
            System.out.println("No se encontró ningún doctor con esa cédula.");
        }
    }

   
    private void registerNewUser() {
        System.out.print("Nuevo nombre de usuario: ");
        String username = scanner.nextLine();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        com.sigp.service.serviceLogin loginService = new com.sigp.service.serviceLogin();
        loginService.registerUser(username, password);
    }

    
    private void listPatients() {
        System.out.println("--- Lista de Pacientes ---");
        if (RepositoryPatient.getPatientList().isEmpty()) {
            System.out.println("No hay pacientes registrados.");
        } else {
            RepositoryPatient.getPatientList().forEach(p ->
                System.out.println(p.name() + " | ID: " + p.id() + " | Email: " + p.email())
            );
        }
    }

    
    private void deletePatient() {
        System.out.println("--- Eliminar Paciente ---");
        if (RepositoryPatient.getPatientList().isEmpty()) {
            System.out.println("No hay pacientes registrados.");
            return;
        }
        listPatients();
        System.out.print("Ingrese el nombre del paciente a eliminar: ");
        String name = scanner.nextLine();
        if (RepositoryPatient.removeByName(name)) {
            System.out.println(" Paciente '" + name + "' eliminado correctamente.");
        } else {
            System.out.println("No se encontró ningún paciente con ese nombre.");
        }
    }
}
