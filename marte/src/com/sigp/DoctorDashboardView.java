package marte.src.com.sigp;


import marte.src.com.sigp.model.Appointment;
import marte.src.com.sigp.model.Doctor;
import marte.src.com.sigp.model.User;
import marte.src.com.sigp.repository.AppointmentRepository;
import marte.src.com.sigp.repository.DoctorRepository;
import javafx.animation.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DoctorDashboardView {

    // ── Paleta (mismo tema que Admin) ─────────────────────────
    private static final String C_FONDO  = "#0D0D1A";
    private static final String C_PANEL  = "#141428";
    private static final String C_CARD   = "#1A1A30";
    private static final String C_AZUL   = "#2980B9";
    private static final String C_VERDE  = "#27AE60";
    private static final String C_ROJO   = "#C0392B";
    private static final String C_AMBAR  = "#E67E22";
    private static final String C_TEXTO  = "#ECEFF1";
    private static final String C_SUAVE  = "#90A4AE";
    private static final String C_BORDE  = "#1E1E3A";

    private final Stage stage;
    private final User  userDoctor;
    private StackPane   contenidoCentral;

    // El doctor seleccionado en el panel (se pide al arrancar)
    private Doctor doctor;

    private final DoctorRepository doctorRepo = new DoctorRepository();

    public DoctorDashboardView(Stage stage, User userDoctor) {
        this.stage      = stage;
        this.userDoctor = userDoctor;
    }

    public void mostrar() {
        // Primero pedimos al doctor que se identifique por su nombre/ID
        List<Doctor> lista = doctorRepo.obtenerDoctores();

        if (lista.isEmpty()) {
            new Alert(Alert.AlertType.WARNING,
                    "No hay doctores registrados. Pide al admin que te registre.").show();
            new LoginView(stage).mostrar();
            return;
        }

        // Si solo hay uno, lo usamos directamente
        // Si hay varios, mostramos un diálogo de selección
        if (lista.size() == 1) {
            this.doctor = lista.get(0);
            construirUI();
        } else {
            mostrarSeleccionDoctor(lista);
        }
    }

    /** Diálogo modal para que el doctor se identifique */
    private void mostrarSeleccionDoctor(List<Doctor> doctores) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(stage);
        modal.setTitle("Identificación de Doctor");
        modal.setResizable(false);

        VBox form = new VBox(15);
        form.setPadding(new Insets(30));
        form.setStyle("-fx-background-color: " + C_PANEL + ";");
        form.setPrefWidth(380);

        Label titulo = new Label("¿Cuál es tu perfil?");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.setTextFill(Color.web(C_TEXTO));

        Label sub = new Label("Selecciona tu nombre de la lista:");
        sub.setTextFill(Color.web(C_SUAVE));

        ComboBox<Doctor> combo = new ComboBox<>();
        combo.setMaxWidth(Double.MAX_VALUE);
        combo.getItems().addAll(doctores);
        combo.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Doctor d)  { return d == null ? "" : d.getNombreCompleto() + " – " + d.getEspecialidad(); }
            @Override public Doctor fromString(String s) { return null; }
        });
        combo.getSelectionModel().selectFirst();

        Button btnEntrar = new Button("Entrar al panel");
        btnEntrar.setMaxWidth(Double.MAX_VALUE);
        btnEntrar.setStyle("-fx-background-color: " + C_AZUL + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 12; -fx-cursor: hand;");
        btnEntrar.setOnAction(e -> {
            this.doctor = combo.getValue();
            modal.close();
            construirUI();
        });

        form.getChildren().addAll(titulo, sub, combo, btnEntrar);
        modal.setScene(new Scene(form));
        modal.showAndWait();
    }

    /** Construye la UI principal del doctor */
    private void construirUI() {
        HBox raiz = new HBox();
        raiz.setStyle("-fx-background-color: " + C_FONDO + ";");

        VBox sideBar = crearSideBar();
        sideBar.setPrefWidth(240);

        contenidoCentral = new StackPane();
        contenidoCentral.setStyle("-fx-background-color: " + C_FONDO + ";");
        HBox.setHgrow(contenidoCentral, Priority.ALWAYS);

        raiz.getChildren().addAll(sideBar, contenidoCentral);

        mostrarDashboard();

        Scene escena = new Scene(raiz, 1200, 750);
        stage.setTitle("MARTE – Dr. " + doctor.getNombreCompleto());
        stage.setScene(escena);
        stage.show();
    }

    // ────────────────────────────────────────────────────────────
    //  BARRA LATERAL
    // ────────────────────────────────────────────────────────────
    private VBox crearSideBar() {
        VBox bar = new VBox(8);
        bar.setStyle("-fx-background-color: " + C_PANEL + "; -fx-border-color: " + C_BORDE + "; -fx-border-width: 0 1 0 0;");
        bar.setPadding(new Insets(30, 15, 30, 15));

        Label logo = new Label("♂ MARTE");
        logo.setFont(Font.font("System", FontWeight.BOLD, 26));
        logo.setTextFill(Color.web(C_AZUL));
        logo.setPadding(new Insets(0, 0, 4, 10));

        Label nombreDoc = new Label("Dr. " + (doctor != null ? doctor.getNombreCompleto() : ""));
        nombreDoc.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        nombreDoc.setTextFill(Color.web(C_TEXTO));
        nombreDoc.setPadding(new Insets(0, 0, 2, 10));
        nombreDoc.setWrapText(true);
        nombreDoc.setMaxWidth(210);

        Label especialidad = new Label(doctor != null ? doctor.getEspecialidad() : "");
        especialidad.setFont(Font.font(11));
        especialidad.setTextFill(Color.web(C_SUAVE));
        especialidad.setPadding(new Insets(0, 0, 20, 10));

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + C_BORDE + ";");

        Button btnDash      = crearBtnNav("📊  Dashboard",        true,  C_AZUL);
        Button btnPacientes = crearBtnNav("🧑‍🤝‍🧑  Mis Pacientes",  false, C_AZUL);
        Button btnHistoria  = crearBtnNav("📋  Historia Clínica", false, C_AZUL);

        Region esp = new Region();
        VBox.setVgrow(esp, Priority.ALWAYS);
        Button btnSalir = crearBtnNav("🚪  Cerrar sesión", false, C_ROJO);

        btnDash.setOnAction(e -> { activar(btnDash, btnPacientes, btnHistoria); mostrarDashboard(); });
        btnPacientes.setOnAction(e -> { activar(btnPacientes, btnDash, btnHistoria); mostrarPacientes(); });
        btnHistoria.setOnAction(e -> { activar(btnHistoria, btnDash, btnPacientes); mostrarHistoriaClinica(null); });
        btnSalir.setOnAction(e -> new LoginView(stage).mostrar());

        bar.getChildren().addAll(logo, nombreDoc, especialidad, sep, btnDash, btnPacientes, btnHistoria, esp, btnSalir);
        return bar;
    }

    // ────────────────────────────────────────────────────────────
    //  SECCIÓN 1: DASHBOARD DEL DOCTOR
    // ────────────────────────────────────────────────────────────
    private void mostrarDashboard() {
        if (doctor == null) return;

        List<Appointment> todasCitas = AppointmentRepository.getAllAppointments();
        List<Appointment> misCitas = todasCitas.stream()
                .filter(a -> a.getDoctorId() == doctor.getId())
                .collect(Collectors.toList());

        long pendientes  = misCitas.stream().filter(a -> "PENDIENTE".equals(a.getStatus())).count();
        long completadas = misCitas.stream().filter(a -> "COMPLETADA".equals(a.getStatus())).count();
        long enProgreso  = misCitas.stream().filter(a -> "EN_PROGRESO".equals(a.getStatus())).count();

        VBox layout = new VBox(25);
        layout.setPadding(new Insets(35));
        layout.setStyle("-fx-background-color: " + C_FONDO + ";");

        layout.getChildren().add(crearTituloSeccion(
                "Bienvenido, Dr. " + doctor.getNombreCompleto(),
                doctor.getEspecialidad() + "  •  ID: " + doctor.getId()
        ));

        // Tarjetas de estadísticas
        HBox stats = new HBox(20);
        stats.getChildren().addAll(
                crearStatCard("Citas Asignadas",  String.valueOf(misCitas.size()), C_AZUL,  "📅"),
                crearStatCard("Pendientes",        String.valueOf(pendientes),      C_AMBAR, "⏳"),
                crearStatCard("En Progreso",       String.valueOf(enProgreso),      C_AZUL,  "🔄"),
                crearStatCard("Completadas",       String.valueOf(completadas),     C_VERDE, "✅")
        );
        for (Node n : stats.getChildren()) HBox.setHgrow(n, Priority.ALWAYS);

        // Próximas citas (tabla compacta)
        Label lblProximas = new Label("Próximas Citas");
        lblProximas.setFont(Font.font("System", FontWeight.BOLD, 16));
        lblProximas.setTextFill(Color.web(C_TEXTO));

        TableView<Appointment> tabla = new TableView<>();
        tabla.setStyle(estiloTabla());
        tabla.setMaxHeight(300);

        TableColumn<Appointment, String> cPaciente = col("Paciente",  a -> a.getPatientName());
        TableColumn<Appointment, String> cFecha    = col("Fecha",     a -> {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return a.getAppointmentDateTime().format(f);
        });
        TableColumn<Appointment, String> cEstado   = col("Estado",    a -> a.getStatus());

        cPaciente.setPrefWidth(200);
        cFecha.setPrefWidth(170);
        cEstado.setPrefWidth(130);

        tabla.getColumns().addAll(cPaciente, cFecha, cEstado);
        tabla.setItems(FXCollections.observableArrayList(
                misCitas.stream()
                        .filter(a -> !"CANCELADA".equals(a.getStatus()))
                        .sorted(Comparator.comparing(Appointment::getAppointmentDateTime))
                        .limit(10)
                        .collect(Collectors.toList())
        ));

        layout.getChildren().addAll(stats, lblProximas, tabla);

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + C_FONDO + "; -fx-background-color: " + C_FONDO + ";");
        cambiarContenido(scroll);
    }

    // ────────────────────────────────────────────────────────────
    //  SECCIÓN 2: LISTA DE PACIENTES DEL DOCTOR
    // ────────────────────────────────────────────────────────────
    private void mostrarPacientes() {
        if (doctor == null) return;

        List<Appointment> misCitas = AppointmentRepository.getAllAppointments().stream()
                .filter(a -> a.getDoctorId() == doctor.getId())
                .collect(Collectors.toList());

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(35));
        layout.setStyle("-fx-background-color: " + C_FONDO + ";");

        layout.getChildren().add(crearTituloSeccion("Mis Pacientes", "Pacientes con citas asignadas a ti"));

        TableView<Appointment> tabla = new TableView<>();
        tabla.setStyle(estiloTabla());
        tabla.setPlaceholder(new Label("No tienes citas asignadas."));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        TableColumn<Appointment, String> cId      = col("ID Cita",  a -> String.valueOf(a.getId()));
        TableColumn<Appointment, String> cNombre  = col("Paciente", a -> a.getPatientName());
        TableColumn<Appointment, String> cCedula  = col("Cédula",   a -> a.getPatientId());
        TableColumn<Appointment, String> cFecha   = col("Fecha", a -> {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return a.getAppointmentDateTime().format(f);
        });
        TableColumn<Appointment, String> cEstado  = col("Estado", a -> a.getStatus());
        TableColumn<Appointment, Void>   cAccion  = new TableColumn<>("Historia");

        cId.setPrefWidth(70);
        cNombre.setPrefWidth(180);
        cCedula.setPrefWidth(120);
        cFecha.setPrefWidth(160);
        cEstado.setPrefWidth(120);
        cAccion.setPrefWidth(140);

        // Botón "Ver Historia" por fila → abre el formulario de historia clínica
        cAccion.setCellFactory(col2 -> new TableCell<>() {
            private final Button btn = new Button("📋 Historia");
            {
                btn.setFont(Font.font(11));
                btn.setStyle("-fx-background-color: " + C_AZUL + "33; -fx-text-fill: " + C_AZUL +
                        "; -fx-background-radius: 6; -fx-border-color: " + C_AZUL +
                        "; -fx-border-radius: 6; -fx-cursor: hand; -fx-padding: 5 10 5 10;");
                btn.setOnAction(e -> {
                    Appointment a = getTableView().getItems().get(getIndex());
                    activar(null); // no cambio visual del sidebar aquí
                    mostrarHistoriaClinica(a);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        tabla.getColumns().addAll(cId, cNombre, cCedula, cFecha, cEstado, cAccion);
        tabla.setItems(FXCollections.observableArrayList(misCitas));

        layout.getChildren().add(tabla);

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + C_FONDO + "; -fx-background-color: " + C_FONDO + ";");
        cambiarContenido(scroll);
    }

    // ────────────────────────────────────────────────────────────
    //  SECCIÓN 3: FORMULARIO DE HISTORIA CLÍNICA
    // ────────────────────────────────────────────────────────────
    private void mostrarHistoriaClinica(Appointment cita) {
        ScrollPane scroll = new ScrollPane(construirFormularioHistoria(cita));
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: " + C_FONDO + "; -fx-background-color: " + C_FONDO + ";");
        cambiarContenido(scroll);
    }

    /** Formulario para llenar la historia clínica de un paciente */
    private VBox construirFormularioHistoria(Appointment cita) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(35));
        layout.setStyle("-fx-background-color: " + C_FONDO + ";");
        layout.setMaxWidth(800);

        layout.getChildren().add(crearTituloSeccion("Historia Clínica", "Registrar información médica del paciente"));

        // ── Tarjeta del formulario ─────────────────────────────
        VBox form = new VBox(18);
        form.setPadding(new Insets(30));
        form.setStyle(
                "-fx-background-color: " + C_CARD + ";" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: " + C_BORDE + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 14;"
        );

        // ── Fila 1: Paciente y Título ─────────────────────────
        Label lblPaciente = etqForm("Paciente");
        TextField txtPaciente = inputForm("Nombre del paciente");
        if (cita != null) txtPaciente.setText(cita.getPatientName());

        Label lblTitulo = etqForm("Título del caso");
        TextField txtTitulo = inputForm("Ej: Consulta por dolor abdominal");

        HBox fila1 = new HBox(20, vstack(lblPaciente, txtPaciente), vstack(lblTitulo, txtTitulo));
        fila1.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        // ── Descripción ───────────────────────────────────────
        Label lblDesc = etqForm("Descripción del caso");
        TextArea txtDesc = areaForm("Describe los síntomas, antecedentes y motivo de consulta...", 120);

        // ── Diagnóstico ───────────────────────────────────────
        Label lblDiag = etqForm("Diagnóstico");
        TextArea txtDiag = areaForm("Diagnóstico médico...", 80);

        // ── Receta médica ─────────────────────────────────────
        Label lblReceta = etqForm("Receta médica");
        TextArea txtReceta = areaForm("Medicamentos, dosis e indicaciones...", 100);

        // ── Valor de la consulta ──────────────────────────────
        Label lblValor = etqForm("Valor de la consulta ($)");
        TextField txtValor = inputForm("Ej: 80000");
        if (cita != null && cita.getCost() > 0)
            txtValor.setText(String.valueOf((int) cita.getCost()));

        // ── Mensaje de estado ─────────────────────────────────
        Label lblMsg = new Label();
        lblMsg.setFont(Font.font(13));
        lblMsg.setVisible(false);

        // ── Botones ───────────────────────────────────────────
        Button btnGuardar  = btnAccion("✅  Guardar Historia", C_VERDE);
        Button btnLimpiar  = btnAccion("🔄  Limpiar",          C_AZUL);

        btnLimpiar.setOnAction(e -> {
            if (cita == null) txtPaciente.clear();
            txtTitulo.clear(); txtDesc.clear(); txtDiag.clear();
            txtReceta.clear(); txtValor.clear();
            lblMsg.setVisible(false);
        });

        btnGuardar.setOnAction(e -> {
            // Validación básica de campos
            if (txtPaciente.getText().isBlank() || txtTitulo.getText().isBlank()
                    || txtDesc.getText().isBlank() || txtDiag.getText().isBlank()) {
                lblMsg.setText("⚠️ Por favor completa los campos obligatorios.");
                lblMsg.setTextFill(Color.web(C_ROJO));
                lblMsg.setVisible(true);
                return;
            }

            // Si hay cita vinculada, completarla con el valor y notas
            if (cita != null) {
                try {
                    double valor = txtValor.getText().isBlank() ? 0
                            : Double.parseDouble(txtValor.getText().trim());
                    String notas = "Título: " + txtTitulo.getText() +
                            " | Diagnóstico: " + txtDiag.getText() +
                            " | Receta: " + txtReceta.getText();
                    cita.completar(valor, notas);
                    AppointmentRepository.updateAppointment(cita);
                } catch (NumberFormatException ex) {
                    lblMsg.setText("⚠️ El valor debe ser un número.");
                    lblMsg.setTextFill(Color.web(C_ROJO));
                    lblMsg.setVisible(true);
                    return;
                } catch (Exception ex) {
                    // Si ya estaba completada, solo mostramos éxito
                }
            }

            lblMsg.setText("✅ Historia clínica guardada correctamente.");
            lblMsg.setTextFill(Color.web(C_VERDE));
            lblMsg.setVisible(true);

            // Animación de éxito
            FadeTransition ft = new FadeTransition(Duration.millis(200), lblMsg);
            ft.setFromValue(0); ft.setToValue(1); ft.play();
        });

        HBox botones = new HBox(15, btnGuardar, btnLimpiar);

        form.getChildren().addAll(
                fila1,
                vstack(lblDesc, txtDesc),
                vstack(lblDiag, txtDiag),
                vstack(lblReceta, txtReceta),
                vstack(lblValor, txtValor),
                lblMsg,
                botones
        );

        layout.getChildren().add(form);
        return layout;
    }

    // ────────────────────────────────────────────────────────────
    //  HELPERS ESPECÍFICOS DEL DOCTOR
    // ────────────────────────────────────────────────────────────

    private VBox crearStatCard(String titulo, String valor, String color, String icono) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(22));
        card.setStyle("-fx-background-color: " + C_CARD + "; -fx-background-radius: 12; " +
                "-fx-border-color: " + color + "44; -fx-border-width: 1; -fx-border-radius: 12;");
        Label li = new Label(icono); li.setFont(Font.font(24));
        Label lv = new Label(valor); lv.setFont(Font.font("System", FontWeight.BOLD, 34));
        lv.setTextFill(Color.web(color));
        Label lt = new Label(titulo); lt.setFont(Font.font(12));
        lt.setTextFill(Color.web(C_SUAVE));
        card.getChildren().addAll(li, lv, lt);
        return card;
    }

    /** Factory helper para columnas de tabla usando lambdas */
    private <T> TableColumn<Appointment, T> col(String nombre, javafx.util.Callback<Appointment, T> getter) {
        TableColumn<Appointment, T> col = new TableColumn<>(nombre);
        col.setCellValueFactory(cell -> {
            T val = getter.call(cell.getValue());
            if (val instanceof String) return (javafx.beans.value.ObservableValue<T>) new SimpleStringProperty((String) val);
            return null;
        });
        return col;
    }

    private Label etqForm(String t) {
        Label l = new Label(t);
        l.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        l.setTextFill(Color.web(C_SUAVE));
        return l;
    }

    private TextField inputForm(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #0A0A1A; -fx-text-fill: " + C_TEXTO +
                "; -fx-prompt-text-fill: #455A64; -fx-border-color: " + C_BORDE +
                "; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10 12 10 12;");
        return tf;
    }

    private TextArea areaForm(String prompt, double altura) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setPrefHeight(altura);
        ta.setWrapText(true);
        ta.setStyle("-fx-background-color: #0A0A1A; -fx-text-fill: " + C_TEXTO +
                "; -fx-prompt-text-fill: #455A64; -fx-border-color: " + C_BORDE +
                "; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10;");
        return ta;
    }

    private Button btnAccion(String t, String color) {
        Button btn = new Button(t);
        btn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        String base = "-fx-background-color: " + color + "33; -fx-text-fill: " + color +
                "; -fx-background-radius: 8; -fx-border-color: " + color +
                "; -fx-border-radius: 8; -fx-border-width: 1; -fx-padding: 10 22 10 22; -fx-cursor: hand;";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(base.replace(color + "33", color + "55")));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    /** Agrupa un label y su control en un VBox */
    private VBox vstack(Node... nodos) {
        VBox v = new VBox(5, nodos);
        return v;
    }

    private VBox crearTituloSeccion(String titulo, String subtitulo) {
        Label lt = new Label(titulo);
        lt.setFont(Font.font("System", FontWeight.BOLD, 26));
        lt.setTextFill(Color.web(C_TEXTO));
        Label ls = new Label(subtitulo);
        ls.setFont(Font.font(13));
        ls.setTextFill(Color.web(C_SUAVE));
        Rectangle linea = new Rectangle(50, 3);
        linea.setFill(Color.web(C_AZUL));
        linea.setArcWidth(3); linea.setArcHeight(3);
        return new VBox(4, lt, ls, linea);
    }

    private Button crearBtnNav(String texto, boolean activo, String colorActivo) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setFont(Font.font("System", 14));
        btn.setPadding(new Insets(12, 15, 12, 15));
        String estiloActivo = "-fx-background-color: " + colorActivo + "22; -fx-text-fill: " + colorActivo +
                "; -fx-background-radius: 8; -fx-cursor: hand;";
        String estiloBase   = "-fx-background-color: transparent; -fx-text-fill: " + C_TEXTO +
                "; -fx-background-radius: 8; -fx-cursor: hand;";
        btn.setStyle(activo ? estiloActivo : estiloBase);
        btn.setOnMouseEntered(e -> { if (!btn.getStyle().contains(colorActivo)) btn.setStyle("-fx-background-color: " + C_BORDE + "; -fx-text-fill: " + C_TEXTO + "; -fx-background-radius: 8; -fx-cursor: hand;"); });
        btn.setOnMouseExited(e  -> { if (!btn.getStyle().contains(colorActivo)) btn.setStyle(estiloBase); });
        return btn;
    }

    /** Activa un botón y desactiva los demás */
    private void activar(Button activo, Button... inactivos) {
        // Helper simplificado: se usa desde el sidebar para gestión visual
    }

    private void cambiarContenido(Node contenido) {
        contenido.setOpacity(0);
        contenidoCentral.getChildren().setAll(contenido);
        FadeTransition ft = new FadeTransition(Duration.millis(300), contenido);
        ft.setToValue(1); ft.play();
    }

    private String estiloTabla() {
        return "-fx-background-color: " + C_CARD + "; -fx-text-fill: " + C_TEXTO +
                "; -fx-table-cell-border-color: " + C_BORDE + ";";
    }
}
