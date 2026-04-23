package com.sigp.dao.impl;

import com.sigp.dao.PatientDAOInterface;
import com.sigp.database.DatabaseConfig;
import com.sigp.model.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientDAO implements PatientDAOInterface {

    private static final String INSERT_SQL = "INSERT INTO pacientes (id, name, nationality, phone, email, age) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT id, name, nationality, phone, email, age FROM pacientes WHERE id = ?";
    private static final String FIND_BY_EMAIL_SQL = "SELECT id, name, nationality, phone, email, age FROM pacientes WHERE LOWER(email) = LOWER(?)";
    private static final String FIND_BY_NAME_SQL = "SELECT id, name, nationality, phone, email, age FROM pacientes WHERE LOWER(name) = LOWER(?)";
    private static final String FIND_ALL_SQL = "SELECT id, name, nationality, phone, email, age FROM pacientes ORDER BY name";
    private static final String UPDATE_SQL = "UPDATE pacientes SET name = ?, nationality = ?, phone = ?, email = ?, age = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM pacientes WHERE id = ?";

    @Override
    public Patient create(Patient patient) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            statement.setString(1, patient.getId());
            statement.setString(2, patient.getName());
            statement.setString(3, patient.getNationality());
            statement.setString(4, patient.getPhone());
            statement.setString(5, patient.getEmail());
            statement.setInt(6, patient.getAge());
            statement.executeUpdate();
            return patient;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo crear el paciente.", e);
        }
    }

    @Override
    public Optional<Patient> findById(String id) {
        return findSingle(FIND_BY_ID_SQL, id);
    }

    @Override
    public Optional<Patient> findByEmail(String email) {
        return findSingle(FIND_BY_EMAIL_SQL, email);
    }

    @Override
    public Optional<Patient> findByName(String name) {
        return findSingle(FIND_BY_NAME_SQL, name);
    }

    @Override
    public List<Patient> findAll() {
        List<Patient> patients = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                patients.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron consultar los pacientes.", e);
        }
        return patients;
    }

    @Override
    public boolean update(Patient patient) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, patient.getName());
            statement.setString(2, patient.getNationality());
            statement.setString(3, patient.getPhone());
            statement.setString(4, patient.getEmail());
            statement.setInt(5, patient.getAge());
            statement.setString(6, patient.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo actualizar el paciente.", e);
        }
    }

    @Override
    public boolean deleteById(String id) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setString(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo eliminar el paciente.", e);
        }
    }

    private Optional<Patient> findSingle(String sql, String value) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, value);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo consultar el paciente.", e);
        }
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        return new Patient(
                rs.getString("name"),
                rs.getString("nationality"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getInt("age"),
                rs.getString("id")
        );
    }
}
