package com.sigp.repository;

import com.sigp.dao.impl.UserDAO;
import com.sigp.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de usuarios del sistema.
 * Incluye un usuario admin precargado por defecto.
 */
public class UserRepository {

    private static final UserDAO userDao = new UserDAO();

    static {
        if (userDao.findByUsername("admin").isEmpty()) {
            userDao.create(new User("admin", "admin123", "ADMIN"));
        }
    }

    /** Agrega un nuevo usuario a la base de datos. */
    public static void addUser(User user) {
        userDao.create(user);
    }

    /** Agrega un nuevo usuario con rol determinado. */
    public static void addUser(String username, String password, String role) {
        addUser(new User(username, password, role));
    }

    /** Retorna todos los usuarios registrados. */
    public static List<User> getUserDatabase() {
        return userDao.findAll();
    }

    /** Busca un usuario por email (sin distinguir mayúsculas). */
    public static User findByEmail(String username) {
        Optional<User> user = userDao.findByUsername(username);
        return user.orElse(null);
    }
}
