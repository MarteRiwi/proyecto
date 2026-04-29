package marte.src.com.sigp;

import marte.src.com.sigp.exception.CustomException;
import marte.src.com.sigp.model.Appointment;
import marte.src.com.sigp.model.Doctor;
import marte.src.com.sigp.model.Patient;
import marte.src.com.sigp.model.User;
import marte.src.com.sigp.repository.AppointmentRepository;
import marte.src.com.sigp.repository.DoctorRepository;
import marte.src.com.sigp.repository.PatientRepository;
import marte.src.com.sigp.repository.UserRepository;
import marte.src.com.sigp.service.AppointmentService;
import marte.src.com.sigp.service.DoctorService;
import marte.src.com.sigp.service.LoginService;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClinicaMarteApp extends Application {

    // ── Servicios reales del proyecto ──────────────────────────────────────────
    private final LoginService loginService = new LoginService();
    private final DoctorService doctorService = new DoctorService();
    // AppointmentService necesita Scanner en consola; para la UI usamos el
    // repositorio directamente cuando sea necesario.

    // ── Estado de sesión ───────────────────────────────────────────────────────
    private Stage primaryStage;
    private User usuarioActual;  // User record del proyecto real

    // ── Layout principal ───────────────────────────────────────────────────────
    private BorderPane rootLayout;
    private VBox contentArea;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        mostrarLogin();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // LOGIN — usa LoginService.authenticate()
    // ══════════════════════════════════════════════════════════════════════════

    private void mostrarLogin() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #0f172a;");

        Label logo = new Label("MARTE");
        logo.setFont(Font.font("System", FontWeight.BOLD, 60));
        logo.setTextFill(Color.web("#f87171"));

        VBox box = new VBox(15);
        box.setMaxWidth(350);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: #1e293b; -fx-background-radius: 20;");

        TextField user = new TextField();
        user.setPromptText("Email / Usuario");
        user.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-padding: 10;");

        PasswordField pass = new PasswordField();
        pass.setPromptText("Contraseña");
        pass.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-padding: 10;");

        Label lblError = new Label();
        lblError.setTextFill(Color.web("#f87171"));

        Button btn = new Button("INGRESAR");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12;");

        btn.setOnAction(e -> {
            // Usa LoginService.authenticate() — el mismo que usa el proyecto en consola
            User autenticado = loginService.authenticate(user.getText().trim(), pass.getText());
            if (autenticado != null) {
                usuarioActual = autenticado;
                mostrarMainApp();
            } else {
                lblError.setText("Credenciales incorrectas. Verifique email y contraseña.");
            }
        });

        // Permitir Enter para iniciar sesión
        pass.setOnAction(btn.getOnAction());

        Label titulo = new Label("Acceso al Sistema");
        titulo.setTextFill(Color.GRAY);
        box.getChildren().addAll(titulo, user, pass, lblError, btn);
        layout.getChildren().addAll(logo, box);

        primaryStage.setScene(new Scene(layout, 1100, 750));
        primaryStage.setTitle("Sistema Médico Marte");
        primaryStage.show();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // NAVEGACIÓN PRINCIPAL
    // ══════════════════════════════════════════════════════════════════════════

    private void mostrarMainApp() {
        rootLayout = new BorderPane();
        VBox sidebar = buildSidebar();
        rootLayout.setLeft(sidebar);

        contentArea = new VBox(20);
        contentArea.setPadding(new Insets(30));
        contentArea.setStyle("-fx-background-color: #f1f5f9;");
        rootLayout.setCenter(contentArea);

        showDashboard();
        primaryStage.getScene().setRoot(rootLayout);
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(25));
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color: #1e293b;");

        Label lblUser = new Label(usuarioActual.username());
        lblUser.setTextFill(Color.WHITE);
        lblUser.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label lblRol = new Label(usuarioActual.role());
        lblRol.setTextFill(Color.web("#94a3b8"));

        sidebar.getChildren().addAll(lblUser, lblRol, new Separator());

        Button btnHome = createMenuBtn("🏠 Dashboard");
        btnHome.setOnAction(e -> showDashboard());
        sidebar.getChildren().add(btnHome);

        switch (usuarioActual.role()) {
            case "ADMIN" -> {
                Button btnDoc = createMenuBtn("👨‍⚕️ Gestión Doctores");
                btnDoc.setOnAction(e -> showGestionDoctores());

                Button btnPac = createMenuBtn("👥 Ver Pacientes");
                btnPac.setOnAction(e -> showGestionPacientes());

                Button btnUsers = createMenuBtn("🔑 Registrar Usuario");
                btnUsers.setOnAction(e -> showRegistrarUsuario());

                sidebar.getChildren().addAll(btnDoc, btnPac, btnUsers);
            }
            case "DOCTOR" -> {
                Button btnCitas = createMenuBtn("📅 Mis Citas");
                btnCitas.setOnAction(e -> showCitasDoctor());

                Button btnCompletar = createMenuBtn("✅ Completar Cita");
                btnCompletar.setOnAction(e -> showCompletarCita());

                sidebar.getChildren().addAll(btnCitas, btnCompletar);
            }
            case "PATIENT" -> {
                Button btnRegistrar = createMenuBtn("📋 Mi Perfil");
                btnRegistrar.setOnAction(e -> showPerfilPaciente());

                Button btnCita = createMenuBtn("📅 Agendar Cita");
                btnCita.setOnAction(e -> showAgendarCita());

                Button btnMisCitas = createMenuBtn("📄 Mis Citas");
                btnMisCitas.setOnAction(e -> showMisCitas());

                sidebar.getChildren().addAll(btnRegistrar, btnCita, btnMisCitas);
            }
        }

        Button btnLogout = createMenuBtn("🚪 Cerrar Sesión");
        btnLogout.setOnAction(e -> {
            usuarioActual = null;
            mostrarLogin();
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().addAll(spacer, btnLogout);
        return sidebar;
    }

    private Button createMenuBtn(String txt) {
        Button b = new Button(txt);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setAlignment(Pos.CENTER_LEFT);
        b.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-padding: 10; -fx-cursor: hand;");
        return b;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DASHBOARD — usa repositorios reales para estadísticas
    // ══════════════════════════════════════════════════════════════════════════

    private void showDashboard() {
        contentArea.getChildren().clear();

        Label titulo = new Label("Dashboard — " + usuarioActual.role());
        titulo.setFont(Font.font("System", FontWeight.BOLD, 24));

        switch (usuarioActual.role()) {
            case "ADMIN" -> {
                // Datos reales desde repositorios
                List<Doctor> doctores = doctorService.obtenerDoctores();
                List<Patient> pacientes = PatientRepository.getPatientList();
                List<Appointment> citas = AppointmentRepository.getAllAppointments();

                long citasPendientes = citas.stream()
                        .filter(c -> c.getStatus().equals("PENDIENTE")).count();
                long citasCompletadas = citas.stream()
                        .filter(c -> c.getStatus().equals("COMPLETADA")).count();

                HBox cards = new HBox(20,
                        createCard("Doctores Registrados", String.valueOf(doctores.size()), "#3b82f6"),
                        createCard("Pacientes Registrados", String.valueOf(pacientes.size()), "#10b981"),
                        createCard("Citas Pendientes", String.valueOf(citasPendientes), "#f59e0b"),
                        createCard("Citas Completadas", String.valueOf(citasCompletadas), "#8b5cf6")
                );

                // Gráfica de estado de citas
                ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                        new PieChart.Data("Pendientes", citasPendientes),
                        new PieChart.Data("Completadas", citasCompletadas),
                        new PieChart.Data("Canceladas",
                                citas.stream().filter(c -> c.getStatus().equals("CANCELADA")).count())
                );
                PieChart chart = new PieChart(pieData);
                chart.setTitle("Estado de Citas");

                contentArea.getChildren().addAll(titulo, cards, chart);
            }
            case "DOCTOR" -> {
                // Buscar el doctor por cédula/email — el email del usuario coincide con la cédula del doctor
                Doctor doctor = buscarDoctorDelUsuario();
                if (doctor != null) {
                    List<Appointment> misCitas = AppointmentRepository.findByDoctorId(doctor.getId());
                    long pendientes = misCitas.stream().filter(c -> c.getStatus().equals("PENDIENTE")).count();
                    long enProgreso = misCitas.stream().filter(c -> c.getStatus().equals("EN_PROGRESO")).count();

                    HBox cards = new HBox(20,
                            createCard("Especialidad", doctor.getEspecialidad(), "#3b82f6"),
                            createCard("Citas Pendientes", String.valueOf(pendientes), "#f59e0b"),
                            createCard("En Progreso", String.valueOf(enProgreso), "#10b981")
                    );
                    contentArea.getChildren().addAll(titulo, cards,
                            new Label("Bienvenido Dr. " + doctor.getNombreCompleto()));
                } else {
                    contentArea.getChildren().addAll(titulo,
                            new Label("Bienvenido. No se encontró su ficha de doctor registrada."));
                }
            }
            case "PATIENT" -> {
                Patient paciente = PatientRepository.findByEmail(usuarioActual.username());
                if (paciente != null) {
                    List<Appointment> misCitas = AppointmentRepository.findByPatientName(paciente.name());
                    long pendientes = misCitas.stream().filter(c -> c.getStatus().equals("PENDIENTE")).count();

                    HBox cards = new HBox(20,
                            createCard("Mis Citas", String.valueOf(misCitas.size()), "#3b82f6"),
                            createCard("Pendientes", String.valueOf(pendientes), "#f59e0b")
                    );
                    contentArea.getChildren().addAll(titulo, cards,
                            new Label("Bienvenido, " + paciente.name() + "."));
                } else {
                    contentArea.getChildren().addAll(titulo,
                            new Label("Bienvenido. Completa tu perfil de paciente para agendar citas."));
                }
            }
        }
    }

    private VBox createCard(String title, String val, String color) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(20));
        card.setMinWidth(150);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        Label l1 = new Label(title);
        l1.setTextFill(Color.GRAY);
        Label l2 = new Label(val);
        l2.setFont(Font.font("System", FontWeight.BOLD, 26));
        l2.setTextFill(Color.web(color));
        card.getChildren().addAll(l1, l2);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ADMIN — GESTIÓN DE DOCTORES (usa DoctorService)
    // ══════════════════════════════════════════════════════════════════════════

    private void showGestionDoctores() {
        contentArea.getChildren().clear();

        Label titulo = new Label("Gestión de Doctores");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));

        // Tabla conectada a la lista real de DoctorService
        ObservableList<Doctor> obs = FXCollections.observableArrayList(doctorService.obtenerDoctores());
        TableView<Doctor> table = new TableView<>(obs);

        TableColumn<Doctor, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Doctor, String> colNom = new TableColumn<>("Nombre");
        colNom.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colNom.setPrefWidth(180);

        TableColumn<Doctor, Integer> colEdad = new TableColumn<>("Edad");
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));

        TableColumn<Doctor, String> colCed = new TableColumn<>("Cédula");
        colCed.setCellValueFactory(new PropertyValueFactory<>("cedula"));

        TableColumn<Doctor, String> colEsp = new TableColumn<>("Especialidad");
        colEsp.setCellValueFactory(new PropertyValueFactory<>("especialidad"));
        colEsp.setPrefWidth(160);

        table.getColumns().addAll(colId, colNom, colEdad, colCed, colEsp);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Formulario para nuevo doctor
        TitledPane paneNuevo = buildFormNuevoDoctor(obs);

        // Botón eliminar — usa DoctorService.eliminarDoctorPorCedula()
        Button btnDel = new Button("🗑 Eliminar Seleccionado");
        btnDel.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white;");
        btnDel.setOnAction(e -> {
            Doctor sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showMsg("Aviso", "Selecciona un doctor primero."); return; }
            try {
                doctorService.eliminarDoctorPorCedula(sel.getCedula());
                obs.setAll(doctorService.obtenerDoctores());
                showMsg("Eliminado", "Doctor eliminado correctamente.");
            } catch (CustomException ex) {
                showMsg("Error", ex.getMessage());
            }
        });

        // Botón editar especialidad — usa DoctorService.actualizarDoctor()
        Button btnEdit = new Button("✏️ Editar Especialidad");
        btnEdit.setOnAction(e -> {
            Doctor sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showMsg("Aviso", "Selecciona un doctor primero."); return; }
            TextInputDialog dlg = new TextInputDialog(sel.getEspecialidad());
            dlg.setTitle("Editar Especialidad");
            dlg.setHeaderText("Nueva especialidad para: " + sel.getNombreCompleto());
            dlg.showAndWait().ifPresent(nuevaEsp -> {
                try {
                    doctorService.actualizarDoctor(
                            sel.getId(), sel.getNombreCompleto(),
                            sel.getEdad(), sel.getCedula(), nuevaEsp);
                    obs.setAll(doctorService.obtenerDoctores());
                } catch (CustomException ex) {
                    showMsg("Error", ex.getMessage());
                }
            });
        });

        contentArea.getChildren().addAll(titulo, table, new HBox(10, btnEdit, btnDel), paneNuevo);
    }

    private TitledPane buildFormNuevoDoctor(ObservableList<Doctor> obs) {
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(15));

        TextField txtNombre = new TextField(); txtNombre.setPromptText("Nombre completo");
        TextField txtEdad   = new TextField(); txtEdad.setPromptText("Edad");
        TextField txtCedula = new TextField(); txtCedula.setPromptText("Cédula (6-10 dígitos)");
        TextField txtEsp    = new TextField(); txtEsp.setPromptText("Especialidad");

        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Edad:"),   txtEdad);
        grid.addRow(2, new Label("Cédula:"), txtCedula);
        grid.addRow(3, new Label("Especialidad:"), txtEsp);

        Button btnGuardar = new Button("Registrar Doctor");
        btnGuardar.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white;");
        btnGuardar.setOnAction(e -> {
            try {
                int edad = Integer.parseInt(txtEdad.getText().trim());
                // DoctorService valida todos los campos y lanza CustomException si hay error
                doctorService.registrarDoctor(
                        txtNombre.getText().trim(),
                        edad,
                        txtCedula.getText().trim(),
                        txtEsp.getText().trim()
                );
                obs.setAll(doctorService.obtenerDoctores());
                txtNombre.clear(); txtEdad.clear(); txtCedula.clear(); txtEsp.clear();
                showMsg("Guardado", "Doctor registrado. ID asignado: " +
                        doctorService.obtenerUltimoRegistrado().getId());
            } catch (NumberFormatException ex) {
                showMsg("Error", "La edad debe ser un número.");
            } catch (CustomException ex) {
                showMsg("Error de validación", ex.getMessage());
            }
        });

        grid.add(btnGuardar, 1, 4);
        TitledPane pane = new TitledPane("➕ Registrar Nuevo Doctor", grid);
        pane.setExpanded(false);
        return pane;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ADMIN — VER PACIENTES (PatientRepository)
    // ══════════════════════════════════════════════════════════════════════════

    private void showGestionPacientes() {
        contentArea.getChildren().clear();

        Label titulo = new Label("Pacientes Registrados");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));

        ObservableList<Patient> obs = FXCollections.observableArrayList(PatientRepository.getPatientList());
        TableView<Patient> table = new TableView<>(obs);

        // Patient es un record — sus accesores no tienen prefijo "get", por eso
        // PropertyValueFactory NO funciona. Usamos lambdas directamente.
        TableColumn<Patient, String> colNom   = new TableColumn<>("Nombre");
        colNom.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().name()));
        colNom.setPrefWidth(180);

        TableColumn<Patient, String> colNac   = new TableColumn<>("Nacionalidad");
        colNac.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().nationality()));
        colNac.setPrefWidth(120);

        TableColumn<Patient, String> colTel   = new TableColumn<>("Teléfono");
        colTel.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().phone()));
        colTel.setPrefWidth(120);

        TableColumn<Patient, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().email()));
        colEmail.setPrefWidth(200);

        TableColumn<Patient, String> colAge   = new TableColumn<>("Edad");
        colAge.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(String.valueOf(d.getValue().age())));
        colAge.setPrefWidth(60);

        TableColumn<Patient, String> colId    = new TableColumn<>("Cédula");
        colId.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().id()));
        colId.setPrefWidth(120);

        table.getColumns().addAll(colNom, colNac, colTel, colEmail, colAge, colId);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        contentArea.getChildren().addAll(titulo, table);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ADMIN — REGISTRAR USUARIO (UserRepository + LoginService)
    // ══════════════════════════════════════════════════════════════════════════

    private void showRegistrarUsuario() {
        contentArea.getChildren().clear();

        Label titulo = new Label("Registrar Nuevo Usuario");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));

        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setMaxWidth(400);
        form.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        TextField txtEmail = new TextField(); txtEmail.setPromptText("Email (username)");
        PasswordField txtPass = new PasswordField(); txtPass.setPromptText("Contraseña (mín. 4 chars, debe tener número)");
        ComboBox<String> cbRol = new ComboBox<>(
                FXCollections.observableArrayList("PATIENT", "DOCTOR", "ADMIN"));
        cbRol.setValue("PATIENT");

        Button btnGuardar = new Button("Registrar Usuario");
        btnGuardar.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-padding: 10 20;");
        btnGuardar.setOnAction(e -> {
            try {
                // LoginService.registerUser() valida y usa UserRepository internamente
                loginService.registerUser(txtEmail.getText().trim(), txtPass.getText(), cbRol.getValue());
                showMsg("Registrado", "Usuario '" + txtEmail.getText() + "' creado como " + cbRol.getValue());
                txtEmail.clear(); txtPass.clear();
            } catch (Exception ex) {
                showMsg("Error", ex.getMessage());
            }
        });

        form.getChildren().addAll(new Label("Email:"), txtEmail,
                new Label("Contraseña:"), txtPass,
                new Label("Rol:"), cbRol, btnGuardar);
        contentArea.getChildren().addAll(titulo, form);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // DOCTOR — VER SUS CITAS (AppointmentRepository)
    // ══════════════════════════════════════════════════════════════════════════

    private void showCitasDoctor() {
        contentArea.getChildren().clear();

        Doctor doctor = buscarDoctorDelUsuario();
        if (doctor == null) {
            contentArea.getChildren().add(
                    new Label("No se encontró un doctor asociado a este usuario.\n" +
                            "Pide al administrador que registre tu ficha de doctor con tu cédula como email."));
            return;
        }

        Label titulo = new Label("Mis Citas — Dr. " + doctor.getNombreCompleto());
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));

        List<Appointment> citas = AppointmentRepository.findByDoctorId(doctor.getId());
        ObservableList<Appointment> obs = FXCollections.observableArrayList(citas);

        TableView<Appointment> table = buildCitasTable(obs);
        contentArea.getChildren().addAll(titulo, table);
    }

    private void showCompletarCita() {
        contentArea.getChildren().clear();

        Doctor doctor = buscarDoctorDelUsuario();
        if (doctor == null) {
            contentArea.getChildren().add(new Label("Doctor no encontrado en el sistema."));
            return;
        }

        Label titulo = new Label("Completar / Iniciar Cita");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));

        // Solo citas PENDIENTE o EN_PROGRESO del doctor
        List<Appointment> disponibles = AppointmentRepository.getAllAppointments().stream()
                .filter(a -> a.getDoctorId() == doctor.getId() &&
                        (a.getStatus().equals("PENDIENTE") || a.getStatus().equals("EN_PROGRESO")))
                .toList();

        if (disponibles.isEmpty()) {
            contentArea.getChildren().addAll(titulo, new Label("No tienes citas pendientes o en progreso."));
            return;
        }

        ObservableList<Appointment> obs = FXCollections.observableArrayList(disponibles);
        TableView<Appointment> table = buildCitasTable(obs);

        TextField txtCosto = new TextField(); txtCosto.setPromptText("Costo $");
        TextArea txtNotas  = new TextArea(); txtNotas.setPromptText("Notas de la consulta"); txtNotas.setPrefHeight(80);

        Button btnIniciar = new Button("▶ Iniciar Cita");
        btnIniciar.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white;");
        btnIniciar.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showMsg("Aviso", "Selecciona una cita."); return; }
            if (!sel.getStatus().equals("PENDIENTE")) { showMsg("Aviso", "Solo se pueden iniciar citas PENDIENTE."); return; }
            sel.setStatus("EN_PROGRESO");
            AppointmentRepository.updateAppointment(sel);
            obs.setAll(AppointmentRepository.getAllAppointments().stream()
                    .filter(a -> a.getDoctorId() == doctor.getId() &&
                            (a.getStatus().equals("PENDIENTE") || a.getStatus().equals("EN_PROGRESO")))
                    .toList());
            table.refresh();
        });

        Button btnCompletar = new Button("✅ Completar Cita");
        btnCompletar.setStyle("-fx-background-color: #10b981; -fx-text-fill: white;");
        btnCompletar.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showMsg("Aviso", "Selecciona una cita."); return; }
            try {
                double costo = Double.parseDouble(txtCosto.getText().trim());
                // Usa el método completar() del modelo Appointment
                sel.completar(costo, txtNotas.getText().trim());
                AppointmentRepository.updateAppointment(sel);
                obs.remove(sel);
                txtCosto.clear(); txtNotas.clear();
                showMsg("Completada", "Cita completada. Costo registrado: $" + String.format("%.2f", costo));
            } catch (NumberFormatException ex) {
                showMsg("Error", "Ingresa un costo numérico válido.");
            } catch (IllegalStateException ex) {
                showMsg("Error", ex.getMessage());
            }
        });

        contentArea.getChildren().addAll(titulo, table,
                new Label("Costo ($):"), txtCosto,
                new Label("Notas:"), txtNotas,
                new HBox(10, btnIniciar, btnCompletar));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PACIENTE — PERFIL
    // ══════════════════════════════════════════════════════════════════════════

    private void showPerfilPaciente() {
        contentArea.getChildren().clear();

        Label titulo = new Label("Mi Perfil de Paciente");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));

        Patient p = PatientRepository.findByEmail(usuarioActual.username());

        if (p != null) {
            // Mostrar ficha del paciente (Patient es un record, sus campos son inmutables)
            GridPane grid = new GridPane();
            grid.setHgap(15); grid.setVgap(10); grid.setPadding(new Insets(20));
            grid.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

            grid.addRow(0, bold("Nombre:"),       new Label(p.name()));
            grid.addRow(1, bold("Cédula:"),        new Label(p.id()));
            grid.addRow(2, bold("Nacionalidad:"),  new Label(p.nationality()));
            grid.addRow(3, bold("Teléfono:"),      new Label(p.phone()));
            grid.addRow(4, bold("Email:"),         new Label(p.email()));
            grid.addRow(5, bold("Edad:"),          new Label(String.valueOf(p.age()) + " años"));

            contentArea.getChildren().addAll(titulo, grid);
        } else {
            // Formulario de registro — crea un Patient record con las validaciones del modelo
            contentArea.getChildren().addAll(titulo, buildFormRegistroPaciente());
        }
    }

    private VBox buildFormRegistroPaciente() {
        VBox form = new VBox(10);
        form.setPadding(new Insets(20));
        form.setMaxWidth(450);
        form.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        Label aviso = new Label("Aún no tienes perfil. Completa tus datos:");
        aviso.setTextFill(Color.GRAY);

        TextField txtNombre = new TextField(); txtNombre.setPromptText("Nombre completo (solo letras)");
        TextField txtNac    = new TextField(); txtNac.setPromptText("Nacionalidad");
        TextField txtTel    = new TextField(); txtTel.setPromptText("Celular colombiano (ej: 3001234567)");
        TextField txtEdad   = new TextField(); txtEdad.setPromptText("Edad");
        TextField txtCedula = new TextField(); txtCedula.setPromptText("Cédula (6-10 dígitos)");

        Button btnGuardar = new Button("Guardar Perfil");
        btnGuardar.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-padding: 10 20;");
        btnGuardar.setOnAction(e -> {
            try {
                int edad = Integer.parseInt(txtEdad.getText().trim());
                // Patient record valida todos los campos en su constructor compacto
                Patient nuevo = new Patient(
                        txtNombre.getText().trim(),
                        txtNac.getText().trim(),
                        txtTel.getText().trim(),
                        usuarioActual.username(),   // email = username del login
                        edad,
                        txtCedula.getText().trim()
                );
                PatientRepository.addPatient(nuevo);
                showMsg("Guardado", "Perfil registrado correctamente.");
                showPerfilPaciente(); // recargar vista
            } catch (NumberFormatException ex) {
                showMsg("Error", "La edad debe ser un número.");
            } catch (IllegalArgumentException ex) {
                // Validaciones del record Patient
                showMsg("Error de validación", ex.getMessage());
            }
        });

        form.getChildren().addAll(aviso, txtNombre, txtNac, txtTel, txtEdad, txtCedula, btnGuardar);
        return form;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PACIENTE — AGENDAR CITA (AppointmentRepository + Appointment model)
    // ══════════════════════════════════════════════════════════════════════════

    private void showAgendarCita() {
        contentArea.getChildren().clear();

        Patient paciente = PatientRepository.findByEmail(usuarioActual.username());
        if (paciente == null) {
            contentArea.getChildren().addAll(
                    new Label("Primero debes completar tu perfil de paciente."),
                    createActionButton("Ir a Mi Perfil", "#3b82f6", e -> showPerfilPaciente())
            );
            return;
        }

        Label titulo = new Label("Agendar Cita Médica");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));

        VBox form = new VBox(12);
        form.setPadding(new Insets(20));
        form.setMaxWidth(500);
        form.setStyle("-fx-background-color: white; -fx-background-radius: 10;");

        // Doctor selector — lista real de DoctorService
        List<Doctor> doctores = doctorService.obtenerDoctores();
        ComboBox<Doctor> cbDoc = new ComboBox<>(FXCollections.observableArrayList(doctores));
        cbDoc.setConverter(new javafx.util.StringConverter<>() {
            public String toString(Doctor d) {
                return d == null ? "" : d.getNombreCompleto() + " — " + d.getEspecialidad();
            }
            public Doctor fromString(String s) { return null; }
        });
        cbDoc.setMaxWidth(Double.MAX_VALUE);

        TextArea txtMotivo = new TextArea();
        txtMotivo.setPromptText("Motivo de consulta / síntomas");
        txtMotivo.setPrefHeight(80);

        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));

        ComboBox<String> cbHora = new ComboBox<>(FXCollections.observableArrayList(
                "08:00", "09:00", "10:00", "11:00", "14:00", "15:00", "16:00"));
        cbHora.setValue("09:00");

        Button btnReservar = new Button("RESERVAR CITA");
        btnReservar.setMaxWidth(Double.MAX_VALUE);
        btnReservar.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12;");

        btnReservar.setOnAction(e -> {
            if (cbDoc.getValue() == null || cbHora.getValue() == null ||
                    datePicker.getValue() == null || txtMotivo.getText().trim().isEmpty()) {
                showMsg("Aviso", "Completa todos los campos incluyendo el motivo de consulta.");
                return;
            }

            Doctor docSel = cbDoc.getValue();
            LocalDate fecha = datePicker.getValue();

            if (fecha.isBefore(LocalDate.now())) {
                showMsg("Aviso", "No puedes agendar citas en el pasado.");
                return;
            }

            LocalTime hora = LocalTime.parse(cbHora.getValue());
            LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);

            // Verificar disponibilidad usando AppointmentRepository
            boolean ocupado = AppointmentRepository.findByDoctorId(docSel.getId()).stream()
                    .anyMatch(a -> a.getAppointmentDateTime().equals(fechaHora) &&
                            !a.getStatus().equals("CANCELADA"));

            if (ocupado) {
                showMsg("No disponible", "El doctor ya tiene una cita en ese horario. Elige otro.");
                return;
            }

            // Crear Appointment con el constructor real del modelo
            Appointment nueva = new Appointment(
                    paciente.name(),
                    paciente.id(),
                    docSel.getId(),
                    docSel.getNombreCompleto(),
                    docSel.getEspecialidad(),
                    fechaHora
            );
            nueva.setNotes("Motivo: " + txtMotivo.getText().trim());
            AppointmentRepository.addAppointment(nueva);

            showMsg("Cita Agendada",
                    "Tu cita con el Dr. " + docSel.getNombreCompleto() +
                            "\nFecha: " + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                            " a las " + cbHora.getValue() +
                            "\nID de cita: " + nueva.getId());
            showDashboard();
        });

        form.getChildren().addAll(
                new Label("Doctor:"), cbDoc,
                new Label("Motivo de consulta:"), txtMotivo,
                new Label("Fecha:"), datePicker,
                new Label("Hora:"), cbHora,
                btnReservar
        );
        contentArea.getChildren().addAll(titulo, form);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PACIENTE — MIS CITAS + CANCELAR
    // ══════════════════════════════════════════════════════════════════════════

    private void showMisCitas() {
        contentArea.getChildren().clear();

        Patient paciente = PatientRepository.findByEmail(usuarioActual.username());
        if (paciente == null) {
            contentArea.getChildren().add(new Label("Primero completa tu perfil de paciente."));
            return;
        }

        Label titulo = new Label("Mis Citas — " + paciente.name());
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));

        List<Appointment> citas = AppointmentRepository.findByPatientName(paciente.name());
        ObservableList<Appointment> obs = FXCollections.observableArrayList(citas);
        TableView<Appointment> table = buildCitasTable(obs);

        // Total gastado en citas COMPLETADAS
        double total = citas.stream()
                .filter(a -> a.getStatus().equals("COMPLETADA"))
                .mapToDouble(Appointment::getCost).sum();
        Label lblTotal = new Label(String.format("Total gastado: $%.2f", total));
        lblTotal.setFont(Font.font("System", FontWeight.BOLD, 14));
        lblTotal.setTextFill(Color.web("#10b981"));

        // Botón cancelar — usa Appointment.cancelar()
        Button btnCancelar = new Button("❌ Cancelar Cita Seleccionada");
        btnCancelar.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white;");
        btnCancelar.setOnAction(e -> {
            Appointment sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) { showMsg("Aviso", "Selecciona una cita."); return; }
            try {
                sel.cancelar(); // valida que esté PENDIENTE
                AppointmentRepository.updateAppointment(sel);
                obs.setAll(AppointmentRepository.findByPatientName(paciente.name()));
                showMsg("Cancelada", "Cita cancelada exitosamente.");
            } catch (IllegalStateException ex) {
                showMsg("No se puede cancelar", ex.getMessage());
            }
        });

        contentArea.getChildren().addAll(titulo, table, lblTotal, btnCancelar);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // UTILIDADES
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Busca el Doctor asociado al usuario logueado.
     * El email del usuario (username) puede coincidir con la cédula del doctor,
     * o el nombre puede estar registrado en el repositorio de doctores.
     */
    private Doctor buscarDoctorDelUsuario() {
        String username = usuarioActual.username();
        DoctorRepository repo = new DoctorRepository();

        // Intentar por cédula (si el username es la cédula del doctor)
        Doctor porCedula = repo.buscarPorCedula(username);
        if (porCedula != null) return porCedula;

        // Intentar por nombre parcial
        return repo.obtenerDoctores().stream()
                .filter(d -> d.getNombreCompleto().toLowerCase()
                        .contains(username.split("@")[0].toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    private TableView<Appointment> buildCitasTable(ObservableList<Appointment> obs) {
        TableView<Appointment> table = new TableView<>(obs);

        TableColumn<Appointment, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Appointment, String> colPac = new TableColumn<>("Paciente");
        colPac.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colPac.setPrefWidth(150);

        TableColumn<Appointment, String> colDoc = new TableColumn<>("Doctor");
        colDoc.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colDoc.setPrefWidth(150);

        TableColumn<Appointment, String> colEsp = new TableColumn<>("Especialidad");
        colEsp.setCellValueFactory(new PropertyValueFactory<>("doctorSpecialty"));
        colEsp.setPrefWidth(130);

        TableColumn<Appointment, LocalDateTime> colFecha = new TableColumn<>("Fecha y Hora");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("appointmentDateTime"));
        colFecha.setPrefWidth(140);

        TableColumn<Appointment, String> colEst = new TableColumn<>("Estado");
        colEst.setCellValueFactory(new PropertyValueFactory<>("status"));
        colEst.setPrefWidth(110);

        TableColumn<Appointment, Double> colCosto = new TableColumn<>("Costo $");
        colCosto.setCellValueFactory(new PropertyValueFactory<>("cost"));

        table.getColumns().addAll(colId, colPac, colDoc, colEsp, colFecha, colEst, colCosto);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        return table;
    }

    private Label bold(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("System", FontWeight.BOLD, 13));
        return l;
    }

    private Button createActionButton(String text, String color,
                                      javafx.event.EventHandler<javafx.event.ActionEvent> handler) {
        Button b = new Button(text);
        b.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-padding: 10 20;");
        b.setOnAction(handler);
        return b;
    }

    private void showMsg(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}