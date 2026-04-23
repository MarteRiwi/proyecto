package com.sigp.dao.impl;

import com.sigp.dao.AppointmentDAOInterface;
import com.sigp.database.DatabaseConfig;
import com.sigp.model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentDAO implements AppointmentDAOInterface {

    private static final String INSERT_SQL = """
            INSERT INTO citas
            (patient_name, patient_id, doctor_id, doctor_name, doctor_specialty, appointment_datetime, status, cost, notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL = "SELECT * FROM citas WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM citas ORDER BY appointment_datetime DESC";
    private static final String FIND_BY_PATIENT_NAME_SQL = "SELECT * FROM citas WHERE LOWER(patient_name) = LOWER(?) ORDER BY appointment_datetime DESC";
    private static final String FIND_BY_PATIENT_ID_SQL = "SELECT * FROM citas WHERE patient_id = ? ORDER BY appointment_datetime DESC";
    private static final String FIND_BY_DOCTOR_ID_SQL = "SELECT * FROM citas WHERE doctor_id = ? ORDER BY appointment_datetime DESC";
    private static final String FIND_BY_PATIENT_AND_STATUS_SQL = "SELECT * FROM citas WHERE LOWER(patient_name) = LOWER(?) AND status = ? ORDER BY appointment_datetime DESC";
    private static final String FIND_BY_DOCTOR_AND_STATUS_SQL = "SELECT * FROM citas WHERE doctor_id = ? AND status = ? ORDER BY appointment_datetime DESC";
    private static final String FIND_HISTORY_SQL = "SELECT * FROM citas WHERE LOWER(patient_name) = LOWER(?) AND status = 'COMPLETADA' ORDER BY appointment_datetime DESC";
    private static final String UPDATE_SQL = """
            UPDATE citas SET
            patient_name = ?,
            patient_id = ?,
            doctor_id = ?,
            doctor_name = ?,
            doctor_specialty = ?,
            appointment_datetime = ?,
            status = ?,
            cost = ?,
            notes = ?
            WHERE id = ?
            """;
    private static final String DELETE_SQL = "DELETE FROM citas WHERE id = ?";

    @Override
    public Appointment create(Appointment appointment) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            bindForWrite(statement, appointment, false);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    appointment.setId(keys.getInt(1));
                }
            }
            return appointment;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo crear la cita.", e);
        }
    }

    @Override
    public Optional<Appointment> findById(int id) {
        return findSingleByInt(FIND_BY_ID_SQL, id);
    }

    @Override
    public List<Appointment> findAll() {
        return findMany(FIND_ALL_SQL, null);
    }

    @Override
    public List<Appointment> findByPatientName(String patientName) {
        return findMany(FIND_BY_PATIENT_NAME_SQL, statement -> statement.setString(1, patientName));
    }

    @Override
    public List<Appointment> findByPatientId(String patientId) {
        return findMany(FIND_BY_PATIENT_ID_SQL, statement -> statement.setString(1, patientId));
    }

    @Override
    public List<Appointment> findByDoctorId(int doctorId) {
        return findMany(FIND_BY_DOCTOR_ID_SQL, statement -> statement.setInt(1, doctorId));
    }

    @Override
    public List<Appointment> findByPatientAndStatus(String patientName, String status) {
        return findMany(FIND_BY_PATIENT_AND_STATUS_SQL, statement -> {
            statement.setString(1, patientName);
            statement.setString(2, status);
        });
    }

    @Override
    public List<Appointment> findByDoctorAndStatus(int doctorId, String status) {
        return findMany(FIND_BY_DOCTOR_AND_STATUS_SQL, statement -> {
            statement.setInt(1, doctorId);
            statement.setString(2, status);
        });
    }

    @Override
    public List<Appointment> findPatientHistory(String patientName) {
        return findMany(FIND_HISTORY_SQL, statement -> statement.setString(1, patientName));
    }

    @Override
    public boolean update(Appointment appointment) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            bindForWrite(statement, appointment, true);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo actualizar la cita.", e);
        }
    }

    @Override
    public boolean deleteById(int id) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setInt(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo eliminar la cita.", e);
        }
    }

    private Optional<Appointment> findSingleByInt(String sql, int value) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, value);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo consultar la cita.", e);
        }
    }

    private List<Appointment> findMany(String sql, StatementBinder binder) {
        List<Appointment> result = new ArrayList<>();
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            if (binder != null) {
                binder.bind(statement);
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("No se pudieron consultar las citas.", e);
        }
        return result;
    }

    private void bindForWrite(PreparedStatement statement, Appointment appointment, boolean includeId)
            throws SQLException {
        statement.setString(1, appointment.getPatientName());
        statement.setString(2, appointment.getPatientId());
        statement.setInt(3, appointment.getDoctorId());
        statement.setString(4, appointment.getDoctorName());
        statement.setString(5, appointment.getDoctorSpecialty());
        statement.setTimestamp(6, Timestamp.valueOf(appointment.getAppointmentDateTime()));
        statement.setString(7, appointment.getStatus());
        statement.setDouble(8, appointment.getCost());
        statement.setString(9, appointment.getNotes());
        if (includeId) {
            statement.setInt(10, appointment.getId());
        }
    }

    private Appointment mapRow(ResultSet rs) throws SQLException {
        LocalDateTime dateTime = rs.getTimestamp("appointment_datetime").toLocalDateTime();
        Appointment appointment = new Appointment(
                rs.getString("patient_name"),
                rs.getString("patient_id"),
                rs.getInt("doctor_id"),
                rs.getString("doctor_name"),
                rs.getString("doctor_specialty"),
                dateTime
        );
        appointment.setId(rs.getInt("id"));
        appointment.setStatus(rs.getString("status"));
        appointment.setCost(rs.getDouble("cost"));
        appointment.setNotes(rs.getString("notes"));
        return appointment;
    }

    @FunctionalInterface
    private interface StatementBinder {
        void bind(PreparedStatement statement) throws SQLException;
    }
}
