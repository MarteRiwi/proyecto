package marte.src.com.sigp;

import marte.src.com.sigp.exception.CustomException;
import marte.src.com.sigp.model.Doctor;
import marte.src.com.sigp.model.Patient;
import marte.src.com.sigp.model.User;
import marte.src.com.sigp.repository.PatientRepository;
import marte.src.com.sigp.service.DoctorService;
import javafx.animation.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.List;

public class AdminDashboardView {

    // ── Paleta de colores ─────────────────────────────────────
    private static final String C_FONDO    = "#0D0D1A";
    private static final String C_PANEL    = "#141428";
    private static final String C_CARD     = "#1A1A30";
    private static final String C_ACENTO   = "#C0392B";
    private static final String C_AZUL     = "#2980B9";
    private static final String C_VERDE    = "#27AE60";
    private static final String C_TEXTO    = "#ECEFF1";
    private static final String C_SUAVE    = "#90A4AE";
    private static final String C_BORDE    = "#1E1E3A";

    private final Stage stage;
    private final User adminUser;
    private final DoctorService doctorService = new DoctorService();

    // Panel de contenido central que cambia según la sección activa
    private StackPane contenidoCentral;

    public AdminDashboardView(Stage stage, User adminUser) {
        this.stage     = stage;
        this.adminUser = adminUser;
    }

    public void mostrar() {
        // Layout principal: barra lateral izquierda + contenido derecho
        HBox raiz = new HBox();
        raiz.setStyle("-fx-background-color: " + C_FONDO + ";");

        // Barra lateral de navegación
        VBox sideBar = crearSideBar();
        sideBar.setPrefWidth(240);

        // Área de contenido (ocupa el resto)
        contenidoCentral = new StackPane();
        contenidoCentral.setStyle("-fx-background-color: " + C_FONDO + ";");
        HBox.setHgrow(contenidoCentral, Priority.ALWAYS);

        raiz.getChildren().addAll(sideBar, contenidoCentral);

        // Mostrar el dashboard como sección inicial
        mostrarDashboard();

        Scene escena = new Scene(raiz, 1200, 750);
        stage.setTitle("MARTE – Panel de Administración");
        stage.setScene(escena);
        stage.setResizable(true);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    // ────────────────────────────────────────────────────────────
    //  BARRA LATERAL
    // ────────────────────────────────────────────────────────────
    private VBox crearSideBar() {
        VBox bar = new VBox(8);
        bar.setStyle(
                "-fx-background-color: " + C_PANEL + ";" +
                        "-fx-border-color: " + C_BORDE + ";" +
                        "-fx-border-width: 0 1 0 0;"
        );
        bar.setPadding(new Insets(30, 15, 30, 15));

        // Logo
        Label logo = new Label("♂ MARTE");
        logo.setFont(Font.font("System", FontWeight.BOLD, 26));
        logo.setTextFill(Color.web(C_ACENTO));
        logo.setPadding(new Insets(0, 0, 10, 10));

        Label rolLabel = new Label("ADMINISTRADOR");
        rolLabel.setFont(Font.font("System", 10));
        rolLabel.setTextFill(Color.web(C_SUAVE));
        rolLabel.setPadding(new Insets(0, 0, 20, 10));

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + C_BORDE + ";");

        // Botones de navegación
        Button btnDashboard = crearBotonNav("📊  Dashboard",    true);
        Button btnDoctores  = crearBotonNav("👨‍⚕️  Doctores",     false);
        Button btnPacientes = crearBotonNav("🧑‍🤝‍🧑  Pacientes",    false);

        // Espaciador empuja el botón de salir hacia abajo
        Region espaciador = new Region();
        VBox.setVgrow(espaciador, Priority.ALWAYS);

        Button btnSalir = crearBotonNav("🚪  Cerrar sesión", false);
        btnSalir.setStyle(btnSalir.getStyle() + "-fx-text-fill: " + C_ACENTO + ";");

        // ── Acciones de navegación ────────────────────────────
        btnDashboard.setOnAction(e -> {
            activarBoton(btnDashboard, btnDoctores, btnPacientes);
            mostrarDashboard();
        });
        btnDoctores.setOnAction(e -> {
            activarBoton(btnDoctores, btnDashboard, btnPacientes);
            mostrarGestionDoctores();
        });
        btnPacientes.setOnAction(e -> {
            activarBoton(btnPacientes, btnDashboard, btnDoctores);
            mostrarGestionPacientes();
        });
        btnSalir.setOnAction(e -> new LoginView(stage).mostrar());

        bar.getChildren().addAll(
                logo, rolLabel, sep,
                btnDashboard, btnDoctores, btnPacientes,
                espaciador, btnSalir
        );
        return bar;
    }

