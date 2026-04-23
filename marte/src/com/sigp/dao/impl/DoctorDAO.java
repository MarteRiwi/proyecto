package com.sigp.dao.impl;

import com.sigp.dao.DoctorDAOInterface;
import com.sigp.database.DatabaseConfig;
import com.sigp.model.Doctor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DoctorDAO implements DoctorDAOInterface {

    private static final String INSERT_SQL = "INSERT INTO doctores (nombre_completo, edad, cedula, especialidad) VALUES (?, ?, ?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT id, nombre_completo, edad, cedula, especialidad FROM doctores WHERE id = ?";
    private static final String FIND_BY_CEDULA_SQL = "SELECT id, nombre_completo, edad, cedula, especialidad FROM doctores WHERE cedula = ?";
    private static final String FIND_ALL_SQL = "SELECT id, nombre_completo, edad, cedula, especialidad FROM doctores ORDER BY id";
    private static final String UPDATE_SQL = "UPDATE doctores SET nombre_completo = ?, edad = ?, cedula = ?, especialidad = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM doctores WHERE id = ?";

    @Override
    public Doctor create(Doctor doctor) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, doctor.getNombreCompleto());
            statement.setInt(2, doctor.getEdad());
            statement.setString(3, doctor.getCedula());
            statement.setString(4, doctor.getEspecialidad());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    doctor.setId(keys.getInt(1));
                }
            }
            return doctor;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo crear el doctor.", e);
        }
    }

    @Override
    public Optional<Doctor> findById(int id) {
        return findSingle(FIND_BY_ID_SQL, id);
    }

    @Override
    public Optional<Doctor> findByCedula(String cedula) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CEDULA_SQL)) {
            statement.setString(1, cedula);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo consultar el doctor por cédula.", e);
        }
    }

    @Override
    public List<Doctor> findAll() {
        List<Doctor> doctors = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                doctors.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron consultar los doctores.", e);
        }
        return doctors;
    }

    @Override
    public boolean update(Doctor doctor) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, doctor.getNombreCompleto());
            statement.setInt(2, doctor.getEdad());
            statement.setString(3, doctor.getCedula());
            statement.setString(4, doctor.getEspecialidad());
            statement.setInt(5, doctor.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo actualizar el doctor.", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo eliminar el doctor.", e);
        }
    }

    private Optional<Doctor> findSingle(String sql, int id) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo consultar el doctor.", e);
        }
    }

    private Doctor mapRow(ResultSet rs) throws SQLException {
        Doctor doctor = new Doctor(
                rs.getString("nombre_completo"),
                rs.getInt("edad"),
                rs.getString("cedula"),
                rs.getString("especialidad")
        );
        doctor.setId(rs.getInt("id"));
        return doctor;
    }
}
