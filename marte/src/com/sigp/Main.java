package com.sigp;

import com.sigp.model.User;
import com.sigp.service.serviceLogin;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        serviceLogin loginService = new serviceLogin();
        boolean running = true;

        System.out.println("******************************************");
        System.out.println("*    SISTEMA HOSPITALARIO MARTE         *");
        System.out.println("******************************************");

        while (running) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Login");
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
                            System.out.println("\nError: Credenciales incorrectas.");
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
                        System.out.println("\nGracias por usar el sistema Marte.");
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
