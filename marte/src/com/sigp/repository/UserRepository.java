package com.sigp.repository;

import com.sigp.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio de usuarios del sistema.
 * Incluye un usuario admin precargado por defecto.
 * Basado en el proyecto de Esteban.
 */
public class UserRepository {

    private static final List<User> userDatabase = new ArrayList<>();

    // Usuario admin precargado al inicio del sistema
    static {
        userDatabase.add(new User("admin", "admin123"));
    }

    /** Agrega un nuevo usuario a la base de datos. */
    public static void addUser(User user) {
        userDatabase.add(user);
    }

    /** Retorna todos los usuarios registrados. */
    public static List<User> getUserDatabase() {
        return userDatabase;
    }

    /** Busca un usuario por email (sin distinguir mayúsculas). */
    public static User findByEmail(String username) {
        for (User u : userDatabase) {
            if (u.username().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }
}