    /** Crea un botón de navegación lateral con estilo */
    private Button crearBotonNav(String texto, boolean activo) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setFont(Font.font("System", 14));
        btn.setPadding(new Insets(12, 15, 12, 15));
        btn.setStyle(estiloBtnNav(activo));
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().contains(C_ACENTO))
                btn.setStyle(estiloBtnNavHover());
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().contains(C_ACENTO))
                btn.setStyle(estiloBtnNav(false));
        });
        return btn;
    }

    private String estiloBtnNav(boolean activo) {
        if (activo) return
                "-fx-background-color: " + C_ACENTO + "22;" +
                        "-fx-text-fill: " + C_ACENTO + ";" +
                        "-fx-background-radius: 8;" +
                        "-fx-cursor: hand;";
        return "-fx-background-color: transparent;" +
                "-fx-text-fill: " + C_TEXTO + ";" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";
    }

    private String estiloBtnNavHover() {
        return "-fx-background-color: " + C_BORDE + ";" +
                "-fx-text-fill: " + C_TEXTO + ";" +
                "-fx-background-radius: 8;" +
                "-fx-cursor: hand;";
    }

    /** Marca un botón como activo y los demás como inactivos */
    private void activarBoton(Button activo, Button... inactivos) {
        activo.setStyle(estiloBtnNav(true));
        for (Button b : inactivos) b.setStyle(estiloBtnNav(false));
    }

    // ────────────────────────────────────────────────────────────
    //  SECCIÓN 1: DASHBOARD
    // ────────────────────────────────────────────────────────────
    private void mostrarDashboard() {
        ScrollPane scroll = new ScrollPane(construirDashboard());
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        cambiarContenido(scroll);
    }

    private VBox construirDashboard() {
        VBox layout = new VBox(25);
        layout.setPadding(new Insets(35));
        layout.setStyle("-fx-background-color: " + C_FONDO + ";");

        // Título de sección
        layout.getChildren().add(crearTituloSeccion("Dashboard", "Resumen general del sistema"));

        // ── Tarjetas de estadísticas rápidas ─────────────────
        List<Doctor> doctores = doctorService.obtenerDoctores();
        List<Patient> pacientes = PatientRepository.getPatientList();

        int totalDoctores  = doctores.size();
        int totalPacientes = pacientes.size();

        HBox tarjetas = new HBox(20);
        tarjetas.getChildren().addAll(
                crearTarjetaStat("Total Doctores",   String.valueOf(totalDoctores),  C_AZUL,  "👨‍⚕️"),
                crearTarjetaStat("Total Pacientes",  String.valueOf(totalPacientes), C_VERDE, "🧑‍🤝‍🧑"),
                crearTarjetaStat("Consultas Hoy",    "–",                            C_ACENTO,"📅")
        );
        // Las tarjetas se expanden por igual
        for (Node n : tarjetas.getChildren())
            HBox.setHgrow(n, Priority.ALWAYS);

        // ── Gráfica de doctores: disponible / ocupado ─────────
        PieChart graficaDoctores = new PieChart();
        graficaDoctores.setTitle("Estado de Doctores");
        graficaDoctores.getData().addAll(
                new PieChart.Data("Disponibles (" + totalDoctores + ")", Math.max(totalDoctores, 1)),
                new PieChart.Data("Ocupados (0)", 1)
        );
        graficaDoctores.setLegendVisible(true);
        graficaDoctores.setPrefHeight(320);
        aplicarEstiloChart(graficaDoctores);

        // ── Gráfica de barras: pacientes por mes (datos demo) ─
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        xAxis.setLabel("Mes");
        yAxis.setLabel("Pacientes");
        BarChart<String, Number> graficaPacientes = new BarChart<>(xAxis, yAxis);
        graficaPacientes.setTitle("Pacientes Atendidos por Mes");
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName("2025");
        String[] meses = {"Ene","Feb","Mar","Abr","May","Jun"};
        int[]    vals  = {12, 19, 8, 24, 15, totalPacientes};
        for (int i = 0; i < meses.length; i++)
            serie.getData().add(new XYChart.Data<>(meses[i], vals[i]));
        graficaPacientes.getData().add(serie);
        graficaPacientes.setPrefHeight(320);
        aplicarEstiloChart(graficaPacientes);

        HBox graficas = new HBox(20, graficaDoctores, graficaPacientes);
        HBox.setHgrow(graficaDoctores,  Priority.ALWAYS);
        HBox.setHgrow(graficaPacientes, Priority.ALWAYS);

        layout.getChildren().addAll(tarjetas, graficas);
        return layout;
    }

    /** Tarjeta de estadística rápida (número grande + ícono) */
    private VBox crearTarjetaStat(String titulo, String valor, String color, String icono) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(25));
        card.setStyle(
                "-fx-background-color: " + C_CARD + ";" +
                        "-fx-background-radius: 14;" +
                        "-fx-border-color: " + color + "44;" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 14;"
        );

        Label lIcono = new Label(icono);
        lIcono.setFont(Font.font(28));

        Label lValor = new Label(valor);
        lValor.setFont(Font.font("System", FontWeight.BOLD, 38));
        lValor.setTextFill(Color.web(color));

        Label lTitulo = new Label(titulo);
        lTitulo.setFont(Font.font(13));
        lTitulo.setTextFill(Color.web(C_SUAVE));

        card.getChildren().addAll(lIcono, lValor, lTitulo);
        return card;
    }

    // ────────────────────────────────────────────────────────────
    //  SECCIÓN 2: GESTIÓN DE DOCTORES
    // ────────────────────────────────────────────────────────────
    private void mostrarGestionDoctores() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(35));
        layout.setStyle("-fx-background-color: " + C_FONDO + ";");

        layout.getChildren().add(crearTituloSeccion("Gestión de Doctores", "Crear, editar y eliminar médicos"));

        // ── Barra de herramientas ─────────────────────────────
        Button btnNuevo = crearBotonAccion("➕  Nuevo Doctor", C_VERDE);
        HBox toolbar = new HBox(10, btnNuevo);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // ── Tabla de doctores ─────────────────────────────────
        TableView<Doctor> tabla = new TableView<>();
        tabla.setStyle(estiloTabla());
        tabla.setPlaceholder(new Label("No hay doctores registrados."));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        TableColumn<Doctor, Integer> colId     = new TableColumn<>("ID");
        TableColumn<Doctor, String>  colNombre = new TableColumn<>("Nombre");
        TableColumn<Doctor, Integer> colEdad   = new TableColumn<>("Edad");
        TableColumn<Doctor, String>  colCedula = new TableColumn<>("Cédula");
        TableColumn<Doctor, String>  colEspec  = new TableColumn<>("Especialidad");
        TableColumn<Doctor, Void>    colAccion = new TableColumn<>("Acciones");

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colEspec.setCellValueFactory(new PropertyValueFactory<>("especialidad"));

        colId.setPrefWidth(60);
        colNombre.setPrefWidth(200);
        colEdad.setPrefWidth(70);
        colCedula.setPrefWidth(130);
        colEspec.setPrefWidth(160);
        colAccion.setPrefWidth(150);

        // Columna de acciones: botones Editar y Eliminar por fila
        colAccion.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar   = crearBotonAccion("✏️ Editar",   C_AZUL);
            private final Button btnEliminar = crearBotonAccion("🗑️ Eliminar", C_ACENTO);
            private final HBox   botones     = new HBox(6, btnEditar, btnEliminar);

            {
                btnEditar.setFont(Font.font(11));
                btnEliminar.setFont(Font.font(11));
                btnEditar.setPadding(new Insets(5, 10, 5, 10));
                btnEliminar.setPadding(new Insets(5, 10, 5, 10));

                btnEditar.setOnAction(e -> {
                    Doctor d = getTableView().getItems().get(getIndex());
                    abrirFormularioDoctor(d, tabla.getItems());
                });
                btnEliminar.setOnAction(e -> {
                    Doctor d = getTableView().getItems().get(getIndex());
                    confirmarEliminacion(d, tabla.getItems());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : botones);
            }
        });

        tabla.getColumns().addAll(colId, colNombre, colEdad, colCedula, colEspec, colAccion);

        // Cargar datos al ObservableList (lista reactiva: la tabla se actualiza sola)
        ObservableList<Doctor> datos = FXCollections.observableArrayList(doctorService.obtenerDoctores());
        tabla.setItems(datos);

        // Acción del botón nuevo doctor
        btnNuevo.setOnAction(e -> abrirFormularioDoctor(null, datos));

        layout.getChildren().addAll(toolbar, tabla);

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        cambiarContenido(scroll);
    }

    /** Abre un diálogo modal para crear o editar un doctor */
    private void abrirFormularioDoctor(Doctor doctorExistente, ObservableList<Doctor> lista) {
        Stage modal = new Stage();
        modal.initModality(Modality.APPLICATION_MODAL);
        modal.initOwner(stage);
        modal.setTitle(doctorExistente == null ? "Nuevo Doctor" : "Editar Doctor");
        modal.setResizable(false);

        boolean esEdicion = (doctorExistente != null);

        VBox form = new VBox(15);
        form.setPadding(new Insets(30));
        form.setStyle("-fx-background-color: " + C_PANEL + ";");
        form.setPrefWidth(420);

        Label titulo = new Label(esEdicion ? "Editar Doctor" : "Registrar Nuevo Doctor");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 18));
        titulo.setTextFill(Color.web(C_TEXTO));

        TextField txtNombre = crearCampoForm("Nombre completo");
        TextField txtEdad   = crearCampoForm("Edad");
        TextField txtCedula = crearCampoForm("Cédula");
        TextField txtEspec  = crearCampoForm("Especialidad");

        // Si es edición, prellenar los campos con los datos actuales
        if (esEdicion) {
            txtNombre.setText(doctorExistente.getNombreCompleto());
            txtEdad.setText(String.valueOf(doctorExistente.getEdad()));
            txtCedula.setText(doctorExistente.getCedula());
            txtEspec.setText(doctorExistente.getEspecialidad());
        }

        Label lblMensaje = new Label();
        lblMensaje.setTextFill(Color.web(C_ACENTO));
        lblMensaje.setFont(Font.font(12));

        Button btnGuardar = crearBotonAccion(esEdicion ? "💾  Actualizar" : "✅  Registrar", C_VERDE);
        btnGuardar.setMaxWidth(Double.MAX_VALUE);

        btnGuardar.setOnAction(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                int    edad   = Integer.parseInt(txtEdad.getText().trim());
                String cedula = txtCedula.getText().trim();
                String espec  = txtEspec.getText().trim();

                if (esEdicion) {
                    doctorService.actualizarDoctor(
                            doctorExistente.getId(), nombre, edad, cedula, espec);
                } else {
                    doctorService.registrarDoctor(nombre, edad, cedula, espec);
                }

                // Refrescar la lista en la tabla
                lista.setAll(doctorService.obtenerDoctores());
                modal.close();

            } catch (NumberFormatException ex) {
                lblMensaje.setText("La edad debe ser un número válido.");
            } catch (CustomException ex) {
                lblMensaje.setText(ex.getMessage());
            }
        });

        form.getChildren().addAll(
                titulo,
                new Label("Nombre:") {{ setTextFill(Color.web(C_SUAVE)); }},
                txtNombre,
                new Label("Edad:") {{ setTextFill(Color.web(C_SUAVE)); }},
                txtEdad,
                new Label("Cédula:") {{ setTextFill(Color.web(C_SUAVE)); }},
                txtCedula,
                new Label("Especialidad:") {{ setTextFill(Color.web(C_SUAVE)); }},
                txtEspec,
                lblMensaje,
                btnGuardar
        );

        modal.setScene(new Scene(form));
        modal.show();
    }

    /** Diálogo de confirmación antes de eliminar */
    private void confirmarEliminacion(Doctor doctor, ObservableList<Doctor> lista) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Eliminar al Dr. " + doctor.getNombreCompleto() + "?");
        alert.setContentText("Esta acción no se puede deshacer.");
        alert.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    doctorService.eliminarDoctorPorCedula(doctor.getCedula());
                    lista.setAll(doctorService.obtenerDoctores());
                } catch (CustomException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            }
        });
    }

    // ────────────────────────────────────────────────────────────
    //  SECCIÓN 3: GESTIÓN DE PACIENTES
    // ────────────────────────────────────────────────────────────
    private void mostrarGestionPacientes() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(35));
        layout.setStyle("-fx-background-color: " + C_FONDO + ";");

        layout.getChildren().add(crearTituloSeccion("Gestión de Pacientes", "Ver y eliminar pacientes"));

        TableView<Patient> tabla = new TableView<>();
        tabla.setStyle(estiloTabla());
        tabla.setPlaceholder(new Label("No hay pacientes registrados."));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        TableColumn<Patient, String> colNombre  = new TableColumn<>("Nombre");
        TableColumn<Patient, String> colCedula  = new TableColumn<>("Cédula");
        TableColumn<Patient, String> colEmail   = new TableColumn<>("Email");
        TableColumn<Patient, String> colTel     = new TableColumn<>("Teléfono");
        TableColumn<Patient, Integer>colEdad    = new TableColumn<>("Edad");
        TableColumn<Patient, Void>   colAccion  = new TableColumn<>("Acciones");

        colNombre.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().name()));
        colCedula.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().id()));
        colEmail.setCellValueFactory(d  -> new javafx.beans.property.SimpleStringProperty(d.getValue().email()));
        colTel.setCellValueFactory(d    -> new javafx.beans.property.SimpleStringProperty(d.getValue().phone()));
        colEdad.setCellValueFactory(d   -> new javafx.beans.property.SimpleIntegerProperty(d.getValue().age()).asObject());

        colNombre.setPrefWidth(160);
        colCedula.setPrefWidth(120);
        colEmail.setPrefWidth(200);
        colTel.setPrefWidth(130);
        colEdad.setPrefWidth(70);
        colAccion.setPrefWidth(110);

        ObservableList<Patient> datos = FXCollections.observableArrayList(PatientRepository.getPatientList());

        colAccion.setCellFactory(col -> new TableCell<>() {
            private final Button btnEliminar = crearBotonAccion("🗑️ Eliminar", C_ACENTO);
            { btnEliminar.setFont(Font.font(11)); btnEliminar.setPadding(new Insets(5,10,5,10)); }

            {
                btnEliminar.setOnAction(e -> {
                    Patient p = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "¿Eliminar a " + p.name() + "?", ButtonType.OK, ButtonType.CANCEL);
                    alert.showAndWait().ifPresent(r -> {
                        if (r == ButtonType.OK) {
                            PatientRepository.removeByName(p.name());
                            datos.setAll(PatientRepository.getPatientList());
                        }
                    });
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnEliminar);
            }
        });

        tabla.getColumns().addAll(colNombre, colCedula, colEmail, colTel, colEdad, colAccion);
        tabla.setItems(datos);

        layout.getChildren().add(tabla);

        ScrollPane scroll = new ScrollPane(layout);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        cambiarContenido(scroll);
    }

    // ────────────────────────────────────────────────────────────
    //  HELPERS
    // ────────────────────────────────────────────────────────────

    /** Reemplaza el contenido central con animación de fade */
    private void cambiarContenido(Node nuevoContenido) {
        nuevoContenido.setOpacity(0);
        contenidoCentral.getChildren().setAll(nuevoContenido);
        FadeTransition ft = new FadeTransition(Duration.millis(300), nuevoContenido);
        ft.setToValue(1);
        ft.play();
    }

    private VBox crearTituloSeccion(String titulo, String subtitulo) {
        Label lTitulo = new Label(titulo);
        lTitulo.setFont(Font.font("System", FontWeight.BOLD, 28));
        lTitulo.setTextFill(Color.web(C_TEXTO));

        Label lSub = new Label(subtitulo);
        lSub.setFont(Font.font(14));
        lSub.setTextFill(Color.web(C_SUAVE));

        Rectangle linea = new Rectangle(50, 3);
        linea.setFill(Color.web(C_ACENTO));
        linea.setArcWidth(3);
        linea.setArcHeight(3);

        return new VBox(4, lTitulo, lSub, linea);
    }

    private Button crearBotonAccion(String texto, String color) {
        Button btn = new Button(texto);
        btn.setFont(Font.font("System", FontWeight.SEMI_BOLD, 13));
        String base  = "-fx-background-color: " + color + "33;" +
                "-fx-text-fill: " + color + ";" +
                "-fx-background-radius: 8;" +
                "-fx-border-color: " + color + ";" +
                "-fx-border-radius: 8;" +
                "-fx-border-width: 1;" +
                "-fx-padding: 8 18 8 18;" +
                "-fx-cursor: hand;";
        String hover = base.replace(color + "33", color + "55");
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
        return btn;
    }

    private TextField crearCampoForm(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
                "-fx-background-color: #0A0A1A;" +
                        "-fx-text-fill: " + C_TEXTO + ";" +
                        "-fx-prompt-text-fill: #455A64;" +
                        "-fx-border-color: " + C_BORDE + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 6;" +
                        "-fx-background-radius: 6;" +
                        "-fx-padding: 10 12 10 12;"
        );
        return tf;
    }

    /** CSS base para las tablas */
    private String estiloTabla() {
        return "-fx-background-color: " + C_CARD + ";" +
                "-fx-text-fill: " + C_TEXTO + ";" +
                "-fx-table-cell-border-color: " + C_BORDE + ";";
    }

    /** Aplica tema oscuro básico a los charts */
    private void aplicarEstiloChart(Chart chart) {
        chart.setStyle("-fx-background-color: " + C_CARD + "; -fx-background-radius: 14;");
        if (chart.lookup(".chart-title") != null) {
            chart.lookup(".chart-title").setStyle("-fx-text-fill: " + C_TEXTO + ";");
        }
    }
}
