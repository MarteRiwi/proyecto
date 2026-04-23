package com.sigp.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Configuración centralizada de conexión JDBC PostgreSQL con HikariCP pool.
 * Singleton para todo el sistema. SOLID: SRP (único responsable conexión).
 */
public class DatabaseConfig {
    private static volatile DataSource dataSource;
    private static volatile boolean schemaInitialized = false;
    private static final String URL = buildJdbcUrl();
    private static final String USER = System.getenv().getOrDefault("SIGP_DB_USER", "postgres");
    private static final String PASSWORD = System.getenv().getOrDefault("SIGP_DB_PASSWORD", "1234");
    
    private DatabaseConfig() {} // Private constructor - Singleton

    private static String buildJdbcUrl() {
        String directUrl = System.getenv("SIGP_DB_URL");
        if (directUrl != null && !directUrl.isBlank()) {
            return directUrl;
        }
        String host = System.getenv().getOrDefault("SIGP_DB_HOST", "localhost");
        String port = System.getenv().getOrDefault("SIGP_DB_PORT", "5432");
        String dbName = System.getenv().getOrDefault("SIGP_DB_NAME", "sigp");
        return "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
    }
    
    /**
     * Inicializa el pool de conexiones lazy-loading.
     */
    public static synchronized DataSource getDataSource() {
        if (dataSource == null) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(URL);
            config.setUsername(USER);
            config.setPassword(PASSWORD);
            config.setDriverClassName("org.postgresql.Driver");
            config.setMaximumPoolSize(20);
            config.setMinimumIdle(5);
            config.setConnectionTimeout(30000);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            dataSource = new HikariDataSource(config);
            initializeSchema();
        }
        return dataSource;
    }

    private static synchronized void initializeSchema() {
        if (schemaInitialized) {
            return;
        }

        String createUsers = """
                CREATE TABLE IF NOT EXISTS usuarios (
                    username VARCHAR(120) PRIMARY KEY,
                    password VARCHAR(255) NOT NULL,
                    role VARCHAR(20) NOT NULL
                )
                """;

        String createPatients = """
                CREATE TABLE IF NOT EXISTS pacientes (
                    id VARCHAR(20) PRIMARY KEY,
                    name VARCHAR(120) NOT NULL,
                    nationality VARCHAR(80) NOT NULL,
                    phone VARCHAR(20) NOT NULL,
                    email VARCHAR(120) NOT NULL UNIQUE,
                    age INT NOT NULL
                )
                """;

        String createDoctors = """
                CREATE TABLE IF NOT EXISTS doctores (
                    id SERIAL PRIMARY KEY,
                    nombre_completo VARCHAR(120) NOT NULL,
                    edad INT NOT NULL,
                    cedula VARCHAR(20) NOT NULL UNIQUE,
                    especialidad VARCHAR(120) NOT NULL
                )
                """;

        String createAppointments = """
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

        try (Connection connection = getConnection();
             var statement = connection.createStatement()) {
            statement.execute(createUsers);
            statement.execute(createPatients);
            statement.execute(createDoctors);
            statement.execute(createAppointments);
            seedInitialData(connection);
            schemaInitialized = true;
        } catch (SQLException e) {
            throw new RuntimeException("No se pudo inicializar el esquema de PostgreSQL.", e);
        }
    }

    private static void seedInitialData(Connection connection) throws SQLException {
        seedUsers(connection);
        seedDoctors(connection);
        seedPatients(connection);
        seedAppointments(connection);
    }

    private static void seedUsers(Connection connection) throws SQLException {
        if (hasRows(connection, "usuarios")) {
            return;
        }

        String sql = "INSERT INTO usuarios (username, password, role) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "admin");
            statement.setString(2, "admin123");
            statement.setString(3, "ADMIN");
            statement.addBatch();

            statement.setString(1, "doctor.demo@mail.com");
            statement.setString(2, "doc1234");
            statement.setString(3, "DOCTOR");
            statement.addBatch();

            statement.setString(1, "paciente.demo@mail.com");
            statement.setString(2, "pac1234");
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

    private static void seedPatients(Connection connection) throws SQLException {
        if (hasRows(connection, "pacientes")) {
            return;
        }

        String sql = "INSERT INTO pacientes (id, name, nationality, phone, email, age) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "1002003004");
            statement.setString(2, "Carlos Ruiz");
            statement.setString(3, "Colombiana");
            statement.setString(4, "3001234567");
            statement.setString(5, "paciente.demo@mail.com");
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
    
    /**
     * Obtiene conexión del pool.
     * @return Connection lista para usar
     * @throws SQLException si no puede conectar
     */
    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }
    
    /**
     * Test de conexión.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error conexión PostgreSQL: " + e.getMessage());
            return false;
        }
    }
    
    public static void shutdown() {
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }
}
