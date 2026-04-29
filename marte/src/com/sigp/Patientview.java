package marte.src.com.sigp;


import marte.src.com.sigp.model.Appointment;
import marte.src.com.sigp.model.Doctor;
import marte.src.com.sigp.model.Patient;
import marte.src.com.sigp.model.User;
import marte.src.com.sigp.repository.AppointmentRepository;
import marte.src.com.sigp.repository.DoctorRepository;
import marte.src.com.sigp.repository.PatientRepository;
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
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Patientview {

    // ── Paleta del tema ───────────────────────────────────────
    private static final String C_FONDO  = "#0D0D1A";
    private static final String C_PANEL  = "#141428";
    private static final String C_CARD   = "#1A1A30";
    private static final String C_VERDE  = "#27AE60";
    private static final String C_AZUL   = "#2980B9";
    private static final String C_AMBAR  = "#E67E22";
    private static final String C_ROJO   = "#C0392B";
    private static final String C_TEXTO  = "#ECEFF1";
    private static final String C_SUAVE  = "#90A4AE";
    private static final String C_BORDE  = "#1E1E3A";

    private final Stage  stage;
    private final User   userPaciente;
    private StackPane    contenidoCentral;
    private Patient      paciente;

    private final DoctorRepository doctorRepo = new DoctorRepository();
    private final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Patientview(Stage stage, User userPaciente) {
        this.stage        = stage;
        this.userPaciente = userPaciente;
    }

    public void mostrar() {
        // Buscar el perfil de paciente asociado al email
        this.paciente = PatientRepository.findByEmail(userPaciente.username());

        // Si no tiene perfil aún, pedimos que lo complete
        if (paciente == null) {
            mostrarRegistroPaciente();
        } else {
            construirUI();
        }
    }

    // ────────────────────────────────────────────────────────────
    //  FORMULARIO DE REGISTRO (si es la primera vez)
    // ────────────────────────────────────────────────────────────
    private void mostrarRegistroPaciente() {
        VBox form = new VBox(15);
        form.setPadding(new Insets(50));
        form.setMaxWidth(500);
        form.setStyle("-fx-background-color: " + C_PANEL + "; -fx-background-radius: 16;");

        Label titulo = new Label("Completa tu perfil de paciente");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 20));
        titulo.setTextFill(Color.web(C_TEXTO));

        Label sub = new Label("Este paso solo ocurre una vez.");
        sub.setTextFill(Color.web(C_SUAVE));

        TextField txtNombre = campo("Nombre completo");
        TextField txtNac    = campo("Nacionalidad");
        TextField txtTel    = campo("Celular (ej: 3001234567)");
        TextField txtEdad   = campo("Edad");
        TextField txtCedula = campo("Número de cédula");

        Label lblError = new Label();
        lblError.setTextFill(Color.web(C_ROJO));
        lblError.setFont(Font.font(12));
        lblError.setVisible(false);

        Button btnRegistrar = botonPrimario("✅ Registrarme", C_VERDE);
        btnRegistrar.setMaxWidth(Double.MAX_VALUE);

        btnRegistrar.setOnAction(e -> {
            try {
                int edad = Integer.parseInt(txtEdad.getText().trim());
                Patient p = new Patient(
                        txtNombre.getText().trim(),
                        txtNac.getText().trim(),
                        txtTel.getText().trim(),
                        userPaciente.username(),
                        edad,
                        txtCedula.getText().trim()
                );
                PatientRepository.addPatient(p);
                this.paciente = p;
                construirUI();
            } catch (NumberFormatException ex) {
                lblError.setText("La edad debe ser un número válido.");
                lblError.setVisible(true);
            } catch (IllegalArgumentException ex) {
                lblError.setText(ex.getMessage());
                lblError.setVisible(true);
            }
        });

        form.getChildren().addAll(titulo, sub, txtNombre, txtNac, txtTel, txtEdad, txtCedula, lblError, btnRegistrar);

        StackPane raiz = new StackPane(form);
        raiz.setStyle("-fx-background-color: " + C_FONDO + ";");
        StackPane.setAlignment(form, Pos.CENTER);

        Scene escena = new Scene(raiz, 700, 600);
        stage.setTitle("MARTE – Registro de Paciente");
        stage.setScene(escena);
        stage.show();
    }

    // ────────────────────────────────────────────────────────────
    //  UI PRINCIPAL DEL PACIENTE
    // ────────────────────────────────────────────────────────────
    private void construirUI() {
        HBox raiz = new HBox();
        raiz.setStyle("-fx-background-color: " + C_FONDO + ";");

        VBox sidebar = crearSidebar();
        sidebar.setPrefWidth(240);

        contenidoCentral = new StackPane();
        contenidoCentral.setStyle("-fx-background-color: " + C_FONDO + ";");
        HBox.setHgrow(contenidoCentral, Priority.ALWAYS);

        raiz.getChildren().addAll(sidebar, contenidoCentral);

        mostrarMisCitas(); // sección inicial

        Scene escena = new Scene(raiz, 1200, 750);
        stage.setTitle("MARTE – " + (paciente != null ? paciente.name() : "Paciente"));
        stage.setScene(escena);
        stage.show();
    }

    // ────────────────────────────────────────────────────────────
    //  BARRA LATERAL
    // ────────────────────────────────────────────────────────────
    private VBox crearSidebar() {
        VBox bar = new VBox(8);
        bar.setStyle("-fx-background-color: " + C_PANEL + "; -fx-border-color: " + C_BORDE + "; -fx-border-width: 0 1 0 0;");
        bar.setPadding(new Insets(30, 15, 30, 15));

        Label logo = new Label("♂ MARTE");
        logo.setFont(Font.font("System", FontWeight.BOLD, 26));
        logo.setTextFill(Color.web(C_VERDE));
        logo.setPadding(new Insets(0, 0, 4, 10));

        Label nombre = new Label(paciente != null ? paciente.name() : userPaciente.username());
        nombre.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        nombre.setTextFill(Color.web(C_TEXTO));
        nombre.setPadding(new Insets(0, 0, 2, 10));
        nombre.setWrapText(true);

        Label rolLabel = new Label("PACIENTE");
        rolLabel.setFont(Font.font(10));
        rolLabel.setTextFill(Color.web(C_SUAVE));
        rolLabel.setPadding(new Insets(0, 0, 20, 10));

        Separator sep = new Separator();

        Button btnCitas    = btnNav("📅  Mis Citas",        true,  C_VERDE);
        Button btnAgendar  = btnNav("➕  Agendar Cita",     false, C_VERDE);
        Button btnPagar    = btnNav("💳  Pagar Cita",       false, C_VERDE);

        Region esp = new Region();
        VBox.setVgrow(esp, Priority.ALWAYS);
        Button btnSalir = btnNav("🚪  Cerrar sesión", false, C_ROJO);

        btnCitas.setOnAction(e   -> { marcarActivo(btnCitas, btnAgendar, btnPagar); mostrarMisCitas(); });
        btnAgendar.setOnAction(e -> { marcarActivo(btnAgendar, btnCitas, btnPagar); mostrarAgendarCita(); });
        btnPagar.setOnAction(e   -> { marcarActivo(btnPagar, btnCitas, btnAgendar); mostrarPago(); });
        btnSalir.setOnAction(e   -> new LoginView(stage).mostrar());

        bar.getChildren().addAll(logo, nombre, rolLabel, sep, btnCitas, btnAgendar, btnPagar, esp, btnSalir);
        return bar;
    }

    // ────────────────────────────────────────────────────────────
    //  SECCIÓN 1: MIS CITAS
    // ────────────────────────────────────────────────────────────
    private void mostrarMisCitas() {
        String nombrePaciente = paciente != null ? paciente.name() : "";
        List<Appointment> misCitas = AppointmentRepository.getAllAppointments().stream()
                .filter(a -> a.getPatientName().equalsIgnoreCase(nombrePaciente))
                .sorted(Comparator.comparing(Appointment::getAppointmentDateTime))
                .collect(Collectors.toList());

        long pendientes  = misCitas.stream().filter(a -> "PENDIENTE".equals(a.getStatus())).count();
        long completadas = misCitas.stream().filter(a -> "COMPLETADA".equals(a.getStatus())).count();

        VBox layout = new VBox(22);
        layout.setPadding(new Insets(35));
        layout.setStyle("-fx-background-color: " + C_FONDO + ";");

        layout.getChildren().add(crearTituloSeccion("Mis Citas", "Historial de todas tus citas médicas"));

        // Estadísticas rápidas
        HBox stats = new HBox(20);
        stats.getChildren().addAll(
                statCard("Total Citas",   String.valueOf(misCitas.size()), C_AZUL,  "📋"),
                statCard("Pendientes",    String.valueOf(pendientes),      C_AMBAR, "⏳"),
                statCard("Completadas",   String.valueOf(completadas),     C_VERDE, "✅")
        );
        stats.getChildren().forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));

        // Tabla de citas
        TableView<Appointment> tabla = new TableView<>();
        tabla.setStyle(estiloTabla());
        tabla.setPlaceholder(new Label("No tienes citas registradas."));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        TableColumn<Appointment, String> cId     = col("ID",         a -> String.valueOf(a.getId()));
        TableColumn<Appointment, String> cDoctor = col("Doctor",     a -> a.getDoctorName());
        TableColumn<Appointment, String> cEspec  = col("Especialidad", a -> a.getDoctorSpecialty());
        TableColumn<Appointment, String> cFecha  = col("Fecha",      a -> a.getAppointmentDateTime().format(FMT));
        TableColumn<Appointment, String> cEstado = col("Estado",     a -> a.getStatus());
        TableColumn<Appointment, String> cCosto  = col("Costo",      a -> a.getCost() > 0 ? "$" + String.format("%.0f", a.getCost()) : "–");

        cId.setPrefWidth(60);     cDoctor.setPrefWidth(180);
        cEspec.setPrefWidth(150); cFecha.setPrefWidth(160);
        cEstado.setPrefWidth(120); cCosto.setPrefWidth(100);

        // Color de fila según estado
        tabla.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(Appointment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setStyle(""); return; }
                switch (item.getStatus()) {
                    case "COMPLETADA" -> setStyle("-fx-background-color: #1A2B1A;");
                    case "CANCELADA"  -> setStyle("-fx-background-color: #2B1A1A;");
                    default           -> setStyle("");
                }
            }
        });

        tabla.getColumns().addAll(cId, cDoctor, cEspec, cFecha, cEstado, cCosto);
        tabla.setItems(FXCollections.observableArrayList(misCitas));

        layout.getChildren().addAll(stats, tabla);

        ScrollPane scroll = envolver(layout);
        cambiarContenido(scroll);
    }

    // ────────────────────────────────────────────────────────────
    //  SECCIÓN 2: AGENDAR CITA
    // ────────────────────────────────────────────────────────────
    private void mostrarAgendarCita() {
        List<Doctor> doctores = doctorRepo.obtenerDoctores();

        VBox layout = new VBox(22);
        layout.setPadding(new Insets(35));
        layout.setStyle("-fx-background-color: " + C_FONDO + ";");
        layout.setMaxWidth(750);

        layout.getChildren().add(crearTituloSeccion("Agendar Cita", "Reserva una cita con un médico especialista"));

        // ── Formulario de agendamiento ────────────────────────
        VBox form = new VBox(16);
        form.setPadding(new Insets(30));
        form.setStyle("-fx-background-color: " + C_CARD + "; -fx-background-radius: 14; " +
                "-fx-border-color: " + C_BORDE + "; -fx-border-width: 1; -fx-border-radius: 14;");

        // Selector de doctor
        Label lblDoctor = etq("Selecciona el Doctor");
        ComboBox<Doctor> comboDoctor = new ComboBox<>();
        comboDoctor.setMaxWidth(Double.MAX_VALUE);
        comboDoctor.getItems().addAll(doctores);
        comboDoctor.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Doctor d)    { return d == null ? "" : "Dr. " + d.getNombreCompleto() + " – " + d.getEspecialidad(); }
            @Override public Doctor fromString(String s) { return null; }
        });
        comboDoctor.setStyle("-fx-background-color: #0A0A1A; -fx-text-fill: " + C_TEXTO +
                "; -fx-border-color: " + C_BORDE + "; -fx-border-radius: 6; -fx-background-radius: 6;");
        if (!doctores.isEmpty()) comboDoctor.getSelectionModel().selectFirst();

        // Campos de fecha y hora
        Label lblFecha = etq("Fecha de la cita");
        DatePicker datePicker = new DatePicker(LocalDate.now().plusDays(1));
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setStyle("-fx-background-color: #0A0A1A; -fx-text-fill: " + C_TEXTO +
                "; -fx-border-color: " + C_BORDE + "; -fx-border-radius: 6; -fx-background-radius: 6;");

        Label lblHora = etq("Hora (formato HH:mm, ej: 09:30)");
        TextField txtHora = campo("09:00");

        // Mensaje de resultado
        Label lblMsg = new Label();
        lblMsg.setFont(Font.font(13));
        lblMsg.setWrapText(true);
        lblMsg.setVisible(false);

        Button btnAgendar = botonPrimario("📅  Confirmar Cita", C_VERDE);
        btnAgendar.setMaxWidth(Double.MAX_VALUE);

        btnAgendar.setOnAction(e -> {
            Doctor docSeleccionado = comboDoctor.getValue();
            LocalDate fecha        = datePicker.getValue();
            String   horaStr       = txtHora.getText().trim();

            if (docSeleccionado == null || fecha == null || horaStr.isBlank()) {
                mostrarMsg(lblMsg, "⚠️ Completa todos los campos.", C_AMBAR);
                return;
            }

            // Parsear hora
            LocalTime hora;
            try {
                hora = LocalTime.parse(horaStr, DateTimeFormatter.ofPattern("HH:mm"));
            } catch (Exception ex) {
                mostrarMsg(lblMsg, "⚠️ Formato de hora inválido. Usa HH:mm (ej: 09:30).", C_ROJO);
                return;
            }

            LocalDateTime fechaHora = LocalDateTime.of(fecha, hora);

            // Validar que no sea en el pasado
            if (fechaHora.isBefore(LocalDateTime.now())) {
                mostrarMsg(lblMsg, "⚠️ No puedes agendar citas en el pasado.", C_ROJO);
                return;
            }

            // ── Regla clave: no dos citas con el mismo doctor en la misma hora ──
            boolean conflicto = AppointmentRepository.getAllAppointments().stream()
                    .anyMatch(a ->
                            a.getDoctorId() == docSeleccionado.getId() &&
                                    a.getAppointmentDateTime().equals(fechaHora) &&
                                    !"CANCELADA".equals(a.getStatus())
                    );

            if (conflicto) {
                mostrarMsg(lblMsg, "❌ El Dr. " + docSeleccionado.getNombreCompleto() +
                        " ya tiene una cita a esa hora. Por favor elige otro horario.", C_ROJO);
                return;
            }

            // Crear y guardar la cita
            String nombreP  = paciente != null ? paciente.name() : userPaciente.username();
            String cedulaP  = paciente != null ? paciente.id()   : "";

            Appointment nueva = new Appointment(
                    nombreP, cedulaP,
                    docSeleccionado.getId(),
                    docSeleccionado.getNombreCompleto(),
                    docSeleccionado.getEspecialidad(),
                    fechaHora
            );
            AppointmentRepository.addAppointment(nueva);

            mostrarMsg(lblMsg, "✅ Cita agendada exitosamente para el " +
                    fechaHora.format(FMT) + " con el Dr. " + docSeleccionado.getNombreCompleto() + ".", C_VERDE);

            // Limpiar hora para la próxima cita
            txtHora.clear();
        });

        form.getChildren().addAll(
                vstack(lblDoctor, comboDoctor),
                vstack(lblFecha, datePicker),
                vstack(lblHora, txtHora),
                lblMsg,
                btnAgendar
        );

        layout.getChildren().add(form);
        cambiarContenido(envolver(layout));
    }

    // ────────────────────────────────────────────────────────────
    //  SECCIÓN 3: PAGAR CITA
    // ────────────────────────────────────────────────────────────
    private void mostrarPago() {
        String nombrePaciente = paciente != null ? paciente.name() : "";

        // Solo citas completadas y con costo > 0 se pueden pagar
        List<Appointment> porPagar = AppointmentRepository.getAllAppointments().stream()
                .filter(a -> a.getPatientName().equalsIgnoreCase(nombrePaciente))
                .filter(a -> "COMPLETADA".equals(a.getStatus()) && a.getCost() > 0)
                .collect(Collectors.toList());

        VBox layout = new VBox(22);
        layout.setPadding(new Insets(35));
        layout.setStyle("-fx-background-color: " + C_FONDO + ";");

        layout.getChildren().add(crearTituloSeccion("Pagar Cita", "Citas completadas pendientes de pago"));

        if (porPagar.isEmpty()) {
            Label vacio = new Label("✅ No tienes citas pendientes de pago.");
            vacio.setFont(Font.font(15));
            vacio.setTextFill(Color.web(C_SUAVE));
            layout.getChildren().add(vacio);
        } else {
            for (Appointment cita : porPagar) {
                layout.getChildren().add(crearTarjetaPago(cita));
            }
        }

        cambiarContenido(envolver(layout));
    }

    /** Tarjeta de pago para una cita */
    private VBox crearTarjetaPago(Appointment cita) {
        VBox card = new VBox(12);
        card.setPadding(new Insets(25));
        card.setMaxWidth(650);
        card.setStyle("-fx-background-color: " + C_CARD + "; -fx-background-radius: 14; " +
                "-fx-border-color: " + C_AMBAR + "44; -fx-border-width: 1; -fx-border-radius: 14;");

        Label lDoctor = new Label("👨‍⚕️ Dr. " + cita.getDoctorName() + " – " + cita.getDoctorSpecialty());
        lDoctor.setFont(Font.font("System", FontWeight.SEMI_BOLD, 15));
        lDoctor.setTextFill(Color.web(C_TEXTO));

        Label lFecha = new Label("📅 " + cita.getAppointmentDateTime().format(FMT));
        lFecha.setTextFill(Color.web(C_SUAVE));

        Label lCosto = new Label("💵 Valor a pagar: $" + String.format("%.0f", cita.getCost()));
        lCosto.setFont(Font.font("System", FontWeight.BOLD, 22));
        lCosto.setTextFill(Color.web(C_AMBAR));

        // Selector de método de pago
        Label lblMetodo = etq("Método de pago");
        ComboBox<String> comboMetodo = new ComboBox<>();
        comboMetodo.getItems().addAll("💳 Tarjeta de crédito", "🏦 Transferencia bancaria", "💵 Efectivo");
        comboMetodo.getSelectionModel().selectFirst();
        comboMetodo.setStyle("-fx-background-color: #0A0A1A; -fx-text-fill: " + C_TEXTO +
                "; -fx-border-color: " + C_BORDE + "; -fx-border-radius: 6; -fx-background-radius: 6;");

        Label lblConfirm = new Label();
        lblConfirm.setFont(Font.font(13));
        lblConfirm.setVisible(false);

        Button btnPagar = botonPrimario("💳  Pagar ahora", C_AMBAR);

        btnPagar.setOnAction(e -> {
            // En un sistema real aquí iría la integración de pasarela de pago
            // Por ahora, simulamos el pago exitoso
            mostrarMsg(lblConfirm,
                    "✅ Pago de $" + String.format("%.0f", cita.getCost()) + " procesado exitosamente. " +
                            "Método: " + comboMetodo.getValue(), C_VERDE);
            btnPagar.setDisable(true);
        });

        card.getChildren().addAll(lDoctor, lFecha, lCosto, vstack(lblMetodo, comboMetodo), lblConfirm, btnPagar);
        return card;
    }

    // ────────────────────────────────────────────────────────────
    //  HELPERS
    // ────────────────────────────────────────────────────────────

    private VBox statCard(String titulo, String valor, String color, String icono) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: " + C_CARD + "; -fx-background-radius: 12; " +
                "-fx-border-color: " + color + "44; -fx-border-width: 1; -fx-border-radius: 12;");
        Label li = new Label(icono); li.setFont(Font.font(22));
        Label lv = new Label(valor); lv.setFont(Font.font("System", FontWeight.BOLD, 30));
        lv.setTextFill(Color.web(color));
        Label lt = new Label(titulo); lt.setFont(Font.font(12));
        lt.setTextFill(Color.web(C_SUAVE));
        card.getChildren().addAll(li, lv, lt);
        return card;
    }

    private TableColumn<Appointment, String> col(String nombre, java.util.function.Function<Appointment, String> fn) {
        TableColumn<Appointment, String> col = new TableColumn<>(nombre);
        col.setCellValueFactory(cell -> new SimpleStringProperty(fn.apply(cell.getValue())));
        return col;
    }

    private Label etq(String t) {
        Label l = new Label(t);
        l.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        l.setTextFill(Color.web(C_SUAVE));
        return l;
    }

    private TextField campo(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-background-color: #0A0A1A; -fx-text-fill: " + C_TEXTO +
                "; -fx-prompt-text-fill: #455A64; -fx-border-color: " + C_BORDE +
                "; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10 12 10 12;");
        return tf;
    }

    private Button botonPrimario(String texto, String color) {
        Button btn = new Button(texto);
        btn.setFont(Font.font("System", FontWeight.BOLD, 13));
        String base = "-fx-background-color: " + color + "; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 12 25 12 25; -fx-cursor: hand;";
        btn.setStyle(base);
        return btn;
    }

    private Button btnNav(String texto, boolean activo, String colorActivo) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setFont(Font.font("System", 14));
        btn.setPadding(new Insets(12, 15, 12, 15));
        String ea = "-fx-background-color: " + colorActivo + "22; -fx-text-fill: " + colorActivo + "; -fx-background-radius: 8; -fx-cursor: hand;";
        String ei = "-fx-background-color: transparent; -fx-text-fill: " + C_TEXTO + "; -fx-background-radius: 8; -fx-cursor: hand;";
        btn.setStyle(activo ? ea : ei);
        btn.setOnMouseEntered(e -> { if (!btn.getStyle().contains(colorActivo)) btn.setStyle("-fx-background-color: " + C_BORDE + "; -fx-text-fill: " + C_TEXTO + "; -fx-background-radius: 8; -fx-cursor: hand;"); });
        btn.setOnMouseExited(e  -> { if (!btn.getStyle().contains(colorActivo)) btn.setStyle(ei); });
        return btn;
    }

    private void marcarActivo(Button activo, Button... inactivos) {
        // Se usan estilos inline; simplificado para claridad
    }

    private VBox vstack(Node... nodes) { return new VBox(5, nodes); }

    private VBox crearTituloSeccion(String titulo, String subtitulo) {
        Label lt = new Label(titulo);
        lt.setFont(Font.font("System", FontWeight.BOLD, 26));
        lt.setTextFill(Color.web(C_TEXTO));
        Label ls = new Label(subtitulo);
        ls.setFont(Font.font(13));
        ls.setTextFill(Color.web(C_SUAVE));
        Rectangle linea = new Rectangle(50, 3);
        linea.setFill(Color.web(C_VERDE));
        linea.setArcWidth(3); linea.setArcHeight(3);
        return new VBox(4, lt, ls, linea);
    }

    private void mostrarMsg(Label lbl, String msg, String color) {
        lbl.setText(msg);
        lbl.setTextFill(Color.web(color));
        lbl.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(300), lbl);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    private ScrollPane envolver(VBox layout) {
        ScrollPane sp = new ScrollPane(layout);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: " + C_FONDO + "; -fx-background-color: " + C_FONDO + ";");
        return sp;
    }

    private void cambiarContenido(Node contenido) {
        contenido.setOpacity(0);
        contenidoCentral.getChildren().setAll(contenido);
        FadeTransition ft = new FadeTransition(Duration.millis(300), contenido);
        ft.setToValue(1); ft.play();
    }

    private String estiloTabla() {
        return "-fx-background-color: " + C_CARD + "; -fx-text-fill: " + C_TEXTO + "; -fx-table-cell-border-color: " + C_BORDE + ";";
    }
}
