package com.sigp.repository;

import com.sigp.model.User;
import java.util.ArrayList;
import java.util.List;


public class RepositoryUser {

    private static List<User> userDatabase = new ArrayList<>();

    static {
        userDatabase.add(new User("admin", "admin123"));
    }

    public static void addUser(User user) {
        userDatabase.add(user);
    }

    public static List<User> getUserDatabase() {
        return userDatabase;
    }

    public static User findByUsername(String username) {
        for (User u : userDatabase) {
            if (u.username().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }
}
