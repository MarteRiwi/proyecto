package marte.src.com.sigp;

import marte.src.com.sigp.model.User;
import marte.src.com.sigp.service.LoginService;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LoginView {

    // ── Colores del tema MARTE ─────────────────────────────────
    // Paleta oscura con acentos en rojo-marsiano y blanco
    private static final String COLOR_FONDO_OSCURO  = "#0D0D1A"; // fondo muy oscuro
    private static final String COLOR_SUPERFICIE     = "#141428"; // tarjeta
    private static final String COLOR_ACENTO         = "#C0392B"; // rojo Marte
    private static final String COLOR_ACENTO_CLARO   = "#E74C3C"; // hover
    private static final String COLOR_TEXTO          = "#ECEFF1";
    private static final String COLOR_TEXTO_SUAVE    = "#90A4AE";
    private static final String COLOR_BORDE          = "#1E1E3A";

    private final Stage stage;
    private final LoginService loginService = new LoginService();

    public LoginView(Stage stage) {
        this.stage = stage;
    }

    /** Construye y muestra la escena de login. */
    public void mostrar() {
        // ── Raíz: StackPane para poder superponer capas ──────
        StackPane raiz = new StackPane();
        raiz.setStyle("-fx-background-color: " + COLOR_FONDO_OSCURO + ";");

        // Capa 1: Círculos decorativos de fondo (estética espacial)
        raiz.getChildren().add(crearFondoDecorativo());

        // Capa 2: Tarjeta central de login
        raiz.getChildren().add(crearTarjetaLogin());

        Scene escena = new Scene(raiz, 1100, 700);
        stage.setTitle("MARTE – Sistema de Gestión de Citas");
        stage.setScene(escena);
        stage.setResizable(false);
        stage.show();
    }

    // ────────────────────────────────────────────────────────────
    //  FONDO DECORATIVO: círculos translúcidos tipo "planetas"
    // ────────────────────────────────────────────────────────────
    private Pane crearFondoDecorativo() {
        Pane fondo = new Pane();
        fondo.setPrefSize(1100, 700);

        // Planeta grande (esquina sup-derecha)
        Circle planeta1 = new Circle(220);
        planeta1.setFill(Color.web("#1A0A0A", 0.5));
        planeta1.setStroke(Color.web(COLOR_ACENTO, 0.15));
        planeta1.setStrokeWidth(1.5);
        planeta1.setCenterX(950);
        planeta1.setCenterY(-80);
        fondo.getChildren().add(planeta1);

        // Planeta pequeño (esquina inf-izq)
        Circle planeta2 = new Circle(100);
        planeta2.setFill(Color.web("#0A0A2A", 0.6));
        planeta2.setStroke(Color.web("#3498DB", 0.2));
        planeta2.setStrokeWidth(1);
        planeta2.setCenterX(80);
        planeta2.setCenterY(640);
        fondo.getChildren().add(planeta2);

        // Línea horizontal decorativa
        Line linea = new Line(0, 350, 1100, 350);
        linea.setStroke(Color.web(COLOR_ACENTO, 0.05));
        linea.setStrokeWidth(1);
        fondo.getChildren().add(linea);

        // Animación de rotación suave al planeta grande
        RotateTransition rot = new RotateTransition(Duration.seconds(30), planeta1);
        rot.setByAngle(360);
        rot.setCycleCount(Animation.INDEFINITE);
        rot.play();

        return fondo;
    }

    // ────────────────────────────────────────────────────────────
    //  TARJETA DE LOGIN: formulario centrado
    // ────────────────────────────────────────────────────────────
    private VBox crearTarjetaLogin() {
        VBox tarjeta = new VBox(24);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setMaxWidth(420);
        tarjeta.setPadding(new Insets(50, 50, 50, 50));
        tarjeta.setStyle(
                "-fx-background-color: " + COLOR_SUPERFICIE + ";" +
                        "-fx-background-radius: 20;" +
                        "-fx-border-color: " + COLOR_BORDE + ";" +
                        "-fx-border-width: 1;" +
                        "-fx-border-radius: 20;"
        );

        // Efecto de sombra/glow rojizo
        DropShadow sombra = new DropShadow();
        sombra.setColor(Color.web(COLOR_ACENTO, 0.3));
        sombra.setRadius(40);
        sombra.setSpread(0.1);
        tarjeta.setEffect(sombra);

        // ── Logo / Título ─────────────────────────────────────
        Label titulo = new Label("♂ MARTE");
        titulo.setFont(Font.font("System", FontWeight.BOLD, 42));
        titulo.setTextFill(Color.web(COLOR_ACENTO));

        Label subtitulo = new Label("SISTEMA DE GESTIÓN DE CITAS");
        subtitulo.setFont(Font.font("System", FontWeight.NORMAL, 11));
        subtitulo.setTextFill(Color.web(COLOR_TEXTO_SUAVE));
        subtitulo.setStyle("-fx-letter-spacing: 3;");

        // Separador rojo
        Rectangle separador = new Rectangle(60, 3);
        separador.setFill(Color.web(COLOR_ACENTO));
        separador.setArcWidth(3);
        separador.setArcHeight(3);

        VBox encabezado = new VBox(6, titulo, subtitulo, separador);
        encabezado.setAlignment(Pos.CENTER);

        // ── Campos de formulario ──────────────────────────────
        Label lblEmail = crearEtiqueta("Usuario / Email");
        TextField txtEmail = crearCampoTexto("admin  o  usuario@correo.com");

        Label lblPass = crearEtiqueta("Contraseña");
        PasswordField txtPass = crearCampoPassword("••••••••");

        // ── Mensaje de error (oculto hasta que haya un fallo) ─
        Label lblError = new Label();
        lblError.setTextFill(Color.web(COLOR_ACENTO_CLARO));
        lblError.setFont(Font.font(13));
        lblError.setVisible(false);

        // ── Botón de ingreso ──────────────────────────────────
        Button btnIngresar = crearBotonPrimario("INGRESAR");

        // Acción del botón: autenticar y redirigir
        btnIngresar.setOnAction(e -> {
            String email = txtEmail.getText().trim();
            String pass  = txtPass.getText().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                mostrarError(lblError, "Por favor completa todos los campos.");
                return;
            }

            User user = loginService.authenticate(email, pass);

            if (user == null) {
                mostrarError(lblError, "Credenciales incorrectas. Intenta de nuevo.");
                // Animación de "shake" en la tarjeta para feedback visual
                animarError(tarjeta);
            } else {
                // Redirigir según el rol del usuario
                redirigirSegunRol(user);
            }
        });

        // También ingresar con Enter desde el campo de contraseña
        txtPass.setOnAction(e -> btnIngresar.fire());

        // ── Línea inferior con versión ────────────────────────
        Label lblVersion = new Label("v1.0  •  Hospital Marte  •  2025");
        lblVersion.setFont(Font.font(10));
        lblVersion.setTextFill(Color.web(COLOR_TEXTO_SUAVE));

        tarjeta.getChildren().addAll(
                encabezado,
                lblEmail, txtEmail,
                lblPass, txtPass,
                lblError,
                btnIngresar,
                lblVersion
        );

        // Animación de entrada: la tarjeta aparece desde abajo
        tarjeta.setTranslateY(40);
        tarjeta.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(600), tarjeta);
        fade.setToValue(1);
        TranslateTransition slide = new TranslateTransition(Duration.millis(600), tarjeta);
        slide.setToY(0);
        ParallelTransition entrada = new ParallelTransition(fade, slide);
        entrada.play();

        return tarjeta;
    }

    // ────────────────────────────────────────────────────────────
    //  REDIRECCIÓN según ROL
    // ────────────────────────────────────────────────────────────
    private void redirigirSegunRol(User user) {
        switch (user.role().toUpperCase()) {
            case "ADMIN"   -> new AdminDashboardView(stage, user).mostrar();
            case "DOCTOR"  -> new DoctorDashboardView(stage, user).mostrar();
            default        -> new Patientview(stage, user).mostrar();  // PATIENT
        }
    }

    // ────────────────────────────────────────────────────────────
    //  HELPERS: creadores de componentes con estilo
    // ────────────────────────────────────────────────────────────

    /** Etiqueta pequeña estilo "label de formulario" */
    private Label crearEtiqueta(String texto) {
        Label lbl = new Label(texto);
        lbl.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        lbl.setTextFill(Color.web(COLOR_TEXTO_SUAVE));
        return lbl;
    }

    /** Campo de texto con estilo oscuro */
    private TextField crearCampoTexto(String placeholder) {
        TextField tf = new TextField();
        tf.setPromptText(placeholder);
        tf.setMaxWidth(Double.MAX_VALUE);
        aplicarEstiloCampo(tf);
        return tf;
    }

    /** Campo de contraseña con estilo oscuro */
    private PasswordField crearCampoPassword(String placeholder) {
        PasswordField pf = new PasswordField();
        pf.setPromptText(placeholder);
        pf.setMaxWidth(Double.MAX_VALUE);
        aplicarEstiloCampo(pf);
        return pf;
    }

    /** Aplica el CSS oscuro a cualquier control de texto */
    private void aplicarEstiloCampo(Control campo) {
        campo.setStyle(
                "-fx-background-color: #0A0A1A;" +
                        "-fx-text-fill: " + COLOR_TEXTO + ";" +
                        "-fx-prompt-text-fill: #455A64;" +
                        "-fx-border-color: " + COLOR_BORDE + ";" +
                        "-fx-border-width: 1.5;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;" +
                        "-fx-padding: 12 15 12 15;" +
                        "-fx-font-size: 14;"
        );
        // Efecto hover: borde rojo al enfocar
        campo.focusedProperty().addListener((obs, antes, ahora) -> {
            if (ahora) {
                campo.setStyle(campo.getStyle().replace(
                        "-fx-border-color: " + COLOR_BORDE + ";",
                        "-fx-border-color: " + COLOR_ACENTO + ";"
                ));
            } else {
                campo.setStyle(campo.getStyle().replace(
                        "-fx-border-color: " + COLOR_ACENTO + ";",
                        "-fx-border-color: " + COLOR_BORDE + ";"
                ));
            }
        });
    }

    /** Botón principal rojo */
    private Button crearBotonPrimario(String texto) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setFont(Font.font("System", FontWeight.BOLD, 14));
        String estiloBas = "-fx-background-color: " + COLOR_ACENTO + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 8;" +
                "-fx-padding: 14 0 14 0;" +
                "-fx-cursor: hand;";
        String estiloHover = estiloBas.replace(COLOR_ACENTO, COLOR_ACENTO_CLARO);
        btn.setStyle(estiloBas);
        btn.setOnMouseEntered(e -> btn.setStyle(estiloHover));
        btn.setOnMouseExited(e -> btn.setStyle(estiloBas));
        return btn;
    }

    /** Muestra un mensaje de error con animación de fade-in */
    private void mostrarError(Label lblError, String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        FadeTransition ft = new FadeTransition(Duration.millis(300), lblError);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    /** Animación de "shake" (temblor) cuando el login falla */
    private void animarError(VBox tarjeta) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), tarjeta);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }
}
