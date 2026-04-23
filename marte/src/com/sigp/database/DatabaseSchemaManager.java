package com.sigp.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Responsable de crear/verificar el esquema SQL y cargar datos iniciales.
 */
public final class DatabaseSchemaManager {
    private static final Logger LOGGER = Logger.getLogger(DatabaseSchemaManager.class.getName());

    private static volatile boolean initialized = false;

    private DatabaseSchemaManager() {
    }

    public static synchronized void initializeIfNeeded() {
        if (initialized) {
            return;
        }

        try (Connection connection = DatabaseConfig.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(CREATE_USERS);
            statement.execute(CREATE_PATIENTS);
            statement.execute(CREATE_DOCTORS);
            statement.execute(CREATE_APPOINTMENTS);
            DataSeeder.seedInitialData(connection, DatabaseConfig.getSettings().seed());
            initialized = true;
            LOGGER.log(Level.INFO, "Esquema PostgreSQL inicializado/verificado correctamente.");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "No se pudo inicializar el esquema PostgreSQL.", e);
            throw new RuntimeException("No se pudo inicializar el esquema de PostgreSQL.", e);
        }
    }

    static synchronized void reset() {
        initialized = false;
    }

    private static final String CREATE_USERS = """
            CREATE TABLE IF NOT EXISTS usuarios (
                username VARCHAR(120) PRIMARY KEY,
                password VARCHAR(255) NOT NULL,
                role VARCHAR(20) NOT NULL
            )
            """;

    private static final String CREATE_PATIENTS = """
            CREATE TABLE IF NOT EXISTS pacientes (
                id VARCHAR(20) PRIMARY KEY,
                name VARCHAR(120) NOT NULL,
                nationality VARCHAR(80) NOT NULL,
                phone VARCHAR(20) NOT NULL,
                email VARCHAR(120) NOT NULL UNIQUE,
                age INT NOT NULL
            )
            """;

    private static final String CREATE_DOCTORS = """
            CREATE TABLE IF NOT EXISTS doctores (
                id SERIAL PRIMARY KEY,
                nombre_completo VARCHAR(120) NOT NULL,
                edad INT NOT NULL,
                cedula VARCHAR(20) NOT NULL UNIQUE,
                especialidad VARCHAR(120) NOT NULL
            )
            """;

    private static final String CREATE_APPOINTMENTS = """
            CREATE TABLE IF NOT EXISTS citas (
                id SERIAL PRIMARY KEY,
                patient_name VARCHAR(120) NOT NULL,
                patient_id VARCHAR(20) NOT NULL,
                doctor_id INT NOT NULL,
                doctor_name VARCHAR(120) NOT NULL,
                doctor_specialty VARCHAR(120) NOT NULL,
                appointment_datetime TIMESTAMP NOT NULL,
                status VARCHAR(20) NOT NULL,
                cost NUMERIC(10,2) NOT NULL DEFAULT 0,
                notes TEXT,
                CONSTRAINT fk_citas_doctor
                    FOREIGN KEY (doctor_id) REFERENCES doctores(id)
                    ON UPDATE CASCADE ON DELETE RESTRICT
            )
            """;

    private static final class DataSeeder {
        private static void seedInitialData(Connection connection, DatabaseConfig.SeedSettings seedSettings) throws SQLException {
            seedUsers(connection, seedSettings);
            seedDoctors(connection);
            seedPatients(connection, seedSettings);
            seedAppointments(connection);
        }

        private static void seedUsers(Connection connection, DatabaseConfig.SeedSettings seedSettings) throws SQLException {
            if (hasRows(connection, "usuarios")) {
                return;
            }

            if (seedSettings.hasMissingPasswords()) {
                LOGGER.log(Level.WARNING, "No se insertaron usuarios seed porque faltan passwords en configuracion.");
                return;
            }

            String sql = "INSERT INTO usuarios (username, password, role) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, seedSettings.adminUsername());
                statement.setString(2, seedSettings.adminPassword());
                statement.setString(3, "ADMIN");
                statement.addBatch();

                statement.setString(1, seedSettings.doctorUsername());
                statement.setString(2, seedSettings.doctorPassword());
                statement.setString(3, "DOCTOR");
                statement.addBatch();

                statement.setString(1, seedSettings.patientUsername());
                statement.setString(2, seedSettings.patientPassword());
                statement.setString(3, "PATIENT");
                statement.addBatch();

                statement.executeBatch();
            }
        }

        private static void seedDoctors(Connection connection) throws SQLException {
            if (hasRows(connection, "doctores")) {
                return;
            }

            String sql = "INSERT INTO doctores (nombre_completo, edad, cedula, especialidad) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "Ana Maria Rojas");
                statement.setInt(2, 39);
                statement.setString(3, "1030123456");
                statement.setString(4, "Medicina General");
                statement.executeUpdate();
            }
        }

        private static void seedPatients(Connection connection, DatabaseConfig.SeedSettings seedSettings) throws SQLException {
            if (hasRows(connection, "pacientes")) {
                return;
            }

            String sql = "INSERT INTO pacientes (id, name, nationality, phone, email, age) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "1002003004");
                statement.setString(2, "Carlos Ruiz");
                statement.setString(3, "Colombiana");
                statement.setString(4, "3001234567");
                statement.setString(5, seedSettings.patientUsername());
                statement.setInt(6, 31);
                statement.executeUpdate();
            }
        }

        private static void seedAppointments(Connection connection) throws SQLException {
            if (hasRows(connection, "citas")) {
                return;
            }

            Integer doctorId = getFirstDoctorId(connection);
            if (doctorId == null) {
                return;
            }

            String sql = """
                    INSERT INTO citas
                    (patient_name, patient_id, doctor_id, doctor_name, doctor_specialty, appointment_datetime, status, cost, notes)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, "Carlos Ruiz");
                statement.setString(2, "1002003004");
                statement.setInt(3, doctorId);
                statement.setString(4, "Ana Maria Rojas");
                statement.setString(5, "Medicina General");
                statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now().plusDays(1).withSecond(0).withNano(0)));
                statement.setString(7, "PENDIENTE");
                statement.setDouble(8, 0.0);
                statement.setString(9, "Cita creada como dato de prueba");
                statement.executeUpdate();
            }
        }

        private static boolean hasRows(Connection connection, String tableName) throws SQLException {
            // tableName se usa solo con valores internos (no proviene del usuario), por eso es seguro concatenarlo.
            String sql = "SELECT EXISTS (SELECT 1 FROM " + tableName + " LIMIT 1)";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean(1);
                }
                return false;
            }
        }

        private static Integer getFirstDoctorId(Connection connection) throws SQLException {
            String sql = "SELECT id FROM doctores ORDER BY id LIMIT 1";
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
                return null;
            }
        }
    }
}
