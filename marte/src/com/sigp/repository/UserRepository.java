package com.sigp.repository;

import com.sigp.dao.impl.UserDAO;
import com.sigp.model.User;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Repositorio de usuarios del sistema.
 * Incluye un usuario admin precargado por defecto.
 */
public class UserRepository {
    private static final Logger LOGGER = Logger.getLogger(UserRepository.class.getName());

    private static final UserDAO userDao = new UserDAO();

    static {
        try {
            if (userDao.findByUsername("admin").isEmpty()) {
                userDao.create(new User("admin", "admin123", "ADMIN"));
            }
        } catch (RuntimeException e) {
            // Evita romper el arranque si la BD no está disponible en este momento.
            LOGGER.log(Level.WARNING,
                    "No fue posible inicializar el usuario admin por defecto. Motivo: {0}",
                    e.getMessage());
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
