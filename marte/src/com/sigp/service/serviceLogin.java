package com.sigp.service;

import com.sigp.model.User;
import com.sigp.repository.RepositoryUser;
import com.sigp.controller.controllerAdmin;


public class serviceLogin {

    public boolean authenticate(String username, String password) {
        for (User u : RepositoryUser.getUserDatabase()) {
            if (u.username().equals(username) && u.password().equals(password)) {
                return true;
            }
        }
        return false;
    }

    
    public boolean validateAdminLogin(String username, String password) {
        for (User u : RepositoryUser.getUserDatabase()) {
            if (u.username().equals("admin") && u.password().equals("admin123")) {
                return true;
            }
        }
        return false;
    }


    public void registerUser(String username, String password) {
        
        if (username == null || username.trim().isEmpty()) {
            System.out.println(" El usuario no puede estar vacío.");
            return;
        }
        for (User u : RepositoryUser.getUserDatabase()) {
            if (u.username().equalsIgnoreCase(username)) {
                System.out.println("  El usuario '" + username + "' ya existe. Elige otro nombre.");
                return;
            }
        }
        try {
            RepositoryUser.addUser(new User(username, password));
            System.out.println(" Usuario '" + username + "' registrado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("  " + e.getMessage());
        }
    }

    
    public boolean isAdmin(String username) {
        return username.toLowerCase().contains("admin");
    }

    
    public void handleLoginSuccess(User user) {
        if (isAdmin(user.username())) {
            System.out.println("Acceso de Administrador detectado.");
            controllerAdmin admin = new controllerAdmin();
            admin.showAdminMenu(user);
        } else {
            System.out.println("Bienvenido, " + user.username());
            System.out.println("Por favor, completa tu información de paciente:");
            serviceAsignacionPatient patientService = new serviceAsignacionPatient();
            patientService.information_patient(user.username());
        }
    }
}