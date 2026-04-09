package com.sigp.repository;

import com.sigp.model.User;
import com.sigp.util.PersistenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Repositorio de usuarios del sistema.
 * Incluye un usuario admin precargado por defecto.
 */
public class UserRepository {

    private static final List<User> userDatabase = new ArrayList<>();

    static {
        Map<String, Object> datos = PersistenceManager.cargarUsuarios();
        List<User> usuariosCargados = (List<User>) datos.get("usuarios");
        if (usuariosCargados != null) {
            userDatabase.addAll(usuariosCargados);
        }
        if (userDatabase.stream().noneMatch(u -> u.username().equalsIgnoreCase("admin"))) {
            userDatabase.add(new User("admin", "admin123", "ADMIN"));
            PersistenceManager.guardarUsuarios(userDatabase);
        }
    }

    /** Agrega un nuevo usuario a la base de datos. */
    public static void addUser(User user) {
        userDatabase.add(user);
        PersistenceManager.guardarUsuarios(userDatabase);
    }

    /** Agrega un nuevo usuario con rol determinado. */
    public static void addUser(String username, String password, String role) {
        addUser(new User(username, password, role));
    }

    /** Retorna todos los usuarios registrados. */
    public static List<User> getUserDatabase() {
        return new ArrayList<>(userDatabase);
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
