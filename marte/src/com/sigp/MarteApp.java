package marte.src.com.sigp;

import javafx.application.Application;
import javafx.stage.Stage;

public class MarteApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // El primer paso siempre es la pantalla de login
        // El login decide a qué panel ir según el rol del usuario
        new LoginView(primaryStage).mostrar();
    }

    public static void main(String[] args) {
        // launch() inicializa el toolkit de JavaFX y llama a start()
        launch(args);
    }
}

/*
 * ═══════════════════════════════════════════════════════════════
 *  README – Cómo integrar JavaFX en tu proyecto MARTE
 * ═══════════════════════════════════════════════════════════════
 *
 *  OPCIÓN A: IntelliJ IDEA (recomendado)
 *  ──────────────────────────────────────
 *  1. Descarga JavaFX SDK desde https://openjfx.io
 *     (elige la versión compatible con tu JDK, ej: JavaFX 21 para JDK 21)
 *
 *  2. En IntelliJ: File → Project Structure → Libraries
 *     → ➕ Java → selecciona la carpeta lib/ del SDK de JavaFX
 *
 *  3. En Run/Debug Configurations → VM options, agrega:
 *     --module-path "/ruta/al/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml
 *
 *     Ejemplo en Windows:
 *     --module-path "C:/javafx-sdk-21/lib" --add-modules javafx.controls,javafx.fxml
 *
 *  4. Cambia la clase principal de com.sigp.Main a com.sigp.ui.MarteApp
 *
 *
 *  OPCIÓN B: Con Maven (pom.xml)
 *  ──────────────────────────────
 *  Agrega en <dependencies>:
 *
 *    <dependency>
 *      <groupId>org.openjfx</groupId>
 *      <artifactId>javafx-controls</artifactId>
 *      <version>21</version>
 *    </dependency>
 *
 *  Y en <plugins>:
 *
 *    <plugin>
 *      <groupId>org.openjfx</groupId>
 *      <artifactId>javafx-maven-plugin</artifactId>
 *      <version>0.0.8</version>
 *      <configuration>
 *        <mainClass>com.sigp.ui.MarteApp</mainClass>
 *      </configuration>
 *    </plugin>
 *
 *
 *  ESTRUCTURA DE ARCHIVOS UI (copia estos 5 archivos):
 *  ─────────────────────────────────────────────────────
 *  src/com/sigp/ui/
 *    ├── MarteApp.java              ← clase principal (este archivo)
 *    ├── LoginView.java             ← pantalla de login
 *    ├── AdminDashboardView.java    ← panel del administrador
 *    ├── DoctorDashboardView.java   ← panel del doctor
 *    └── PatientView.java           ← panel del paciente
 *
 *
 *  RUTA DE DATOS (ya configurada en PersistenceManager.java):
 *  ────────────────────────────────────────────────────────────
 *  Los archivos .txt ya están en:
 *  C:\Users\Administrador\Desktop\proyecto\data\
 *
 *  El PersistenceManager original ya busca ../data o data
 *  así que no necesitas cambiar nada en él.
 *
 *
 *  CREDENCIALES DE PRUEBA (de usuarios.txt):
 *  ───────────────────────────────────────────
 *  Admin:    usuario = admin          contraseña = admin123
 *  Doctor:   usuario = email@doc.com  (según tu archivo)
 *  Paciente: usuario = email@pac.com  (según tu archivo)
 *
 * ═══════════════════════════════════════════════════════════════
 */
