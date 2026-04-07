package com.sigp;

import com.sigp.model.User;
import com.sigp.service.LoginService;
import java.util.Scanner;

/**
 * Punto de entrada del Sistema Hospitalario Marte (SIGP).
 *
 * Flujo principal:
 *   1. El usuario hace login o se registra.
 *   2. Si es admin  → panel de administración (gestión de doctores y pacientes).
 *   3. Si es paciente → registro de información personal y menú de citas.
 *
 * Fusión de los proyectos de Esteban (Main + login + pacientes)
 * y Stiven (gestión completa de doctores con CRUD e IDs).
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LoginService loginService = new LoginService();
        boolean running = true;

        System.out.println("******************************************");
        System.out.println("*       SISTEMA HOSPITALARIO MARTE      *");
        System.out.println("******************************************");

        while (running) {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrarse");
            System.out.println("3. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());

                switch (choice) {
                    case 1 -> {
                        System.out.print("Usuario: ");
                        String userStr = scanner.nextLine();
                        System.out.print("Contraseña: ");
                        String passStr = scanner.nextLine();

                        if (loginService.authenticate(userStr, passStr)) {
                            User loggedUser = new User(userStr, passStr);
                            System.out.println("\nAcceso concedido.");
                            loginService.handleLoginSuccess(loggedUser);
                        } else {
                            System.out.println("\nError: credenciales incorrectas.");
                        }
                    }
                    case 2 -> {
                        System.out.print("Nuevo usuario: ");
                        String username = scanner.nextLine();
                        System.out.print("Nueva contraseña: ");
                        String password = scanner.nextLine();
                        loginService.registerUser(username, password);
                    }
                    case 3 -> {
                        System.out.println("\nGracias por usar el sistema Marte. ¡Hasta pronto!");
                        running = false;
                    }
                    default -> System.out.println("Opción no válida.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Por favor ingresa un número válido.");
            }
        }

        scanner.close();
    }
}