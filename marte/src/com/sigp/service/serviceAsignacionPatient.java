package com.sigp.service;

import com.sigp.model.Patient;
import com.sigp.repository.RepositoryPatient;
import java.util.Scanner;


public class serviceAsignacionPatient {

    
    public static void asignar_patient(String name, String nationality, String phone, String email, int age, String id) {
        try {
            Patient newPatient = new Patient(name, nationality, phone, email, age, id);
            RepositoryPatient.addPatient(newPatient);
            Getpatient(name);
            showPostRegisterMenu(name);
        } catch (IllegalArgumentException e) {
            System.out.println(" Error al registrar paciente: " + e.getMessage());
        }
    }

    
    
    public void information_patient(String loginname) {
        Scanner sc = new Scanner(System.in);
        String name = loginname;
        System.out.println("\n--- REGISTRATION FOR: " + name.toUpperCase() + " ---");

        try {
            System.out.print("Enter patient nationality: ");
            String nationality = sc.nextLine();

            System.out.print("Enter patient phone: ");
            String phone = sc.nextLine();

            System.out.print("Enter patient email: ");
            String email = sc.nextLine();

            System.out.print("Enter patient age: ");
            int age = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Enter patient ID: ");
            String id = sc.nextLine();

            asignar_patient(name, nationality, phone, email, age, id);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input! Please enter a valid number for age.");
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }
    }

    
    public static void Getpatient(String name) {
        Patient found = RepositoryPatient.findByName(name);
        if (found != null) {
            System.out.println("\n========================================");
            System.out.println(" " + found.name() + " ha sido registrado como paciente.");
            System.out.println("Tus datos registrados son:");
            System.out.println("  Nombre:       " + found.name());
            System.out.println("  ID:           " + found.id());
            System.out.println("  Nacionalidad: " + found.nationality());
            System.out.println("  Teléfono:     " + found.phone());
            System.out.println("  Email:        " + found.email());
            System.out.println("  Edad:         " + found.age() + " años");
            System.out.println("========================================\n");
        } else {
            System.out.println("Patient not found.");
        }
    }

    
    private static void showPostRegisterMenu(String name) {
        Scanner sc = new Scanner(System.in);
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("¿Qué deseas hacer ahora?");
            System.out.println("1. Ir al menú de citas");
            System.out.println("2. Salir del sistema");
            System.out.print("Selecciona una opción: ");
            try {
                int opt = Integer.parseInt(sc.nextLine().trim());
                switch (opt) {
                    case 1 -> {
                        showAppointmentMenu(name);
                        inMenu = false;
                    }
                    case 2 -> {
                        System.out.println("\nGracias por usar el sistema Marte. ¡Hasta pronto, " + name + "!");
                        inMenu = false;
                        System.exit(0);
                    }
                    default -> System.out.println("Opción no válida. Ingresa 1 o 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingresa un número válido.");
            }
        }
    }

    
    private static void showAppointmentMenu(String name) {
        Scanner sc = new Scanner(System.in);
        boolean inMenu = true;
        while (inMenu) {
            System.out.println("\n========================================");
            System.out.println("   MENÚ DE CITAS — " + name.toUpperCase());
            System.out.println("========================================");
            System.out.println("1. Agendar nueva cita");
            System.out.println("2. Ver mis citas");
            System.out.println("3. Cancelar una cita");
            System.out.println("4. Salir");
            System.out.print("Selecciona una opción: ");
            try {
                int opt = Integer.parseInt(sc.nextLine().trim());
                switch (opt) {
                    case 1 -> System.out.println(" Módulo 'Agendar cita' en desarrollo.");
                    case 2 -> System.out.println(" Módulo 'Ver citas' en desarrollo.");
                    case 3 -> System.out.println(" Módulo 'Cancelar cita' en desarrollo.");
                    case 4 -> {
                        System.out.println("Saliendo del sistema. ¡Hasta pronto, " + name + "!");
                        inMenu = false;
                        System.exit(0);
                    }
                    default -> System.out.println("Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor ingresa un número válido.");
            }
        }
    }
}