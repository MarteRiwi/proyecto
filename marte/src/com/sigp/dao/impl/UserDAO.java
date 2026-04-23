package com.sigp.dao.impl;

import com.sigp.dao.UserDAOInterface;
import com.sigp.database.DatabaseConfig;
import com.sigp.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements UserDAOInterface {

    private static final String INSERT_SQL = "INSERT INTO usuarios (username, password, role) VALUES (?, ?, ?)";
    private static final String FIND_BY_USERNAME_SQL = "SELECT username, password, role FROM usuarios WHERE LOWER(username) = LOWER(?)";
    private static final String FIND_ALL_SQL = "SELECT username, password, role FROM usuarios ORDER BY username";
    private static final String UPDATE_SQL = "UPDATE usuarios SET password = ?, role = ? WHERE LOWER(username) = LOWER(?)";
    private static final String DELETE_SQL = "DELETE FROM usuarios WHERE LOWER(username) = LOWER(?)";

    @Override
    public User create(User user) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole());
            statement.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo crear el usuario.", e);
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USERNAME_SQL)) {
            statement.setString(1, username);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo consultar el usuario.", e);
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron consultar los usuarios.", e);
        }
        return users;
    }

    @Override
    public boolean update(User user) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, user.getPassword());
            statement.setString(2, user.getRole());
            statement.setString(3, user.getUsername());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo actualizar el usuario.", e);
        }
    }

    @Override
    public boolean deleteByUsername(String username) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setString(1, username);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo eliminar el usuario.", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role")
        );
    }
}
