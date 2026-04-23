package com.sigp.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuracion centralizada de conexion JDBC PostgreSQL con HikariCP.
 * Responsabilidad unica: construir y administrar el pool de conexiones.
 */
public class DatabaseConfig {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConfig.class.getName());
    private static final String DRIVER_CLASS = "org.postgresql.Driver";
    private static final String CLASSPATH_CONFIG_FILE = "database.properties";

    private static volatile DataSource dataSource;
    private static final AppSettings SETTINGS = AppSettings.load();

    private DatabaseConfig() {
    }

    /**
     * Inicializa el pool de conexiones lazy-loading.
     */
    public static synchronized DataSource getDataSource() {
        if (dataSource == null) {
            DataSource createdDataSource = null;
            try {
                createdDataSource = DataSourceFactory.create(SETTINGS.database());
                validateConnection(createdDataSource, SETTINGS.database());
                dataSource = createdDataSource;
                LOGGER.log(Level.INFO, "Pool de conexiones inicializado para {0}", SETTINGS.database().jdbcUrl());
            } catch (RuntimeException e) {
                closeDataSource(createdDataSource);
                throw buildConnectionException(SETTINGS.database(), e);
            }
        }
        return dataSource;
    }

    /**
     * Obtiene conexion del pool.
     *
     * @return Connection lista para usar
     * @throws SQLException si no puede conectar
     */
    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

    /**
     * Test de conexion.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Error al probar conexion PostgreSQL.", e);
            return false;
        }
    }

    public static synchronized void shutdown() {
        if (dataSource != null) {
            closeDataSource(dataSource);
            dataSource = null;
            DatabaseSchemaManager.reset();
            LOGGER.log(Level.INFO, "Pool de conexiones cerrado.");
        }
    }

    static AppSettings getSettings() {
        return SETTINGS;
    }

    private static void validateConnection(DataSource source, DatabaseSettings settings) {
        try (Connection ignored = source.getConnection()) {
            // Fail-fast para detectar credenciales/host/puerto incorrectos en el arranque.
        } catch (SQLException e) {
            throw buildConnectionException(settings, e);
        }
    }

    private static RuntimeException buildConnectionException(DatabaseSettings settings, Throwable cause) {
        String message = "No se pudo conectar a PostgreSQL (url=" + settings.jdbcUrl()
                + ", user=" + settings.username() + ")."
                + " Verifica host, puerto, base de datos y credenciales.";
        return new RuntimeException(message, cause);
    }

    private static void closeDataSource(DataSource source) {
        if (source instanceof HikariDataSource hikariDataSource) {
            hikariDataSource.close();
        }
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }

    static record AppSettings(DatabaseSettings database, SeedSettings seed) {
        private static AppSettings load() {
            ConfigResolver resolver = ConfigResolver.load();
            return new AppSettings(
                    DatabaseSettings.from(resolver),
                    SeedSettings.from(resolver)
            );
        }
    }

    static record DatabaseSettings(String jdbcUrl, String username, String password) {
        private static DatabaseSettings from(ConfigResolver resolver) {
            String directUrl = resolver.resolve("SIGP_DB_URL", "sigp.db.url", "db.url");
            String host = resolver.resolveOrDefaultWithFallbackEnv("SIGP_DB_HOST", "POSTGRES_HOST", "sigp.db.host", "db.host", "localhost");
            String port = resolver.resolveOrDefaultWithFallbackEnv("SIGP_DB_PORT", "POSTGRES_PORT", "sigp.db.port", "db.port", "5432");
            String dbName = resolver.resolveOrDefaultWithFallbackEnv("SIGP_DB_NAME", "POSTGRES_DB", "sigp.db.name", "db.name", "sigp");
            String jdbcUrl = directUrl.isBlank() ? "jdbc:postgresql://" + host + ":" + port + "/" + dbName : directUrl;

            String username = resolver.resolveOrDefaultWithFallbackEnv("SIGP_DB_USER", "POSTGRES_USER", "sigp.db.user", "db.user", "postgres");
            String password = resolver.resolveWithFallbackEnv("SIGP_DB_PASSWORD", "POSTGRES_PASSWORD", "sigp.db.password", "db.password");

            if (password.isBlank()) {
                LOGGER.log(Level.WARNING, "No se encontro SIGP_DB_PASSWORD/db.password; se usara password vacio.");
            }

            return new DatabaseSettings(jdbcUrl, username, password);
        }
    }

    static record SeedSettings(
            String adminUsername,
            String adminPassword,
            String doctorUsername,
            String doctorPassword,
            String patientUsername,
            String patientPassword
    ) {
        private static SeedSettings from(ConfigResolver resolver) {
            return new SeedSettings(
                    resolver.resolveOrDefault("SIGP_SEED_ADMIN_USER", "sigp.seed.admin.user", "seed.admin.user", "admin"),
                    resolver.resolve("SIGP_SEED_ADMIN_PASSWORD", "sigp.seed.admin.password", "seed.admin.password"),
                    resolver.resolveOrDefault("SIGP_SEED_DOCTOR_USER", "sigp.seed.doctor.user", "seed.doctor.user", "doctor.demo@mail.com"),
                    resolver.resolve("SIGP_SEED_DOCTOR_PASSWORD", "sigp.seed.doctor.password", "seed.doctor.password"),
                    resolver.resolveOrDefault("SIGP_SEED_PATIENT_USER", "sigp.seed.patient.user", "seed.patient.user", "paciente.demo@mail.com"),
                    resolver.resolve("SIGP_SEED_PATIENT_PASSWORD", "sigp.seed.patient.password", "seed.patient.password")
            );
        }

        boolean hasMissingPasswords() {
            return adminPassword.isBlank() || doctorPassword.isBlank() || patientPassword.isBlank();
        }
    }

    private static final class ConfigResolver {
        private final Properties properties;

        private ConfigResolver(Properties properties) {
            this.properties = properties;
        }

        private static ConfigResolver load() {
            Properties loaded = new Properties();
            loadClasspathProperties(loaded);
            loadExternalProperties(loaded);
            return new ConfigResolver(loaded);
        }

        private String resolve(String envKey, String systemPropertyKey, String propertiesKey) {
            return firstNonBlank(
                    System.getenv(envKey),
                    System.getProperty(systemPropertyKey),
                    properties.getProperty(propertiesKey)
            );
        }

        private String resolveWithFallbackEnv(String envKey, String fallbackEnvKey, String systemPropertyKey, String propertiesKey) {
            return firstNonBlank(
                    System.getenv(envKey),
                    System.getenv(fallbackEnvKey),
                    System.getProperty(systemPropertyKey),
                    properties.getProperty(propertiesKey)
            );
        }

        private String resolveOrDefault(String envKey, String systemPropertyKey, String propertiesKey, String defaultValue) {
            String value = resolve(envKey, systemPropertyKey, propertiesKey);
            return value.isBlank() ? defaultValue : value;
        }

        private String resolveOrDefaultWithFallbackEnv(String envKey, String fallbackEnvKey, String systemPropertyKey, String propertiesKey, String defaultValue) {
            String value = resolveWithFallbackEnv(envKey, fallbackEnvKey, systemPropertyKey, propertiesKey);
            return value.isBlank() ? defaultValue : value;
        }

        private static void loadClasspathProperties(Properties target) {
            try (InputStream inputStream = DatabaseConfig.class.getClassLoader().getResourceAsStream(CLASSPATH_CONFIG_FILE)) {
                if (inputStream != null) {
                    target.load(inputStream);
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "No se pudo cargar {0} desde classpath.", CLASSPATH_CONFIG_FILE);
                LOGGER.log(Level.FINE, "Detalle de error de carga de classpath", e);
            }
        }

        private static void loadExternalProperties(Properties target) {
            String externalPath = firstNonBlank(
                    System.getenv("SIGP_DB_CONFIG_FILE"),
                    System.getProperty("sigp.db.config.file")
            );

            if (externalPath.isBlank()) {
                return;
            }

            try (InputStream inputStream = Files.newInputStream(Path.of(externalPath))) {
                target.load(inputStream);
                LOGGER.log(Level.INFO, "Configuracion externa cargada desde {0}", externalPath);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "No se pudo cargar el archivo de configuracion externa: " + externalPath, e);
            }
        }
    }

    private static final class DataSourceFactory {
        private static DataSource create(DatabaseSettings settings) {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(settings.jdbcUrl());
            config.setUsername(settings.username());
            config.setPassword(settings.password());
            config.setDriverClassName(DRIVER_CLASS);
            config.setMaximumPoolSize(20);
            config.setMinimumIdle(5);
            config.setConnectionTimeout(30000);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            return new HikariDataSource(config);
        }
    }
}
